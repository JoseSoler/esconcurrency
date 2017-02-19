package com.genesystems;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class App {

    public static final String INDEX = "variant_bio";
    public static final String TYPE = "VariantBio_T1";
    public static final String VARIANT_ID = "chr1_45331218:TTCC>T";

    public static Client client;
    public static ExecutorService executor = Executors.newSingleThreadExecutor();
    //public static ExecutorService executor = Executors.newFixedThreadPool(4);


    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {

        client = connect();
        initVariant();

        printVariant("Initial Sate: ");

        long startTime = System.nanoTime();

        //start workers
        UpdateStrategy updateStrategy = new UnsafeUpdateStrategy();

        executor.submit(new VariantWorker(client, VARIANT_ID, "VP001", updateStrategy));
        executor.submit(new VariantWorker(client, VARIANT_ID, "VP002", updateStrategy));
        executor.submit(new VariantWorker(client, VARIANT_ID, "VP003", updateStrategy));
        executor.submit(new VariantWorker(client, VARIANT_ID, "VP004", updateStrategy));
        executor.submit(new VariantWorker(client, VARIANT_ID, "VP005", updateStrategy));
        executor.submit(new VariantWorker(client, VARIANT_ID, "VP006", updateStrategy));
        executor.submit(new VariantWorker(client, VARIANT_ID, "VP007", updateStrategy));
        executor.submit(new VariantWorker(client, VARIANT_ID, "VP008", updateStrategy));

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime);

        printVariant("Final State: ");

        System.out.print("Total time: " + duration / 1000000000 + " seconds");

        shutdown();
    }

    private static void initVariant() throws IOException {
        Variant variant = new Variant();
        variant.setId(VARIANT_ID);

        ObjectMapper objectMapper = new ObjectMapper();

        IndexResponse response = client.prepareIndex(INDEX, TYPE, VARIANT_ID)
                .setSource(objectMapper.writeValueAsString(variant))
                .get();

    }

    private static Client connect() throws UnknownHostException {
        return TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));

    }

    private static void shutdown() {
        client.close();
    }

    private static void printVariant(String message) {
        GetResponse response = client.prepareGet(INDEX, TYPE, VARIANT_ID).get();
        System.out.println(message + response.getSourceAsString());
    }
}

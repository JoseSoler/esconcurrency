package com.genesystems;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class VariantWorker implements Runnable {

    private ObjectMapper mapper = new ObjectMapper();
    private Random random = new Random();

    private Client client;
    private String docId;
    private String sampleName;
    private UpdateStrategy updateStrategy;

    public VariantWorker(Client client, String docId, String sample, UpdateStrategy updateStrategy) {
        this.client = client;
        this.docId = docId;
        this.sampleName = sample;
        this.updateStrategy = updateStrategy;
    }

    public void run() {

        try {
            int tryies = 1;

            while (!applyMergeLogic()) {
                System.out.println(Thread.currentThread().getName()
                        + " - " + tryies + " attempt(s) to update variant");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Boolean applyMergeLogic() throws IOException, InterruptedException {

        GetResponse response = client.prepareGet(App.INDEX, App.TYPE, docId).get();
        Variant variant = mapper.readValue(response.getSourceAsString(), Variant.class);

        Sample sample = new Sample();
        sample.setId(sampleName);
        List<Sample> samples = variant.getSamples();
        samples.add(sample);

        wasteRandomTime();

        try {
            updateStrategy.update(client, variant, response.getVersion());
            System.out.println(Thread.currentThread().getName()
                    + " ----> Successfully updated the variant.");
            return true;
        } catch (ExecutionException ex) {
            System.out.println(Thread.currentThread().getName()
                    + " xxxxx> Failed to update an outdated variant.");
            return false;
        }

    }

    private void wasteRandomTime() {
        try {
            int secs = random.nextInt(21) * 1000;
            System.out.println(Thread.currentThread().getName() +
                    " - Applying merge logic for " + secs / 1000 + " secs");
            Thread.sleep(secs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

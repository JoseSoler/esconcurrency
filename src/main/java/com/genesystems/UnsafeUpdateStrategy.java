package com.genesystems;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;

import java.util.concurrent.ExecutionException;

public class UnsafeUpdateStrategy implements UpdateStrategy {

    private ObjectMapper mapper = new ObjectMapper();

    public void update(Client client, Variant variant, Long version) throws JsonProcessingException,
            ExecutionException, InterruptedException {

        //Ignoring version deliberately

        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index(App.INDEX);
        updateRequest.type(App.TYPE);
        updateRequest.id(App.VARIANT_ID);
        updateRequest.doc(mapper.writeValueAsString(variant));
        client.update(updateRequest).get();
    }
}

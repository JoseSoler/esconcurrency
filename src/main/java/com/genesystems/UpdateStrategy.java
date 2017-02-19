package com.genesystems;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.client.Client;

import java.util.concurrent.ExecutionException;

public interface UpdateStrategy {

    void update(Client client, Variant variant, Long version) throws JsonProcessingException,
            ExecutionException, InterruptedException;
}

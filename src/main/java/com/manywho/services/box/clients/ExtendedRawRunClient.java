package com.manywho.services.box.clients;

import com.manywho.sdk.client.raw.RawRunClient;
import com.manywho.services.box.entities.InitializationRequest;
import org.apache.http.client.methods.HttpPost;

import javax.ws.rs.core.MediaType;
import java.util.UUID;

public class ExtendedRawRunClient extends RawRunClient{
    private static String BASE_URL = "https://flow.manywho.com/api/run/1";

    public String authentication(UUID stateId, UUID tenant, InitializationRequest initializationRequest) {
        HttpPost request = new HttpPost(String.format("%s/authentication/%s", BASE_URL, stateId));
        request.addHeader("ManyWhoTenant", tenant.toString());
        request.addHeader("Content-Type", MediaType.APPLICATION_JSON);
        request.setEntity(createEntity(initializationRequest));

        return executeWithResponse(request, String.class);
    }
}

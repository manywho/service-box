package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class CreateFolderControllerTest extends BoxServiceFunctionalTest{

    @Test
    public void testCreateFolder() throws Exception {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("folder-create/box-response/create-folder-response.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("folder-create/box-response/items/items-for-folder.json", 200));

        Response responseMsg = target("/folder/create").request()
                .headers(headers)
                .post(getServerRequestFromFile("folder-create/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("folder-create/response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}

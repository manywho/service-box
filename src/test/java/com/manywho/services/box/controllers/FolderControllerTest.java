package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class FolderControllerTest extends BoxServiceFunctionalTest{
    @Test
    public void testCreateFolder() throws Exception {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("folder-create/box-response/create-folder-response.json", 200));

        Response responseMsg = target("/folder/create").request()
                .headers(headers)
                .post(getServerRequestFromFile("folder-create/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("folder-create/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(1, requestIntersectorTests.executedCalls());
    }
}

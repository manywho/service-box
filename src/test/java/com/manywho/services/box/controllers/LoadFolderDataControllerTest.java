package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;

public class LoadFolderDataControllerTest extends BoxServiceFunctionalTest {

    @Test
    public void testLoadFolder() throws IOException, URISyntaxException {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("folder-load/box-response/folder.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("folder-load/box-response/items-in-folder.json", 200));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .post(getObjectDataRequestFromFile("folder-load/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("folder-load/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(2, requestIntersectorTests.executedCalls());
    }
}

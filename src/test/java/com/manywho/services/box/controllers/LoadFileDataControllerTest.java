package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class LoadFileDataControllerTest extends BoxServiceFunctionalTest {

    @Test
    public void testLoadFile() throws Exception {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("file-load/box-response/file.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("file-load/box-response/folders.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("file-load/box-response/files.json", 200));
        Response responseMsg = target("/data").request()
                .headers(headers)
                .post(getObjectDataRequestFromFile("file-load/request.json"));

        assertJsonSame(
                getJsonFormatFileContent("file-load/response.json"),
                getJsonFormatResponse(responseMsg)
        );
    }
}

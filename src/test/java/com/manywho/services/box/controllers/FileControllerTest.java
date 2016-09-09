package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;

public class FileControllerTest extends BoxServiceFunctionalTest {

    @Test
    public void testCopyFile() throws IOException, URISyntaxException {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("file-copy/box-response/copied-file.json", 200));

        Response responseMsg = target("/file/copy").request()
                .headers(headers)
                .post(getServerRequestFromFile("file-copy/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("file-copy/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(1, requestIntersectorTests.executedCalls());
    }

    @Test
    public void testMoveFile() throws IOException, URISyntaxException {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("file-move/box-response/moved-file.json", 200));

        Response responseMsg = target("/file/move").request()
                .headers(headers)
                .post(getServerRequestFromFile("file-move/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("file-move/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(1, requestIntersectorTests.executedCalls());
    }
}

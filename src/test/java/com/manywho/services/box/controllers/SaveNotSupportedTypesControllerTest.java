package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class SaveNotSupportedTypesControllerTest extends BoxServiceFunctionalTest{

    @Test
    public void testSaveFile() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .put(getObjectDataRequestFromFile("data-save/not-supported/file-save-request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("data-save/not-supported/file-save-response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(0, requestIntersectorTests.executedCalls());
    }

    @Test
    public void testSaveFileSys() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .put(getObjectDataRequestFromFile("data-save/not-supported/filesys-save-request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("data-save/not-supported/filesys-save-response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(0, requestIntersectorTests.executedCalls());
    }

    @Test
    public void testSaveComment() throws Exception {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .put(getObjectDataRequestFromFile("data-save/not-supported/filesys-save-request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("data-save/not-supported/filesys-save-response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(0, requestIntersectorTests.executedCalls());
    }
}

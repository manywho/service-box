package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class SaveDataTaskControllerTest extends BoxServiceFunctionalTest{

    @Test
    public void testCreateTask() throws Exception {
        // https://api.box.com/2.0/tasks
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-save/task/box-response/task-created.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-save/task/box-response/file.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-save/task/box-response/comments-of-file.json", 200));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .put(getObjectDataRequestFromFile("data-save/task/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("data-save/task/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(3, requestIntersectorTests.executedCalls());
    }
}

package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class TaskControllerTest extends BoxServiceFunctionalTest{

    @Test
    public void testCreateTask() throws Exception {
        // https://api.box.com/2.0/tasks
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("task-create/box-response/task-created.json", 200));

        Response responseMsg = target("/task/create").request()
                .headers(headers)
                .post(getServerRequestFromFile("task-create/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("task-create/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(1, requestIntersectorTests.executedCalls());
    }
}

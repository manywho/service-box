package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class TaskAssignmentControllerTest extends BoxServiceFunctionalTest {
    @Test
    public void testCreateTaskAssignment() throws Exception {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("taskassignment-create/box-response/taskassignment-created.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("taskassignment-create/box-response/file.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("taskassignment-create/box-response/comments-of-file.json", 200));

        Response responseMsg = target("/task/addassignment").request()
                .headers(headers)
                .post(getServerRequestFromFile("taskassignment-create/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("taskassignment-create/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(3, requestIntersectorTests.executedCalls());
    }
}

package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.json.JSONException;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertSame;

public class LoadDataControllerTest extends BoxServiceFunctionalTest {

    @Test
    public void testLoadFolder() throws IOException, URISyntaxException, JSONException {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-load/folder-load/box-response/folder.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-load/folder-load/box-response/items-in-folder.json", 200));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .post(getObjectDataRequestFromFile("data-load/folder-load/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("data-load/folder-load/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(2, requestIntersectorTests.executedCalls());
    }

    @Test
    public void testLoadFile() throws Exception {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-load/file-load/box-response/file.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-load/file-load/box-response/parent-folder-of-file.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-load/file-load/box-response/comments-of-file.json", 200));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .post(getObjectDataRequestFromFile("data-load/file-load/request.json"));

        assertJsonSame(
                getJsonFormatFileContent("data-load/file-load/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertSame(3,requestIntersectorTests.executedCalls());
    }

    @Test
    public void testLoadTask() throws Exception {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-load/task-load/box-response/task.json", 200));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .post(getObjectDataRequestFromFile("data-load/task-load/request.json"));

        assertJsonSame(
                getJsonFormatFileContent("data-load/task-load/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertSame(1, requestIntersectorTests.executedCalls());
    }

    @Test
    public void testLoadTaskAssignment() throws Exception {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-load/taskassignment-load/box-response/taskassignment.json", 200));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .post(getObjectDataRequestFromFile("data-load/taskassignment-load/request.json"));

        assertJsonSame(
                getJsonFormatFileContent("data-load/taskassignment-load/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertSame(1, requestIntersectorTests.executedCalls());
    }
}

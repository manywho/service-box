package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class DescribeControllerTest extends BoxServiceFunctionalTest{

    @Test
    public void testDescribeServiceResponse() throws Exception {

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("metadata/box-response/metadata.json", 200));

        Response responseMsg = target("/metadata").request()
                .headers(headers)
                .post(getServerRequestFromFile("metadata/metadata-request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("metadata/metadata-response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(1, requestIntersectorTests.executedCalls());
    }
}

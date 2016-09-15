package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Ignore;
import org.junit.Test;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertEquals;

public class AssignFlowControllerTest extends BoxServiceFunctionalTest {

    @Test
    public void testAssignFlowToWebhook() throws IOException, URISyntaxException {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("flow-assignment/box-response/create-webhook.json", 200));
        String userCredentials = "service:box:user:123456789:credentials";
        String userTokenKey = "service:box:user:token:12345678";
        String flowHeaderKey = "service:box:box-userid:123456789:flow-auth-header";

        mockJedis.set(userCredentials, getJsonFormatFileContent("flow-assignment/db/credentials.json"));

        Response responseMsg = target("/trigger-assign/flow").request()
                .headers(headers)
                .post(getServerRequestFromFile("flow-assignment/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("flow-assignment/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals("\"123456789\"", mockJedis.get(userTokenKey));
        assertJsonSame(getJsonFormatFileContent("flow-assignment/db/credentials-after-action.json"),
                mockJedis.get(userCredentials));

        assertEquals(1, requestIntersectorTests.executedCalls());
        assertNotNull(mockJedis.get(flowHeaderKey));
    }


    protected static AuthenticatedWho getAuthenticatedWho() {
        AuthenticatedWho authenticatedWho = new AuthenticatedWho();
        authenticatedWho.setManyWhoTenantId("67204d5c-6022-474d-8f80-0d576b43d02d");
        authenticatedWho.setManyWhoUserId("52df1a90-3826-4508-b7c2-cde8aa5b72cf");
        authenticatedWho.setManyWhoToken("the-token");
        authenticatedWho.setDirectoryId("Directory1");
        authenticatedWho.setDirectoryName("Directory1");
        authenticatedWho.setEmail("admin@manywho.com");
        authenticatedWho.setIdentityProvider("NONE");
        authenticatedWho.setTenantName("UNKNOWN");
        authenticatedWho.setToken("12345678");
        authenticatedWho.setUsername("");
        authenticatedWho.setUserId("123456789");
        authenticatedWho.setFirstName("");
        authenticatedWho.setLastName("");
        return authenticatedWho;
    }
}

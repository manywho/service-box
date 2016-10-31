package com.manywho.services.box.controllers;

import com.google.common.io.Resources;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.when;

public class AuthorizationControllerTest extends BoxServiceFunctionalTest {

    @Test
    public void testUserMembershipRestriction() throws IOException, URISyntaxException {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        AuthenticatedWho authenticatedWho = getDefaultAuthenticatedWho();
        authenticatedWho.setUserId("123456789");
        headers.add("Authorization", AuthorizationUtils.serialize(authenticatedWho));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("authorization/user-group-member/box-response/me.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("authorization/user-group-member/box-response/full-user.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("authorization/user-group-member/box-response/membership.json", 200));

        Response responseMsg = target("/authorization").request().headers(headers)
                .post(getObjectDataRequestFromFile("authorization/user-group-member/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("authorization/user-group-member/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(3, requestIntersectorTests.executedCalls());
    }

    @Test
    public void testUserNotMembershipRestriction() throws IOException, URISyntaxException {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        AuthenticatedWho authenticatedWho = getDefaultAuthenticatedWho();
        authenticatedWho.setUserId("123456789");
        headers.add("Authorization", AuthorizationUtils.serialize(authenticatedWho));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("authorization/user-no-group-member/box-response/me.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("authorization/user-no-group-member/box-response/full-user.json", 200));

        //the group doesn't have permissions
        requestIntersectorTests.addApiResponse(createBoxApiResponse("authorization/user-no-group-member/box-response/membership.json", 200));

        Response responseMsg = target("/authorization").request().headers(headers)
                .post(getObjectDataRequestFromFile("authorization/user-no-group-member/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("authorization/user-no-group-member/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(3, requestIntersectorTests.executedCalls());
    }

    @Test
    public void listGroups() throws IOException, URISyntaxException {
        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        AuthenticatedWho authenticatedWho = getDefaultAuthenticatedWho();
        authenticatedWho.setUserId("123456789");
        headers.add("Authorization", AuthorizationUtils.serialize(authenticatedWho));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("authorization/list-groups/box-response/authorized-groups.json", 200));

        when(mockSecurityConfiguration.getPrivateKeyLocation()).thenReturn(Resources.getResource("authorization/list-groups/request.json").getPath());
        Response responseMsg = target("/authorization/group").request().headers(headers)
                .post(getObjectDataRequestFromFile("authorization/list-groups/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("authorization/list-groups/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(0, requestIntersectorTests.executedCalls());
    }

}

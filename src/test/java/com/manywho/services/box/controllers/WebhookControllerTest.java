package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.EngineInitializationResponse;
import com.manywho.sdk.entities.run.EngineInvokeResponse;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import com.manywho.services.box.test.FlowResponseMock;
import com.manywho.services.box.test.HttpClientUnirestForTest;
import com.mashape.unirest.http.Unirest;
import org.json.JSONException;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class WebhookControllerTest extends BoxServiceFunctionalTest {

    /**
     * pre-condition:
     *  A flow have created a listener for a file using trigger FILE.DOWNLOADED
     *  that create a webhook in box,
     *  save authenticatedwho in database
     *  save a listener-request in database
     *  save user credentials for box n database
     *  save the webhook in database
     *
     *  after a user download the file, that tirgger the webhook
     *
     *  test:
     *  the webhook is removed from database and box. the flow continue
     *
     *  post-condition:
     *  clean database
     */
    @Test
    public void testFileDownloadedOnlyOneListenerRequest() throws IOException, URISyntaxException, JSONException {
        String authenticatedWhoKey = "service:box:autenticatedwho:webhook:177326:state:dd56ef5e-7ec4-4902-9db5-e7ad74c7ff53";
        String listenerRequestKey = "service:box:listener-request:webhook:177326:trigger:FILE.DOWNLOADED:state:dd56ef5e-7ec4-4902-9db5-e7ad74c7ff53";
        String userkey = "service:box:user:328136111:credentials";
        String webhookKey = "service:box:webhook:targettype:FILE:targetid:94205724383";

        // database

        mockJedis.set(authenticatedWhoKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/authenticatedwho.json"));

        mockJedis.set(listenerRequestKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/listener-request-download.json"));

        mockJedis.set(userkey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/user.json"));

        mockJedis.set(webhookKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/webhook.json"));

        // box Responses

        //get user id https://api.box.com/2.0/users/me
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/user-me.json", 200));

        //get triggered file https://api.box.com/2.0/files/94205724383?fields=type%2Cid%2Csequence_id%2Cetag%2Csha1%2Cname%2Cdescription%2Csize%2Cpath_collection%2Ccreated_at%2Cmodified_at%2Ctrashed_at%2Cpurged_at%2Ccontent_created_at%2Ccontent_modified_at%2Ccreated_by%2Cmodified_by%2Cowned_by%2Cshared_link%2Cparent%2Citem_status%2Cversion_number%2Ccomment_count%2Cpermissions%2Ctags%2Clock%2Cextension%2Cis_package%2Cfile_version
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/file.json", 200));

        //https://api.box.com/2.0/folders/0/items/?limit=1000&offset=0
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/folder-items.json", 200));

        //get comments for file https://api.box.com/2.0/files/94205724383/comments
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/comments.json", 200));

        //DELETE webhook https://api.box.com/2.0/webhooks/177326
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/delete.json", 200));

        HttpClientUnirestForTest httpClientMock = new HttpClientUnirestForTest();
        Unirest.setHttpClient(httpClientMock);

        // I will do to calls to the flow to know the status
        FlowResponseMock httpResponse = new FlowResponseMock();
        httpClientMock.addResponse(httpResponse);

        when(verifier.verify(any(), any(),any(), any(), any(), any())).thenReturn(true);

        Response responseMsg = target("/webhook/callback").request()
                .post(getWebhookFromFile("webhooks/file-downloaded/payload/payload.json"));

        verify(verifier, times(1)).verify(any(),any(),any(),any(),any(), any());

        // how many box calls
        assertSame(5, requestIntersectorTests.executedCalls());

        // how many engine calls
        assertSame(1, httpClientMock.getResponsesHistory().size());

        //check database status
        assertNull(mockJedis.get(webhookKey));
        assertNull(mockJedis.get(listenerRequestKey));
        assertNull(mockJedis.get(authenticatedWhoKey));
        // we don't delete the user credentials
        assertNotNull(mockJedis.get(userkey));
    }

    /**
     * pre-condition:
     *  A flow have created a listener for a file using trigger FILE.DOWNLOADED, and another flow have created a lister
     *  for  the same file using trigger FILE.PREVIEWED
     *
     *  save authenticatedwho in database
     *  save a listener-request in database
     *  save user credentials for box In database
     *  save the webhook in database
     *
     *  after a user download the file, that tirgger the webhook
     *
     *  test:
     *  the webhook is removed from database and box. the flow continue
     *
     *  post-condition:
     *  the trigger still in database
     *  the listener request is deleted from database
     */
    @Test
    public void testFileDownloadedPlusFiledPreviewListenerRequest() throws IOException, URISyntaxException, JSONException {
        String authenticatedWhoKey = "service:box:autenticatedwho:webhook:177326:state:dd56ef5e-7ec4-4902-9db5-e7ad74c7ff53";
        String listenerRequestDownloadKey = "service:box:listener-request:webhook:177326:trigger:FILE.DOWNLOADED:state:dd56ef5e-7ec4-4902-9db5-e7ad74c7ff53";
        String listenerRequestPreviewKey = "service:box:listener-request:webhook:177326:trigger:FILE.PREVIEWED:state:dd56ef5e-7ec4-4902-9db5-123456789";
        String userkey = "service:box:user:328136111:credentials";
        String webhookKey = "service:box:webhook:targettype:FILE:targetid:94205724383";

        // database

        mockJedis.set(authenticatedWhoKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/authenticatedwho.json"));

        mockJedis.set(listenerRequestDownloadKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/listener-request-download.json"));

        mockJedis.set(listenerRequestPreviewKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/listener-request-preview.json"));

        mockJedis.set(userkey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/user.json"));

        mockJedis.set(webhookKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/webhook.json"));

        // box Responses

        //get user id https://api.box.com/2.0/users/me
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/user-me.json", 200));

        //get triggered file https://api.box.com/2.0/files/94205724383?fields=type%2Cid%2Csequence_id%2Cetag%2Csha1%2Cname%2Cdescription%2Csize%2Cpath_collection%2Ccreated_at%2Cmodified_at%2Ctrashed_at%2Cpurged_at%2Ccontent_created_at%2Ccontent_modified_at%2Ccreated_by%2Cmodified_by%2Cowned_by%2Cshared_link%2Cparent%2Citem_status%2Cversion_number%2Ccomment_count%2Cpermissions%2Ctags%2Clock%2Cextension%2Cis_package%2Cfile_version
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/file.json", 200));

        //https://api.box.com/2.0/folders/0/items/?limit=1000&offset=0
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/folder-items.json", 200));

        //get comments for file https://api.box.com/2.0/files/94205724383/comments
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/comments.json", 200));

        HttpClientUnirestForTest httpClientMock = new HttpClientUnirestForTest();
        Unirest.setHttpClient(httpClientMock);

        // I will do to calls to the flow to know the status
        FlowResponseMock httpResponse = new FlowResponseMock();
        httpClientMock.addResponse(httpResponse);


        when(verifier.verify(any(), any(),any(), any(), any(), any())).thenReturn(true);

        Response responseMsg = target("/webhook/callback").request()
                .post(getWebhookFromFile("webhooks/file-downloaded/payload/payload.json"));

        verify(verifier, times(1)).verify(any(),any(),any(),any(),any(), any());

        // how many box calls
        assertSame(4, requestIntersectorTests.executedCalls());

        // how many engine calls
        assertSame(1, httpClientMock.getResponsesHistory().size());

        //check database status
        assertNotNull(mockJedis.get(webhookKey));
        assertNotNull(mockJedis.get(listenerRequestPreviewKey));
        assertNotNull(mockJedis.get(userkey));

        assertNull(mockJedis.get(authenticatedWhoKey));
        assertNull(mockJedis.get(listenerRequestDownloadKey));
    }

    /**
     * pre-condition:
     *  A flow have created a listener for a file using trigger FILE.DOWNLOADED
     *  that create a webhook in box,
     *  save authenticatedwho in database
     *  save a listener-request in database
     *  save user credentials for box n database
     *  save the webhook in database
     *
     *  The user Assign a flow for a file using trigger FILE.PREVIEWED
     *
     *  after a user preview the file, that tirgger the webhook, that is going to run the flow
     *
     *  test:
     *  the second flow is executed
     *
     *  post-condition:
     *  nothing is deleted from database
     */
    @Test
    public void testFileDownloadedFlowTriggeredAndOneListenerRequest() throws IOException, URISyntaxException, JSONException {
        String authenticatedWhoKey = "service:box:autenticatedwho:webhook:177326:state:dd56ef5e-7ec4-4902-9db5-e7ad74c7ff53";
        String listenerRequestKey = "service:box:listener-request:webhook:177326:trigger:FILE.DOWNLOADED:state:dd56ef5e-7ec4-4902-9db5-e7ad74c7ff53";
        String flowListenerRequestKey = "service:box:flow-listener-request:targettype:file:targetid:94205724383:trigger:FILE.PREVIEWED";
        String flowHeaderKey = "service:box:box-userid:328136111:flow-auth-header";

        String userkey = "service:box:user:328136111:credentials";
        String webhookKey = "service:box:webhook:targettype:FILE:targetid:94205724383";

        // database
        mockJedis.set(authenticatedWhoKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/authenticatedwho.json"));

        // create a listener for a type request
        mockJedis.set(listenerRequestKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/listener-request-download.json"));

        mockJedis.set(userkey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/user.json"));

        mockJedis.set(webhookKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/webhook.json"));

        //create a listener for a flow request
        mockJedis.set(flowListenerRequestKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/flow-listener-request-preview.json"));

        mockJedis.set(flowHeaderKey,
                getJsonFormatFileContent("webhooks/file-downloaded/database/authentication-header-for-flow.json"));

        // box Responses

        //get user id https://api.box.com/2.0/users/me
        requestIntersectorTests.addApiResponse(createBoxApiResponse("webhooks/file-downloaded/box-request/user-me.json", 200));

        EngineInitializationResponse engineInitializationResponse = mock(EngineInitializationResponse.class);
        Mockito.when(engineInitializationResponse.getStateId()).thenReturn("7f9f754a-799c-11e6-8b77-86f30ca893d3");

        httpClientApacheForTest.addResponse(engineInitializationResponse);
        httpClientApacheForTest.addResponse("12345");
        httpClientApacheForTest.addResponse(engineInitializationResponse);
        EngineInvokeResponse engineInvokeResponse = mock(EngineInvokeResponse.class);
        httpClientApacheForTest.addResponse(engineInvokeResponse);

        when(verifier.verify(any(), any(),any(), any(), any(), any())).thenReturn(true);

        Response responseMsg = target("/webhook/callback").request()
                .post(getWebhookFromFile("webhooks/file-downloaded/payload/payload-preview.json"));

        verify(verifier, times(1)).verify(any(),any(),any(),any(),any(), any());

        // how many box calls
        assertSame(1, requestIntersectorTests.executedCalls());

        // how many engine calls
        assertSame(4, httpClientApacheForTest.getResponsesHistory().size());

        //check database status
        assertNotNull(mockJedis.get(webhookKey));
        assertNotNull(mockJedis.get(listenerRequestKey));
        assertNotNull(mockJedis.get(authenticatedWhoKey));
        assertNotNull(mockJedis.get(flowListenerRequestKey));
        assertNotNull(mockJedis.get(userkey));
    }
}

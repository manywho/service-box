package com.manywho.services.box.controllers;

import com.manywho.services.box.test.BoxServiceFunctionalTest;
import com.manywho.services.box.test.FlowResponseMock;
import com.manywho.services.box.test.HttpClientForTest;
import com.mashape.unirest.http.Unirest;
import org.json.JSONException;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

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
     *  after a user download the file, thats tirggering the webhook
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
                getJsonFormatFileContent("webhooks/file-downloaded/database/listener-request.json"));

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

        HttpClientForTest httpClientMock = new HttpClientForTest();
        Unirest.setHttpClient(httpClientMock);

        // I will do to calls to the flow to know the status
        FlowResponseMock httpResponse = new FlowResponseMock();
        httpClientMock.addResponse(httpResponse);

        Response responseMsg = target("/webhook/callback").request()
                .post(getWebhookFromFile("webhooks/file-downloaded/payload/payload.json"));

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
}

package com.manywho.services.box.controllers;

import com.manywho.sdk.utils.AuthorizationUtils;
import com.manywho.services.box.test.BoxServiceFunctionalTest;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

public class SaveDataMetadataControllerTest extends BoxServiceFunctionalTest{

    @Test
    public void testSaveMetadata() throws Exception {
        // this test assume there is a metadata object in box called Contract with a property called
        // status that property in this test have value Signed

        MultivaluedMap<String,Object> headers = new MultivaluedHashMap<>();
        headers.add("Authorization", AuthorizationUtils.serialize(getDefaultAuthenticatedWho()));

        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-save/metadata/box-response/file-metadata.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-save/metadata/box-response/file-metadata.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-save/metadata/box-response/file-metadata-enterprise-contract.json", 200));
        requestIntersectorTests.addApiResponse(createBoxApiResponse("data-save/metadata/box-response/file-metadata-fields.json", 200));

        Response responseMsg = target("/data").request()
                .headers(headers)
                .put(getObjectDataRequestFromFile("data-save/metadata/request.json"));

        //check the response is right
        assertJsonSame(
                getJsonFormatFileContent("data-save/metadata/response.json"),
                getJsonFormatResponse(responseMsg)
        );

        assertEquals(4, requestIntersectorTests.executedCalls());
    }
}

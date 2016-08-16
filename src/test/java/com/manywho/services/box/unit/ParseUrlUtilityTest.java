package com.manywho.services.box.unit;


import com.manywho.services.box.utilities.ParseUrlUtility;
import org.junit.Test;

import java.net.URISyntaxException;

import static junit.framework.TestCase.assertEquals;

public class ParseUrlUtilityTest {

    @Test
    public void testGetTenant(){
        String tenantId = ParseUrlUtility.getTenantId("https://flow.manywho.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("67204d5c-6022-474d-8f80-0d576b43d02d", tenantId);
    }

    @Test
    public void testGetFlowId() throws URISyntaxException {
        String tenantId = ParseUrlUtility.getFlowId("https://flow.manywho.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc", tenantId);
    }

    @Test
    public void testGetFlowVersionId() throws URISyntaxException {
        String tenantId = ParseUrlUtility.getFlowVersionId("https://flow.manywho.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("ca20d743-af5d-4c93-9d40-2d2aca14fa94", tenantId);
    }
}

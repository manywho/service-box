package com.manywho.services.box.unit;


import com.manywho.services.box.utilities.ParseUrlUtility;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import java.net.URISyntaxException;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;

public class ParseUrlUtilityTest {

    @ClassRule
    public static ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testGetTenant() throws URISyntaxException {
        String tenantId = ParseUrlUtility.getTenantId("https://flow.manywho.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("67204d5c-6022-474d-8f80-0d576b43d02d", tenantId);

        String tenantIdBoomi = ParseUrlUtility.getTenantId("https://flow.boomi.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("67204d5c-6022-474d-8f80-0d576b43d02d", tenantIdBoomi);
    }

    @Test
    public void testGetFlowId() throws URISyntaxException {
        String tenantId = ParseUrlUtility.getFlowId("https://flow.manywho.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc", tenantId);

        String tenantIdBoomi = ParseUrlUtility.getFlowId("https://flow.boomi.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc", tenantIdBoomi);
    }

    @Test
    public void testGetFlowVersionId() throws URISyntaxException {
        String tenantId = ParseUrlUtility.getFlowVersionId("https://flow.manywho.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("ca20d743-af5d-4c93-9d40-2d2aca14fa94", tenantId);

        String tenantIdBoomi = ParseUrlUtility.getFlowVersionId("https://flow.boomi.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("ca20d743-af5d-4c93-9d40-2d2aca14fa94", tenantIdBoomi);
    }

    @Test
    public void testGetFlowVersionIdPublished() throws URISyntaxException {
        String tenantId = ParseUrlUtility.getFlowVersionId("https://flow.manywho.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc");
        assertNull(tenantId);

        String tenantIdBoomi = ParseUrlUtility.getFlowVersionId("https://flow.boomi.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc");
        assertNull(tenantIdBoomi);
    }

    @Test
    public void testNotFlowButValidUrl() throws URISyntaxException {
        assertNull(ParseUrlUtility.getFlowVersionId("https://flow.boomi.com"));
        assertNull(ParseUrlUtility.getFlowId("https://flow.boomi.com"));
        assertNull(ParseUrlUtility.getTenantId("https://flow.boomi.com"));

        assertNull(ParseUrlUtility.getFlowVersionId("https://flow.manywho.com"));
        assertNull(ParseUrlUtility.getFlowId("https://flow.manywho.com"));
        assertNull(ParseUrlUtility.getTenantId("https://flow.manywho.com"));
    }

    @Test
    public void testGetTenantIdWithoutProtocol() throws URISyntaxException {
        String tenantIdBoomi = ParseUrlUtility.getTenantId("flow.boomi.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=16e5dbdf-dab6-45ad-83b3-9b4d9e7f6cfc&flow-version-id=ca20d743-af5d-4c93-9d40-2d2aca14fa94");
        assertEquals("67204d5c-6022-474d-8f80-0d576b43d02d", tenantIdBoomi);
    }

    @Test
    public void testGetTenantIdWithUnexpectedFormath() throws URISyntaxException {
        assertNull(ParseUrlUtility.getTenantId("flow.boomi.com/unexpected-url"));
        assertNull(ParseUrlUtility.getFlowId("flow.boomi.com/unexpected-url"));
        assertNull(ParseUrlUtility.getFlowVersionId("flow.boomi.com/unexpected-url"));
    }

    @Test(expected = URISyntaxException.class)
    public void testGetFlowVersionNotValidUrl() throws URISyntaxException {
        ParseUrlUtility.getFlowVersionId("not valid url");
    }

    @Test(expected = URISyntaxException.class)
    public void testGetFlowIdNotValidUrl() throws URISyntaxException {
        ParseUrlUtility.getFlowId("not valid url");
    }

    @Test(expected = URISyntaxException.class)
    public void testGetTenantNotValidUrl() throws URISyntaxException {
       ParseUrlUtility.getTenantId("not valid url");
    }

    @Test(expected = RuntimeException.class)
    public void testGetFlowVersionNullUrl() throws URISyntaxException {
        ParseUrlUtility.getFlowVersionId(null);
    }

    @Test(expected = RuntimeException.class)
    public void testGetFlowIdNullUrl() throws URISyntaxException {
        ParseUrlUtility.getFlowId(null);
    }

    @Test(expected = RuntimeException.class)
    public void testGetTenantNullUrl() throws URISyntaxException {
        ParseUrlUtility.getTenantId(null);
    }
}

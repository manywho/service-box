package com.manywho.services.box.utilities;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

public class ParseUrlUtility {

    public static String getTenantId(String url) throws URISyntaxException {
        validateAndGetParameters(url);
        String[] parts = url.split("/play");
        String[] beforePlay = parts[0].split("/");

        if (beforePlay.length > 0) {
            String tenantId = beforePlay[beforePlay.length -1];

            try {
                if (tenantId.equals(UUID.fromString(tenantId).toString())) {
                    return tenantId;
                }
            } catch (Exception ex) {
                // it is not an uuid so this is not the tenant id
            }
        }

        return null;
    }

    public static String getFlowId(String url) throws URISyntaxException {
        return validateAndGetParameters(url)
                .stream().filter(param -> param.getName().equals("flow-id"))
                .findFirst()
                .map(NameValuePair::getValue)
                .orElse(null);
    }

    public static String getFlowVersionId(String url) throws URISyntaxException {
        return validateAndGetParameters(url)
                .stream().filter(param -> param.getName().equals("flow-version-id"))
                .findFirst()
                .map(NameValuePair::getValue)
                .orElse(null);
    }

    private static List<NameValuePair> validateAndGetParameters(String url) throws URISyntaxException {
        if (url == null) {
            throw new RuntimeException("The URL can not be null");
        }

        return URLEncodedUtils.parse(new URI(url), "UTF-8");
    }
}

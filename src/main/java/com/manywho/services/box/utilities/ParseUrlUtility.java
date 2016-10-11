package com.manywho.services.box.utilities;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ParseUrlUtility {

    public static String getTenantId(String s) {
        String[] parts = s.substring(25).split("/play");
        return parts[0];
    }

    public static String getFlowId(String s) throws URISyntaxException {
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(s), "UTF-8");
        return params.get(0).getValue();
    }

    public static String getFlowVersionId(String s) throws URISyntaxException {
        List<NameValuePair> params = URLEncodedUtils.parse(new URI(s), "UTF-8");
        if(params.size()>1) {
            return params.get(1).getValue();
        } else {
            return null;
        }
    }
}

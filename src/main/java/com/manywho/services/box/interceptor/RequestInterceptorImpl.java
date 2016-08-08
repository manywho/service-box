package com.manywho.services.box.interceptor;

import com.box.sdk.BoxAPIRequest;
import com.box.sdk.BoxAPIResponse;

/**
 * this class is used only for test purposes
 */
public class RequestInterceptorImpl implements com.box.sdk.RequestInterceptor {
    @Override
    public BoxAPIResponse onRequest(BoxAPIRequest request) {
        return null;
    }
}

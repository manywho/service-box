package com.manywho.services.box.facades;

import com.box.sdk.BoxAPIConnection;

public class BoxFacade implements BoxFacadeInterface {

    @Override
    public BoxAPIConnection createApiConnection(String clientId, String clientSecret, String authorizationCode) {
        return new BoxAPIConnection(clientId, clientSecret, authorizationCode);
    }

    @Override
    public BoxAPIConnection createApiConnection(String clientId, String clientSecret, String accessToken, String refreshToken) {
        return new BoxAPIConnection(clientId, clientSecret, accessToken, refreshToken);
    }
}

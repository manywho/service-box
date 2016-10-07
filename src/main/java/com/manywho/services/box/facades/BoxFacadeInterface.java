package com.manywho.services.box.facades;

import com.box.sdk.BoxAPIConnection;

public interface BoxFacadeInterface {
    public BoxAPIConnection createApiConnection(String clientId, String clientSecret, String authorizationCode);
    public BoxAPIConnection createApiConnection(String clientId, String clientSecret, String accessToken, String refreshToken);
}

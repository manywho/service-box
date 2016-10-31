package com.manywho.services.box.facades;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxDeveloperEditionAPIConnection;

public interface BoxFacadeInterface {
    BoxAPIConnection createApiConnection(String authorizationCode);
    BoxAPIConnection createApiConnection(String accessToken, String refreshToken);
    BoxDeveloperEditionAPIConnection createDeveloperApiConnection(String enterpriseId);
}

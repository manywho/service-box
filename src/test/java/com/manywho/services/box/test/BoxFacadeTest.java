package com.manywho.services.box.test;

import com.box.sdk.BoxAPIConnection;
import com.manywho.services.box.facades.BoxFacade;

public class BoxFacadeTest extends BoxFacade{
    /**
     *  Authentication that never expire
     *
     * @param clientId
     * @param clientSecret
     * @param authorizationCode
     * @return
     */
    @Override
    public BoxAPIConnection createApiConnection(String clientId, String clientSecret, String authorizationCode) {
        BoxAPIConnection boxAPIConnection = super.createApiConnection(clientId, clientSecret, authorizationCode);
        String jsonFormat = "{\"accessToken\": \"%s\",\"refreshToken\":\"%s\",\"lastRefresh\":%d, \"expires\":%d," +
                "\"userAgent\":\"%s\", \"tokenURL\":\"%s\", \"baseURL\":\"%s\",\"baseUploadURL\":\"%s\", " +
                "\"autoRefresh\": %b, \"maxRequestAttempts\": %d}";

        String json = String.format(jsonFormat, "1234", "12345", System.currentTimeMillis(), 90000000,
                "Mozilla", "abc1234", "http://wwww...", "http://wwww...", true, 1);

        boxAPIConnection.restore(json);

        return  boxAPIConnection;
    }

    /**
     * Authentication that never expire
     *
     * @param clientId
     * @param clientSecret
     * @param accessToken
     * @param refreshToken
     * @return
     */
    @Override
    public BoxAPIConnection createApiConnection(String clientId, String clientSecret, String accessToken, String refreshToken) {
        BoxAPIConnection boxAPIConnection = super.createApiConnection(clientId, clientSecret, accessToken, refreshToken);
        String jsonFormat = "{\"accessToken\": \"%s\",\"refreshToken\":\"%s\",\"lastRefresh\":%d, \"expires\":%d," +
                "\"userAgent\":\"%s\", \"tokenURL\":\"%s\", \"baseURL\":\"%s\",\"baseUploadURL\":\"%s\", " +
                "\"autoRefresh\": %b, \"maxRequestAttempts\": %d}";

        String json = String.format(jsonFormat, "1234", "12345", System.currentTimeMillis(), 90000000,
                "Mozilla", "abc1234", "http://wwww...", "http://wwww...", true, 1);

        boxAPIConnection.restore(json);

        return  boxAPIConnection;
    }
}

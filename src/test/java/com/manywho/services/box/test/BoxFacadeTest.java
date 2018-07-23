package com.manywho.services.box.test;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.facades.BoxFacade;
import com.manywho.services.box.services.TokenCacheService;
import com.manywho.services.box.utilities.SystemInteractionInterface;

import javax.inject.Inject;

public class BoxFacadeTest extends BoxFacade{

    private TokenCacheService tokenCacheService;
    private SystemInteractionInterface systemInteraction;

    @Inject
    public BoxFacadeTest(SecurityConfiguration securityConfiguration, TokenCacheService tokenCacheService,
                         SystemInteractionInterface systemInteraction) {
        super(securityConfiguration, tokenCacheService, systemInteraction);

        tokenCacheService = tokenCacheService;
        systemInteraction = systemInteraction;
    }

    /**
     *  Authentication that never expire
     *
     * @param authorizationCode
     * @return
     */
    @Override
    public BoxAPIConnection createApiConnection(String authorizationCode) {
        BoxAPIConnection boxAPIConnection = super.createApiConnection(authorizationCode);
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
     * @param accessToken
     * @param refreshToken
     * @return
     */
    @Override
    public BoxAPIConnection createApiConnection(String accessToken, String refreshToken) {
        BoxAPIConnection boxAPIConnection = super.createApiConnection(accessToken, refreshToken);
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
     * @return
     */
    @Override
    public BoxDeveloperEditionAPIConnection createDeveloperApiConnection(String enterpriseID) {
        BoxDeveloperEditionAPIConnection boxAPIConnection = super.createDeveloperApiConnection("aaaa");


        String jsonFormat = "{\"accessToken\": \"%s\",\"refreshToken\":\"%s\",\"lastRefresh\":%d, \"expires\":%d," +
                "\"userAgent\":\"%s\", \"tokenURL\":\"%s\", \"baseURL\":\"%s\",\"baseUploadURL\":\"%s\", " +
                "\"autoRefresh\": %b, \"maxRequestAttempts\": %d}";

        String json = String.format(jsonFormat, "1234", "12345", System.currentTimeMillis(), 90000000,
                "Mozilla", "abc1234", "http://wwww...", "http://wwww...", true, 1);

        boxAPIConnection.restore(json);

        return boxAPIConnection;
    }
}

package com.manywho.services.box.oauth2;

import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import org.scribe.model.OAuthConfig;

public class BoxProvider extends AbstractOauth2Provider {
    @Override
    public String getName() {
        return "Box";
    }

    @Override
    public String getClientId() {
        return "";
    }

    @Override
    public String getClientSecret() {
        return "";
    }

    @Override
    public String getRedirectUri() {
        return "https://flow.manywho.com/api/run/1/oauth2";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return null;
    }

    @Override
    public String getAuthorizationUrl(OAuthConfig config) {
        return String.format("https://app.box.com/api/oauth2/authorize?response_type=code&client_id=%s", config.getApiKey());
    }
}

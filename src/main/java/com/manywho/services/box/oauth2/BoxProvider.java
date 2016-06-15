package com.manywho.services.box.oauth2;

import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.configuration.SecurityConfiguration;
import org.scribe.model.OAuthConfig;

import javax.inject.Inject;

public class BoxProvider extends AbstractOauth2Provider {
    private final SecurityConfiguration configuration;

    @Inject
    public BoxProvider(SecurityConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return "Box";
    }

    @Override
    public String getClientId() {
        return configuration.getOauth2ContentApiClientId();
    }

    @Override
    public String getClientSecret() {
        return configuration.getOauth2ContentApiClientSecret();
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
        return String.format("https://app.box.com/api/oauth2/authorize?response_type=code&approval_prompt=auto&client_id=%s", config.getApiKey());
    }
}

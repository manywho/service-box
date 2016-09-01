package com.manywho.services.box.configuration;

import com.manywho.sdk.services.config.ServiceConfigurationDefault;
import com.manywho.sdk.services.config.ServiceConfigurationEnvironmentVariables;
import com.manywho.sdk.services.config.ServiceConfigurationProperties;

import javax.inject.Inject;

public class SecurityConfiguration extends ServiceConfigurationDefault {
    @Inject
    public SecurityConfiguration(ServiceConfigurationEnvironmentVariables environment, ServiceConfigurationProperties properties) {
        super(environment, properties);
    }

    public String getOauth2ContentApiClientId() {
        return this.get("oauth2.contentApi.clientId");
    }

    public String getOauth2ContentApiClientSecret() {
        return this.get("oauth2.contentApi.clientSecret");
    }

    public String getOauth2DeveloperEditionClientId() {
        return this.get("oauth2.developerEdition.clientId");
    }

    public String getOauth2DeveloperEditionClientSecret() {
        return this.get("oauth2.developerEdition.clientSecret");
    }

    public String getPrivateKeyLocation() {
        return this.get("secure.privateKeyLocation");
    }

    public String getPrivateKeyPassword() {
        return this.get("secure.privateKeyPassword");
    }
}

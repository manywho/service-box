package com.manywho.services.box.configuration;

import com.manywho.sdk.services.config.ServiceConfigurationDefault;
import com.manywho.sdk.services.config.ServiceConfigurationEnvironmentVariables;
import com.manywho.sdk.services.config.ServiceConfigurationProperties;
import com.manywho.services.box.utilities.Base64Helper;

import javax.inject.Inject;
import java.util.Base64;

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

    public String getOauth2DeveloperEditionPublicId() {
        return this.get("oauth2.developerEdition.publicKeyId");
    }

    public String getPrivateKey() {
        return Base64Helper.decode(this.get("secure.privateKey"));
    }

    public String getPrivateKeyPassword() {
        return this.get("secure.privateKeyPassword");
    }

    public String getWebhookSignaturePrimaryKey() {return this.get("webhook.signature.primary.key");}

    public String getWebhookSignatureSecondaryKey() {return this.get("webhook.signature.secondary.key");}
}

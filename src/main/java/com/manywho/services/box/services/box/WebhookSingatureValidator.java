package com.manywho.services.box.services.box;

import com.box.sdk.BoxWebHookSignatureVerifier;
import com.manywho.services.box.configuration.SecurityConfiguration;

import javax.inject.Inject;

public class WebhookSingatureValidator {
    private SecurityConfiguration securityConfiguration;
    private BoxWebHookSignatureVerifier verifier;

    @Inject
    public WebhookSingatureValidator(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;

        this.verifier =  new BoxWebHookSignatureVerifier(
                this.securityConfiguration.getWebhookSignaturePrimaryKey(),
                this.securityConfiguration.getWebhookSignatureSecondaryKey()
        );
    }

    public WebhookSingatureValidator(SecurityConfiguration securityConfiguration, BoxWebHookSignatureVerifier verifier) {
        this.securityConfiguration = securityConfiguration;
        this.verifier = verifier;
    }

    public Boolean validateWebhookSignature(String signatureVersion, String algorithm, String signaturePrimary,
                                            String signatureSecondary, String payload, String deliveryTimestamp ) {

        return verifier.verify(signatureVersion, algorithm, signaturePrimary, signatureSecondary, payload, deliveryTimestamp);
    }
}

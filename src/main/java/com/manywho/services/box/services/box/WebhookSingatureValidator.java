package com.manywho.services.box.services.box;

import com.box.sdk.BoxWebHookSignatureVerifier;
import com.manywho.services.box.configuration.SecurityConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;

public class WebhookSingatureValidator {
    private SecurityConfiguration securityConfiguration;
    private BoxWebHookSignatureVerifier verifier;
    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());

    @Inject
    public WebhookSingatureValidator(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
        LOGGER.debug("signature keys");
        LOGGER.debug(StringUtils.length(this.securityConfiguration.getWebhookSignaturePrimaryKey()));
        LOGGER.debug(StringUtils.length(this.securityConfiguration.getWebhookSignatureSecondaryKey()));

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


package com.manywho.services.box.configuration;

import com.manywho.sdk.services.config.ServiceConfigurationProperties;
import com.manywho.services.box.utilities.Base64Helper;

public class EncryptConfiguration extends ServiceConfigurationProperties {
    public String getVerificationKey() {
        return Base64Helper.decode(this.get("verification.key"));
    }
}
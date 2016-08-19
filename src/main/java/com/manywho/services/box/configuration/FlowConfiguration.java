package com.manywho.services.box.configuration;

import com.manywho.sdk.services.config.ServiceConfigurationProperties;

public class FlowConfiguration extends ServiceConfigurationProperties {
    public String getAssignmentFlowId() {return this.get("assignment.flowId");
    }
}

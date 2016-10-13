package com.manywho.services.box.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import javax.validation.constraints.NotNull;

public class AssignFlowWebhookCreate {
    @Property("Flow ID")
    @NotNull(message = "A Flow ID is required when assigning a flow")
    private String flowId;

    @Property("Flow Version ID")
    private String flowVersionId;

    @Property("Tenant ID")
    @NotNull(message = "A Tenant ID is required when assigning a flow")
    private String tenantId;

    @Property("Box Target Item Type")
    @NotNull(message = "A Box Target Item Type is required when assigning a flow")
    private String targetType;

    @Property("Box Target Item ID")
    @NotNull(message = "A Box Target Item ID is required when assigning a flow")
    private String targetId;

    @Property("Trigger")
    @NotNull(message = "A Trigger is required when assigning a flow")
    private String trigger;

    public String getFlowId() {
        return flowId;
    }

    public String getFlowVersionId() {
        return flowVersionId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getTrigger() {
        return trigger;
    }
}

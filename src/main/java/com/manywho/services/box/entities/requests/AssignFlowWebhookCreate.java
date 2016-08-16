package com.manywho.services.box.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import javax.validation.constraints.NotNull;

public class AssignFlowWebhookCreate {
    @Property("Flow Id")
    @NotNull(message = "A name is required when assigning a flow")
    private String flowId;

    @Property("Flow Version Id")
    private String flowVersionId;

    @Property("Tenant Id")
    @NotNull(message = "A Tenant Id is required when assigning a flow")
    private String tenantId;

    @Property("Target Type")
    @NotNull(message = "A Target Type is required when assigning a flow")
    private String targetType;

    @Property("Target Id")
    @NotNull(message = "A Target Type is required when assigning a flow")
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

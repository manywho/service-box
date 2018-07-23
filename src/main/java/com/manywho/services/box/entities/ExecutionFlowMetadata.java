package com.manywho.services.box.entities;

import java.util.Objects;

public class ExecutionFlowMetadata {
    private String keyTemplate;
    private String flowId;
    private String flowVersionId;
    private String enterpriseId;
    private String trigger;
    private String tenantId;

    public ExecutionFlowMetadata(String keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public ExecutionFlowMetadata() {}

    public Boolean canExecute() {
        return flowId!=null && (flowVersionId != null || trigger != null) && tenantId != null;
    }

    public String getKeyTemplate() {
        return keyTemplate;
    }

    public void setKeyTemplate(String keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getFlowVersionId() {
        return flowVersionId;
    }

    public void setFlowVersionId(String flowVersionId) {
        if (Objects.equals(flowVersionId, "")) {
            flowVersionId = null;
        }

        this.flowVersionId = flowVersionId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}

package com.manywho.services.box.entities;

public class ExecutionFlowMetadata {
    private String keyTemplate;
    private String flowId;
    private String flowVersionId;
    private String enterpriseId;

    public ExecutionFlowMetadata(String keyTemplate) {
        this.keyTemplate = keyTemplate;
    }

    public Boolean canExecute() {
        return flowId!=null && flowVersionId != null;
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
        this.flowVersionId = flowVersionId;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
}

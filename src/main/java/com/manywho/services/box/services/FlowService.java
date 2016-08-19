package com.manywho.services.box.services;

import com.manywho.sdk.client.raw.RawRunClient;
import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.entities.run.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.entities.ExecutionFlowMetadata;

import javax.inject.Inject;
import java.util.UUID;

public class FlowService {
    final private RawRunClient runClient;

    @Inject
    public FlowService(RawRunClient runClient) {
        this.runClient = runClient;
    }

    public EngineInitializationResponse startFlow(String tenantId, FlowId flowId, ExecutionFlowMetadata executionFlowMetadata, String targetType, String targetId) throws Exception {
        EngineInitializationRequest engineInitializationRequest = new EngineInitializationRequest();
        engineInitializationRequest.setFlowId(flowId);

        EngineValueCollection engineValues = new EngineValueCollection();

        engineValues.add(new EngineValue("webhook-flow-id", ContentType.String, executionFlowMetadata.getFlowId()));
        engineValues.add(new EngineValue("webhook-flow-version-id", ContentType.String, executionFlowMetadata.getFlowVersionId()));
        engineValues.add(new EngineValue("webhook-tenant-id", ContentType.String, executionFlowMetadata.getTenantId()));
        engineValues.add(new EngineValue("webhook-trigger", ContentType.String, executionFlowMetadata.getTrigger()));
        engineValues.add(new EngineValue("webhook-target-id", ContentType.String, targetId));
        engineValues.add(new EngineValue("webhook-target-type", ContentType.String, targetType));

        engineInitializationRequest.setInputs(engineValues);

        return runClient.initialize(UUID.fromString(tenantId), null, engineInitializationRequest);
    }

    public EngineInitializationResponse startFlowAfterWebhook(Credentials credentials,
                                                              ExecutionFlowMetadata executionFlowMetadata,
                                                              String targetType, String targetId) throws Exception {

        EngineInitializationRequest engineInitializationRequest = new EngineInitializationRequest();

        if(executionFlowMetadata.getFlowVersionId() == null) {
            engineInitializationRequest.setFlowId(new FlowId(executionFlowMetadata.getFlowId()));
        } else {
            engineInitializationRequest.setFlowId(new FlowId(executionFlowMetadata.getFlowId(), executionFlowMetadata.getFlowVersionId()));
        }

        EngineValueCollection engineValues = new EngineValueCollection();
        engineValues.add(new EngineValue("webhook-target-id", ContentType.String, targetId));
        engineValues.add(new EngineValue("webhook-target-type", ContentType.String, targetType));
        // todo probably need to be passed in the header
        engineValues.add(new EngineValue("access token", ContentType.String, credentials.getAccessToken()));
        engineInitializationRequest.setInputs(engineValues);

        return runClient.initialize(UUID.fromString(executionFlowMetadata.getTenantId()), null, engineInitializationRequest);
    }

    public EngineInvokeResponse joinFlow(ExecutionFlowMetadata executionFlowMetadata, String stateId, String auth){
       return runClient.join(UUID.fromString(executionFlowMetadata.getTenantId()), UUID.fromString(stateId), auth);
    }
}
package com.manywho.services.box.services;

import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.entities.run.*;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.FlowMode;
import com.manywho.services.box.clients.ExtendedRawRunClient;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.entities.InitializationRequest;

import javax.inject.Inject;
import java.util.UUID;

public class FlowService {
    final private ExtendedRawRunClient runClient;

    @Inject
    public FlowService(ExtendedRawRunClient runClient) {
        this.runClient = runClient;
    }

    public String getFlowAuthenticationCode(String stateId, AuthenticatedWho authenticatedWho, String password, String sessionToken, String sessionUrl, String loginUrl ) {
        InitializationRequest initializationRequest = new InitializationRequest();
        initializationRequest.setUsername(authenticatedWho.getUsername());
        initializationRequest.setLoginUrl(loginUrl);
        initializationRequest.setPassword(password);
        initializationRequest.setSessionToken(sessionToken);
        initializationRequest.setToken(authenticatedWho.getToken());
        initializationRequest.setSessionUrl(sessionUrl);


        return runClient.authentication(UUID.fromString(stateId), UUID.fromString(authenticatedWho.getManyWhoTenantId()), initializationRequest);
    }

    public EngineInitializationResponse startFlow(String tenantId, FlowId flowId, ExecutionFlowMetadata executionFlowMetadata, String targetType, String targetId, String auth) throws Exception {
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

        return runClient.initialize(UUID.fromString(tenantId), auth, engineInitializationRequest);
    }

    public EngineInitializationResponse initializeFlowWithoutAuthentication(ExecutionFlowMetadata executionFlowMetadata) throws Exception {

        EngineInitializationRequest engineInitializationRequest = new EngineInitializationRequest();
        engineInitializationRequest.setMode(FlowMode.Default.toString());

        if(executionFlowMetadata.getFlowVersionId() == null) {
            engineInitializationRequest.setFlowId(new FlowId(executionFlowMetadata.getFlowId()));
        } else {
            engineInitializationRequest.setFlowId(new FlowId(executionFlowMetadata.getFlowId(), executionFlowMetadata.getFlowVersionId()));
        }

        return runClient.initialize(UUID.fromString(executionFlowMetadata.getTenantId()), null, engineInitializationRequest);
    }


    public EngineInitializationResponse initializeFlowWithAuthentication(ExecutionFlowMetadata executionFlowMetadata,
                                                                         String targetType, String targetId,
                                                                         String header) throws Exception {
        EngineInitializationRequest engineInitializationRequest = new EngineInitializationRequest();

        if(executionFlowMetadata.getFlowVersionId() == null) {
            engineInitializationRequest.setFlowId(new FlowId(executionFlowMetadata.getFlowId()));
        } else {
            engineInitializationRequest.setFlowId(new FlowId(executionFlowMetadata.getFlowId(), executionFlowMetadata.getFlowVersionId()));
        }

        EngineValueCollection engineValues = new EngineValueCollection();
        engineValues.add(new EngineValue("webhook-target-id", ContentType.String, targetId));
        engineValues.add(new EngineValue("webhook-target-type", ContentType.String, targetType));
        engineInitializationRequest.setInputs(engineValues);

        return runClient.initialize(UUID.fromString(executionFlowMetadata.getTenantId()), header, engineInitializationRequest);
    }

    public EngineInvokeResponse executeFlow(String tenantId, String auth, EngineInvokeRequest engineInvokeRequest) {
        return runClient.execute(UUID.fromString(tenantId), auth, engineInvokeRequest);
    }
}
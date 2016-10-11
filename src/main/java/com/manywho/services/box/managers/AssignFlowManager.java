package com.manywho.services.box.managers;

import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.entities.requests.AssignFlowWebhookCreate;
import com.manywho.services.box.services.ListenerService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

public class AssignFlowManager {
    private CacheManagerInterface cacheManager;
    private ListenerService listenerService;

    @Inject
    public AssignFlowManager(CacheManagerInterface cacheManager, ListenerService listenerService){
        this.cacheManager = cacheManager;
        this.listenerService = listenerService;
    }

    public void assignFlowToWebhook(AssignFlowWebhookCreate assignFlow, AuthenticatedWho authenticatedWho, String authorizationHeader) throws Exception {

        ExecutionFlowMetadata executionFlowMetadata = new ExecutionFlowMetadata();
        executionFlowMetadata.setTrigger(assignFlow.getTrigger());
        executionFlowMetadata.setFlowId(assignFlow.getFlowId());
        if (StringUtils.isEmpty(assignFlow.getFlowVersionId()) || "null".equalsIgnoreCase(assignFlow.getFlowVersionId())) {
            executionFlowMetadata.setFlowVersionId(null);
        } else {
            executionFlowMetadata.setFlowVersionId(assignFlow.getFlowVersionId());
        }

        executionFlowMetadata.setTenantId(assignFlow.getTenantId());

        Credentials credentials = cacheManager.getCredentials(authenticatedWho.getUserId());
        credentials.setFlowsListenning(true);
        cacheManager.saveCredentials(authenticatedWho.getUserId(), credentials);

        cacheManager.saveUserIdByTokenKey(authenticatedWho.getToken(), authenticatedWho.getUserId());
        listenerService.createFlowListener(assignFlow.getTargetId(), authenticatedWho.getToken() , executionFlowMetadata);
        cacheManager.saveFlowHeaderByUser(authenticatedWho.getUserId(), authorizationHeader);
    }
}

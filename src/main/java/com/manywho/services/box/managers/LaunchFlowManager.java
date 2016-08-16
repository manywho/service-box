package com.manywho.services.box.managers;

import com.box.sdk.*;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.facades.BoxFacade;
import com.manywho.services.box.services.AuthenticationService;
import com.manywho.services.box.services.CallbackService;
import com.manywho.services.box.services.WebhookService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class LaunchFlowManager {
    AuthenticationService authenticationService;
    BoxFacade boxFacade;
    CacheManager cacheManager;
    AbstractOauth2Provider oauth2Provider;
    WebhookService webhookService;
    WebhookManager webhookManager;
    CallbackService callbackService;

    @Inject
    public LaunchFlowManager(AuthenticationService authenticationService, BoxFacade boxFacade,
                             AbstractOauth2Provider oauth2Provider, CallbackService callbackService,
                             WebhookService webhookService, CacheManager cacheManager, WebhookManager webhookManager) {

        this.oauth2Provider = oauth2Provider;
        this.authenticationService = authenticationService;
        this.boxFacade = boxFacade;
        this.callbackService = callbackService;
        this.webhookService = webhookService;
        this.cacheManager = cacheManager;
        this.webhookManager = webhookManager;
    }

    public void createFlowListener(String fileId, BoxAPIConnection apiConnection, ExecutionFlowMetadata executionFlowMetadata) throws Exception {

        String webhookId = cacheManager.getWebhook("file", fileId);
        BoxWebHook.Info info;

        if(webhookId != null) {
            info = webhookManager.getWebhookInfo(apiConnection.getAccessToken(), "", webhookId);
            webhookManager.addTriggerToWebhookInfo(apiConnection.getAccessToken(), info, executionFlowMetadata.getTrigger());
        } else {
            webhookManager.createWebhook(apiConnection.getAccessToken(), "", "file", fileId, BoxWebHook.Trigger.fromValue(executionFlowMetadata.getTrigger()));
        }

        cacheManager.saveFlowListener("file", fileId, executionFlowMetadata.getTrigger(), executionFlowMetadata);
    }

    public ExecutionFlowMetadata getExecutionFlowMetadata(String accessToken, String fileId) throws Exception {

        BoxFile boxFile = boxFacade.getFile(accessToken, fileId);

        List<ExecutionFlowMetadata> fileMetadata = callbackService.getAllPossibleExecutionFlowMetadata(boxFile);

        if (fileMetadata.size() <1 ) {
            throw new Exception("There is not metadata template for this file");
        }

        BoxDeveloperEditionAPIConnection developerApiConnection = boxFacade.createDeveloperApiConnection(callbackService.getEnterpriseIdFromMetadata(fileMetadata));

        List<BoxMetadataTemplate.Info> accountTemplates = BoxMetadataTemplate.getEnterpriseTemplates(developerApiConnection);
        callbackService.overwriteNullValuesWithDefaultOptions(fileMetadata, accountTemplates);

        List<ExecutionFlowMetadata> executionFlowMetadatas = fileMetadata.stream()
                .filter(ExecutionFlowMetadata::canExecute)
                .collect(Collectors.toList());

        if(executionFlowMetadatas.size() > 1) {
            throw new Exception("More than one flow attached to this file");
        }

        if(executionFlowMetadatas.size() < 1) {
            throw new Exception("There is not flow attached to this file");
        }

        return executionFlowMetadatas.get(0);
    }

}

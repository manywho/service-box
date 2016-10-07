package com.manywho.services.box.managers;

import com.box.sdk.*;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.client.BoxClient;
import com.manywho.services.box.services.AuthenticationService;
import com.manywho.services.box.services.CallbackService;
import com.manywho.services.box.services.WebhookTriggersService;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

public class LaunchFlowManager {
    AuthenticationService authenticationService;
    BoxClient boxClient;
    CacheManagerInterface cacheManager;
    AbstractOauth2Provider oauth2Provider;
    WebhookTriggersService webhookTriggersService;
    WebhookManager webhookManager;
    CallbackService callbackService;

    @Inject
    public LaunchFlowManager(AuthenticationService authenticationService, BoxClient boxClient,
                             AbstractOauth2Provider oauth2Provider, CallbackService callbackService,
                             WebhookTriggersService webhookTriggersService, CacheManagerInterface cacheManager, WebhookManager webhookManager) {

        this.oauth2Provider = oauth2Provider;
        this.authenticationService = authenticationService;
        this.boxClient = boxClient;
        this.callbackService = callbackService;
        this.webhookTriggersService = webhookTriggersService;
        this.cacheManager = cacheManager;
        this.webhookManager = webhookManager;
    }

    public ExecutionFlowMetadata getExecutionFlowMetadata(String accessToken, String fileId) throws Exception {

        BoxFile boxFile = boxClient.getFileWithWebIntegretionCredentials(accessToken, fileId);

        List<ExecutionFlowMetadata> fileMetadata = callbackService.getAllPossibleExecutionFlowMetadata(boxFile);

        if (fileMetadata.size() <1 ) {
            throw new Exception("There is not metadata template for this file");
        }

        BoxDeveloperEditionAPIConnection developerApiConnection = boxClient.createDeveloperApiConnection(callbackService.getEnterpriseIdFromMetadata(fileMetadata));

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

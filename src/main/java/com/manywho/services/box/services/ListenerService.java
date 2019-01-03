package com.manywho.services.box.services;

import com.box.sdk.BoxWebHook;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.managers.CacheManagerInterface;
import com.manywho.services.box.managers.WebhookManager;

import javax.inject.Inject;

public class ListenerService {
    CacheManagerInterface cacheManager;
    WebhookManager webhookManager;

    @Inject
    public ListenerService(CacheManagerInterface cacheManager, WebhookManager webhookManager) {
        this.cacheManager = cacheManager;
        this.webhookManager = webhookManager;
    }

    public void createFlowListener(String fileId, String accessToken, ExecutionFlowMetadata executionFlowMetadata) throws Exception {

        String webhookId = cacheManager.getWebhook("file", fileId);
        BoxWebHook.Info info;

        if(webhookId != null) {
            info = webhookManager.getWebhookInfo(accessToken, "", webhookId);
            webhookManager.addTriggerToWebhookInfo(accessToken, info, executionFlowMetadata.getTrigger());
        } else {
            webhookManager.createWebhook(accessToken, "", "FILE", fileId, BoxWebHook.Trigger.valueOf(executionFlowMetadata.getTrigger()));
        }

        cacheManager.saveFlowListener("file", fileId, executionFlowMetadata.getTrigger(), executionFlowMetadata);
    }
}

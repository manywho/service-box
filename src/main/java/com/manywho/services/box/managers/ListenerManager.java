package com.manywho.services.box.managers;

import com.box.sdk.BoxTask;
import com.box.sdk.BoxWebHook;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.box.entities.TaskWebhook;
import com.manywho.services.box.services.TaskService;
import com.manywho.services.box.services.WebhookService;

import javax.inject.Inject;
import java.util.Objects;

public class ListenerManager {
    @Inject
    private WebhookManager webhookManager;

    @Inject
    private WebhookService webhookService;

    @Inject
    CacheManager cacheManager;

    @Inject
    TaskService taskService;

    public void createListener(AuthenticatedWho authenticatedWho, ListenerServiceRequest listenerServiceRequest, BoxWebHook.Trigger triggerType, String name) throws Exception {
        String webhookId = cacheManager.getWebhookByTarget(name, listenerServiceRequest.getValueForListening().getObjectData().get(0).getExternalId());
        BoxWebHook.Info webhookInfo;

        if (webhookId != null) {
            webhookInfo = webhookManager.getWebhookInfo(authenticatedWho.getToken(), listenerServiceRequest.getToken(), webhookId);
            if ( !webhookService.haveTrigger(webhookInfo, triggerType)) {
                webhookService.addTriggerToWebhookInfo(webhookInfo, triggerType);
                webhookService.updateWebhookInfo(authenticatedWho.getToken(), webhookId, webhookInfo);
            }
        } else {
            String externalId = listenerServiceRequest.getValueForListening().getObjectData().get(0).getExternalId();
            TaskWebhook taskWebhook = null;

            if (Objects.equals(name, "TASK")) {
                BoxTask.Info boxTask = taskService.getTask(authenticatedWho.getToken(), externalId);
                String externalIdFile = boxTask.getItem().getID();
                name = "FILE";
                taskWebhook = new TaskWebhook(boxTask.getID(), externalIdFile, name);
                externalId = externalIdFile;
            }

            webhookInfo = webhookManager.createWebhook(authenticatedWho.getToken(), listenerServiceRequest.getToken(),
                    name, externalId, triggerType);

            if (taskWebhook != null) {
                taskWebhook.setId(webhookInfo.getID());
                cacheManager.saveWebhookTask(taskWebhook.getId(), taskWebhook);
            }

            webhookId = webhookInfo.getID();
        }

        cacheManager.saveListenerServiceRequest(webhookId, triggerType.getValue(), listenerServiceRequest.getStateId(), listenerServiceRequest);
        cacheManager.saveAuthenticatedWhoForWebhook(webhookId, listenerServiceRequest.getStateId(), authenticatedWho);
    }
}

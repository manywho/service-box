package com.manywho.services.box.managers;

import com.box.sdk.BoxWebHook;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.box.services.WebhookTriggersService;

import javax.inject.Inject;

public class ListenerManager {
    private WebhookManager webhookManager;
    private WebhookTriggersService webhookTriggersService;
    private CacheManager cacheManager;

    @Inject
    public ListenerManager(WebhookManager webhookManager, WebhookTriggersService webhookTriggersService, CacheManager cacheManager) {
        this.webhookManager = webhookManager;
        this.webhookTriggersService = webhookTriggersService;
        this.cacheManager = cacheManager;
    }

    public void createListener(AuthenticatedWho authenticatedWho, ListenerServiceRequest listenerServiceRequest, BoxWebHook.Trigger triggerType, String name) throws Exception {
        String webhookId = cacheManager.getWebhook(name, listenerServiceRequest.getValueForListening().getObjectData().get(0).getExternalId());
        BoxWebHook.Info webhookInfo;

        if (webhookId != null) {
            webhookInfo = webhookManager.getWebhookInfo(authenticatedWho.getToken(), listenerServiceRequest.getToken(), webhookId);
            if ( !webhookTriggersService.haveTrigger(webhookInfo, triggerType)) {
                webhookTriggersService.addTriggerToWebhookInfo(webhookInfo, triggerType);
                webhookManager.updateWebhookInfo(authenticatedWho.getToken(), webhookId, webhookInfo);
            }
        } else {
            webhookInfo = webhookManager.createWebhook(authenticatedWho.getToken(),
                    listenerServiceRequest.getToken(), name,
                    listenerServiceRequest.getValueForListening().getObjectData().get(0).getExternalId(), triggerType);

            webhookId = webhookInfo.getID();
        }

        cacheManager.saveListenerServiceRequest(webhookId, triggerType.getValue(), listenerServiceRequest.getStateId(), listenerServiceRequest);
        cacheManager.saveAuthenticatedWhoForWebhook(webhookId, listenerServiceRequest.getStateId(), authenticatedWho);
    }
}

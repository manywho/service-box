package com.manywho.services.box.managers;

import com.box.sdk.BoxWebHook;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.box.services.WebhookTriggersService;

import javax.inject.Inject;
import java.util.Set;

public class ListenerManager {
    private WebhookManager webhookManager;
    private WebhookTriggersService webhookTriggersService;
    private CacheManagerInterface cacheManager;

    @Inject
    public ListenerManager(WebhookManager webhookManager, WebhookTriggersService webhookTriggersService, CacheManagerInterface cacheManager) {
        this.webhookManager = webhookManager;
        this.webhookTriggersService = webhookTriggersService;
        this.cacheManager = cacheManager;
    }

    public void createListener(AuthenticatedWho authenticatedWho, ListenerServiceRequest listenerServiceRequest, BoxWebHook.Trigger triggerType, String name) throws Exception {
        String objectId = listenerServiceRequest.getValueForListening().getObjectData().get(0).getExternalId();
        String webhookId = cacheManager.getWebhook(name, objectId);

        BoxWebHook.Info webhookInfo;

        if (webhookId != null) {
            webhookInfo = webhookManager.getWebhookInfo(authenticatedWho.getToken(), listenerServiceRequest.getToken(), webhookId);
            if ( !webhookTriggersService.haveTrigger(webhookInfo, triggerType)) {
                Set<BoxWebHook.Trigger> triggerList = webhookTriggersService.listOfTriggerForWebhook(webhookInfo, triggerType);
                webhookManager.updateWebhookInfoTriggers(authenticatedWho.getToken(), webhookId, triggerList);
            }
        } else {
            webhookInfo = webhookManager.createWebhook(authenticatedWho.getToken(), listenerServiceRequest.getToken(),
                    name, objectId, triggerType);

            webhookId = webhookInfo.getID();
        }

        cacheManager.saveListenerServiceRequest(webhookId, triggerType.getValue(), listenerServiceRequest.getStateId(), listenerServiceRequest);
        cacheManager.saveAuthenticatedWhoForWebhook(webhookId, listenerServiceRequest.getStateId(), authenticatedWho);
    }
}

package com.manywho.services.box.managers;

import com.box.sdk.BoxWebHook;
import com.manywho.services.box.client.BoxClient;
import com.manywho.services.box.services.WebhookTriggersService;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.Set;

public class WebhookManager {
    private WebhookTriggersService webhookTriggersService;
    private CacheManagerInterface cacheManager;
    private BoxClient boxFacade;
    private UriInfo uriInfo;

    @Inject
    public WebhookManager(WebhookTriggersService webhookTriggersService, CacheManagerInterface cacheManager, UriInfo uriInfo, BoxClient boxFacade) {
        this.webhookTriggersService = webhookTriggersService;
        this.cacheManager = cacheManager;
        this.uriInfo = uriInfo;
        this.boxFacade = boxFacade;
    }

    public BoxWebHook.Info createWebhook(String userToken, String token, String targetType, String targetId, BoxWebHook.Trigger trigger) throws Exception {
        Set<BoxWebHook.Trigger> triggersToSend = new HashSet<>();
        triggersToSend.add(trigger);

        BoxWebHook.Info webhook = boxFacade.createWebhook(userToken, targetId, targetType, "https://" + uriInfo.getAbsolutePath().getHost() + "/api/box/3/webhook/callback", triggersToSend);

        if (webhook == null) {
            throw new Exception("Unable to create a Webhook with the name");
        }

        cacheManager.saveWebhook(targetType, targetId, webhook.getID());

        return webhook;
    }

    public BoxWebHook.Info getWebhookInfo(String userToken, String token, String webhookId) throws Exception {

        BoxWebHook.Info webhook = boxFacade.getWebhook(userToken, webhookId);
        if (webhook == null) {
            throw new Exception("Unable to get a Webhook with id" + webhookId);
        }

        return webhook;
    }

    public void addTriggerToWebhookInfo(String accessToken, BoxWebHook.Info info, String trigger)
    {
        Set<BoxWebHook.Trigger> triggerSets = webhookTriggersService.listOfTriggerForWebhook(info, BoxWebHook.Trigger.fromValue(trigger));
        updateWebhookInfoTriggers(accessToken, info.getResource().getID(), triggerSets);
    }

    public void deleteWebhook(String userToken, String id) {
        boxFacade.deleteWebhook(userToken, id);
    }

    public void updateWebhookInfoTriggers(String token, String webhookId, Set<BoxWebHook.Trigger> triggerSet) {
        boxFacade.updateWebhookTriggers(token, webhookId, triggerSet);
    }
}

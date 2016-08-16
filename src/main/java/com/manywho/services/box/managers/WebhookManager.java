package com.manywho.services.box.managers;

import com.box.sdk.BoxWebHook;
import com.manywho.services.box.services.WebhookService;
import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.Set;

public class WebhookManager {
    private WebhookService webhookService;
    private CacheManager cacheManager;
    private UriInfo uriInfo;

    @Inject
    public WebhookManager(WebhookService webhookService, CacheManager cacheManager, UriInfo uriInfo) {
        this.webhookService = webhookService;
        this.cacheManager = cacheManager;
        this.uriInfo = uriInfo;
    }

    public BoxWebHook.Info createWebhook(String userToken, String token, String targetType, String targetId, BoxWebHook.Trigger trigger) throws Exception {
        Set<BoxWebHook.Trigger> triggersToSend = new HashSet<>();
        triggersToSend.add(trigger);

        BoxWebHook.Info webhookInfo = webhookService.createWebhook(userToken, targetId, targetType,
                "https://" + uriInfo.getAbsolutePath().getHost() + "/api/box/3/webhook/callback", triggersToSend);

        cacheManager.saveWebhook(targetType, targetId, webhookInfo.getID());

        return webhookInfo;
    }

    public BoxWebHook.Info getWebhookInfo(String userToken, String token, String webhookId) throws Exception {

        return webhookService.getWebhookInfo(userToken, webhookId);
    }

    public void addTriggerToWebhookInfo(String accessToken, BoxWebHook.Info info, String trigger)
    {
        webhookService.addTriggerToWebhookInfo(info, BoxWebHook.Trigger.fromValue(trigger));
        webhookService.updateWebhookInfo(accessToken, info.getResource().getID(), info);
    }

    public void deleteWebhook(String userToken, String id) {
        webhookService.deleteWebhook(userToken, id);
    }
}

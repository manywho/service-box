package com.manywho.services.box.managers;

import com.box.sdk.BoxWebHook;
import com.manywho.services.box.services.WebhookService;

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.HashSet;
import java.util.Set;

public class WebhookManager {
    @Inject
    private WebhookService webhookService;

    @Inject
    private CacheManager cacheManager;

    @Context
    private UriInfo uriInfo;

    public BoxWebHook.Info createWebhook(String userToken, String token, String targetType, String targetId, BoxWebHook.Trigger trigger) throws Exception {
        Set<BoxWebHook.Trigger> triggersToSend = new HashSet<>();
        triggersToSend.add(trigger);

        BoxWebHook.Info webhookInfo = webhookService.createWebhook(userToken, targetId, targetType,
                "https://" + uriInfo.getAbsolutePath().getHost() + "/api/box/3/webhook/callback", triggersToSend);

        cacheManager.saveWebhookByTarget(targetType, targetId, webhookInfo.getID());

        return webhookInfo;
    }

    public BoxWebHook.Info getWebhookInfo(String userToken, String token, String webhookId) throws Exception {

        return webhookService.getWebhookInfo(userToken, webhookId);
    }

    public void deleteWebhook(String userToken, String id) {
        webhookService.deleteWebhook(userToken, id);
    }
}

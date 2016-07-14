package com.manywho.services.box.services;

import com.box.sdk.BoxWebHook;
import com.manywho.services.box.facades.BoxFacade;

import javax.inject.Inject;
import java.util.Objects;
import java.util.Set;

public class WebhookService {
    @Inject
    private BoxFacade boxFacade;

    public BoxWebHook.Info createWebhook(String token, String targetId, String targetType, String callbackUrl, Set<BoxWebHook.Trigger> triggers) throws Exception {
        BoxWebHook.Info webhook = boxFacade.createWebhook(token, targetId, targetType, callbackUrl, triggers);

        if (webhook == null) {
            throw new Exception("Unable to create a Webhook with the name");
        }

        return webhook;
    }

    public void deleteWebhook(String token, String webhookId) {
        boxFacade.deleteWebhook(token, webhookId);
    }

    public void addTriggerToWebhookInfo(BoxWebHook.Info webhook, BoxWebHook.Trigger newTrigger) {
        Set<BoxWebHook.Trigger> currentTriggers = webhook.getTriggers();
        boolean found = false;

        for (BoxWebHook.Trigger t:currentTriggers) {
            if (Objects.equals(t.toString(), newTrigger.toString())) {
                found = true;
            }
        }

        if(!found) {
            currentTriggers.add(newTrigger);
        }
    }
    // workaround bug in library
    public Boolean haveTrigger(BoxWebHook.Info webhook, BoxWebHook.Trigger newTrigger) {

        for (BoxWebHook.Trigger t:webhook.getTriggers()) {
            if (Objects.equals(t.toString(), newTrigger.toString())) {
                return true;
            }
        }

        return false;
    }

    public BoxWebHook.Info getWebhookInfo(String token, String webhookId) throws Exception {
        BoxWebHook.Info webhook = boxFacade.getWebhook(token, webhookId);
        if (webhook == null) {
            throw new Exception("Unable to get a Webhook with id" + webhookId);
        }

        return webhook;
    }

    public void updateWebhookInfo(String token, String webhookId, BoxWebHook.Info info) {
        boxFacade.updateWebhook(token, webhookId, info);
    }
}

package com.manywho.services.box.managers;


import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.services.AuthenticationService;

import javax.inject.Inject;

public class WebhookHandlerManager {
    AuthenticationService authenticationService;
    CallbackWebhookManager callbackWebhookManager;

    @Inject
    public WebhookHandlerManager(AuthenticationService authenticationService, CallbackWebhookManager callbackWebhookManager){
        this.authenticationService = authenticationService;
        this.callbackWebhookManager = callbackWebhookManager;
    }

    public void handleWebhook(WebhookReturn webhookReturn, String webhookId, String targetId, String targetType, String createdByUserId) throws Exception {
        switch (targetType) {
            case "file":
                // if we dont have credentials means
                if(authenticationService.updateCredentials(createdByUserId) != null) {
                    callbackWebhookManager.processEventFile(webhookId, targetId, webhookReturn.getTrigger());
                    callbackWebhookManager.processEventFileForFlow(createdByUserId, targetType, targetId, webhookReturn.getTrigger());
                }
                break;
            case "folder":
                callbackWebhookManager.processEventFolder(webhookId, targetId, webhookReturn.getTrigger());
                break;
            default:
                break;
        }
    }
}

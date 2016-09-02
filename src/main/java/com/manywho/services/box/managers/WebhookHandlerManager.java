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
                if(authenticationService.updateCredentials(createdByUserId) != null) {
                    callbackWebhookManager.processEventFile(webhookId, targetId, webhookReturn.getTrigger());
                    callbackWebhookManager.processEventFileForFlow(createdByUserId, targetType, targetId, webhookReturn.getTrigger());
                }
                break;
            case "task_assignment":
                if(authenticationService.updateCredentials(createdByUserId) != null) {
                    callbackWebhookManager.processEventTask(webhookId, targetId, webhookReturn.getTrigger());
                    callbackWebhookManager.processEventTaskForFlow(createdByUserId, targetType, targetId,
                            webhookReturn.getTrigger(), webhookReturn.getSource().getItem().getId(),
                            webhookReturn.getSource().getItem().getType());
                }
                break;
            case "folder":
                if(authenticationService.updateCredentials(createdByUserId) != null) {
                    callbackWebhookManager.processEventFolder(webhookId, targetId, webhookReturn.getTrigger());
                }
                break;
            default:
                break;
        }
    }
}

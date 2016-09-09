package com.manywho.services.box.managers;


import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.services.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;

public class WebhookHandlerManager {
    private AuthenticationService authenticationService;
    private CallbackWebhookManager callbackWebhookManager;
    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());

    @Inject
    public WebhookHandlerManager(AuthenticationService authenticationService, CallbackWebhookManager callbackWebhookManager){
        this.authenticationService = authenticationService;
        this.callbackWebhookManager = callbackWebhookManager;
    }

    public void handleWebhook(WebhookReturn webhookReturn, String webhookId, String targetId, String targetType, String createdByUserId) throws Exception {
        if(authenticationService.updateCredentials(createdByUserId) == null) {
            LOGGER.debug("credentials null it shouldn't happen");
            return;
        }

        switch (targetType) {
            case "file":
                callbackWebhookManager.processEventFile(webhookId, targetId, webhookReturn.getTrigger());
                callbackWebhookManager.processEventForFlow(createdByUserId, targetType, targetId, webhookReturn.getTrigger());
                break;
            case "task_assignment":
                callbackWebhookManager.processEventTask(webhookId, targetId, webhookReturn.getTrigger());
                callbackWebhookManager.processEventTaskForFlow(createdByUserId, targetType, targetId,
                        webhookReturn.getTrigger(), webhookReturn.getSource().getItem().getId(),
                        webhookReturn.getSource().getItem().getType());
                break;
            case "folder":
                callbackWebhookManager.processEventFolder(webhookId, targetId, webhookReturn.getTrigger());
                callbackWebhookManager.processEventForFlow(createdByUserId, targetType, targetId, webhookReturn.getTrigger());
                break;
            default:
                break;
        }
        LOGGER.debug("targetType:" + targetType + " targetId:"+ targetId + " processed" );
    }
}

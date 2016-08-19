package com.manywho.services.box.controllers;

import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.managers.CallbackWebhookManager;
import com.manywho.services.box.services.AuthenticationService;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/webhook")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CallbackWebhookController {
    private CallbackWebhookManager callbackWebhookManager;
    private AuthenticationService authenticationService;

    @Inject
    public CallbackWebhookController(CallbackWebhookManager callbackWebhookManager, AuthenticationService authenticationService) {
        this.callbackWebhookManager = callbackWebhookManager;
        this.authenticationService = authenticationService;
    }

    @Path("/callback")
    @POST
    public void callback(WebhookReturn webhookReturn) throws Exception {

        String webhookId = (String) webhookReturn.getWebhook().get("id");
        String targetId = (String) webhookReturn.getSource().get("id");
        String targetType = (String) webhookReturn.getSource().get("type");
        String createdByUserId = (String) webhookReturn.getCreatedBy().get("id");

        switch (targetType) {
            case "file":
                authenticationService.updateCredentials(createdByUserId);
                callbackWebhookManager.processEventFile(webhookId, targetId, webhookReturn.getTrigger());
                callbackWebhookManager.proccessEventFileForFlow(createdByUserId, targetType, targetId, webhookReturn.getTrigger());
                break;
            case "folder":
                callbackWebhookManager.processEventFolder(webhookId, targetId, webhookReturn.getTrigger());
                break;
            default:
                break;
        }
    }
}
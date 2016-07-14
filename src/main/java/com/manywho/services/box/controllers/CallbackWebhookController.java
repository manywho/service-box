package com.manywho.services.box.controllers;

import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.managers.CallbackWebhookManager;
import javax.inject.Inject;
import javax.ws.rs.*;

@Path("/webhook")
@Consumes("application/json")
@Produces("application/json")
public class CallbackWebhookController {

    @Inject
    private CallbackWebhookManager callbackWebhookManager;

    @Path("/callback")
    @POST
    public void callback(WebhookReturn webhookReturn) throws Exception {

        String webhookId = (String) webhookReturn.getWebhook().get("id");
        String targetId = (String) webhookReturn.getSource().get("id");
        String targetType = (String) webhookReturn.getSource().get("type");
        switch (targetType) {
            case "file":
                callbackWebhookManager.processEventFile(webhookId, targetId, webhookReturn.getTrigger());
                break;
            case "folder":
                callbackWebhookManager.processEventFolder(webhookId, targetId, webhookReturn.getTrigger());
                break;
            default:
                break;
        }
    }
}

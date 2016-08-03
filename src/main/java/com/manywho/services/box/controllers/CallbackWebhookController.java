package com.manywho.services.box.controllers;

import com.manywho.services.box.entities.WebhookCallback;
import com.manywho.services.box.managers.CallbackWebhookManager;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.Objects;

@Path("/webhook")
@Consumes("application/json")
@Produces("application/json")
public class CallbackWebhookController {

    @Inject
    private CallbackWebhookManager callbackWebhookManager;

    @Path("/callback")
    @POST
    public void callback(WebhookCallback webhookCallback) throws Exception {
            String webhookId = (String) webhookCallback.getWebhook().get("id");
            String trigger = webhookCallback.getTrigger();
            String targetId;
            String targetType;

            // when the trigger is about a task the source is null, this is a work around that box bug
            if (Objects.equals(trigger, "TASK_ASSIGNMENT.UPDATED") || Objects.equals(trigger, "TASK_ASSIGNMENT.CREATED")) {
                targetType = "task";
                targetId = callbackWebhookManager.getTargetId(webhookId);
            } else {
                targetId = (String) webhookCallback.getSource().get("id");
                targetType = (String) webhookCallback.getSource().get("type");
            }

            switch (targetType) {
                case "file":
                    callbackWebhookManager.processEventFile(webhookId, targetId, webhookCallback.getTrigger());
                    break;
                case "folder":
                    callbackWebhookManager.processEventFolder(webhookId, targetId, webhookCallback.getTrigger());
                    break;
                case "task":
                    callbackWebhookManager.processEventTask(webhookId, targetId, webhookCallback.getTrigger());
                    break;
                default:
                    break;
            }
    }
}

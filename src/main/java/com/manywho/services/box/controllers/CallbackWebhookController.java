package com.manywho.services.box.controllers;

import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.managers.CacheManager;
import com.manywho.services.box.managers.CallbackWebhookManager;
import com.manywho.services.box.services.FlowService;

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

    @Inject
    private CallbackWebhookManager callbackWebhookManager;

    @Inject
    private FlowService flowService;

    @Inject
    private CacheManager cacheManager;

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

        // check if there

        //ExecutionFlowMetadata executionFlowMetadata = cacheManager.getFlowListener(targetType, targetId, webhookReturn.getTrigger());
    }
}

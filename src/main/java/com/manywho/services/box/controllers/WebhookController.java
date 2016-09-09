package com.manywho.services.box.controllers;

import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.managers.WebhookHandlerManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/webhook")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WebhookController {
    private WebhookHandlerManager webhookHandlerManager;

    @Inject
    public WebhookController(WebhookHandlerManager webhookHandlerManager) {

        this.webhookHandlerManager = webhookHandlerManager;
    }

    @Path("/callback")
    @POST
    public void callback(WebhookReturn webhookReturn) throws Exception {

        String webhookId = (String) webhookReturn.getWebhook().get("id");
        String targetId = webhookReturn.getSource().getId();
        String targetType = webhookReturn.getSource().getType();
        String createdByUserId = (String) webhookReturn.getCreatedBy().get("id");

        webhookHandlerManager.handleWebhook(webhookReturn, webhookId, targetId, targetType, createdByUserId);
    }
}
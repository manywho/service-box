package com.manywho.services.box.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.managers.WebhookHandlerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

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
    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());

    @Inject
    private ObjectMapper objectMapper;

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
        try {
            LOGGER.debug("the listener is called");

            webhookHandlerManager.handleWebhook(webhookReturn, webhookId, targetId, targetType, createdByUserId);
        }catch (Exception ex) {
            LOGGER.debug(ex.getMessage());
            LOGGER.info(objectMapper.writeValueAsString(ex));
            throw ex;
        }
    }
}
package com.manywho.services.box.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.client.BoxClient;
import com.manywho.services.box.managers.WebhookHandlerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/webhook")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WebhookController {
    @Inject
    private WebhookHandlerManager webhookHandlerManager;

    @Inject
    private BoxClient boxClient;

    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());

    @Inject
    private ObjectMapper objectMapper;

    @Inject
    public WebhookController(WebhookHandlerManager webhookHandlerManager) {

        this.webhookHandlerManager = webhookHandlerManager;
    }

    @Path("/callback")
    @POST
    public void callback(String payload, @HeaderParam("Box-Delivery-Id") String deliveryId,
                         @HeaderParam("Box-Delivery-Timestamp") String deliveryTimestamp,
                         @HeaderParam("Box-Signature-Algorithm") String algorithm,
                         @HeaderParam("Box-Signature-Primary") String signaturePrimary,
                         @HeaderParam("Box-Signature-Secondary") String signatureSecondary,
                         @HeaderParam("Box-Signature-Version") String signatureVersion) throws Exception {

        WebhookReturn webhookReturn = objectMapper.readValue(payload, WebhookReturn.class);

        String webhookId = (String) webhookReturn.getWebhook().get("id");
        String targetId = webhookReturn.getSource().getId();
        String targetType = webhookReturn.getSource().getType();
        String createdByUserId = (String) webhookReturn.getCreatedBy().get("id");

        if(!boxClient.validateWebhookSignature(signatureVersion, algorithm, signaturePrimary, signatureSecondary,
                payload, deliveryTimestamp)) {

            LOGGER.debug(objectMapper.writeValueAsString(webhookReturn));
            LOGGER.debug("The signature can not be verify");
            throw new RuntimeException("The signature can not be verify");
        }

        try {
            LOGGER.debug(objectMapper.writeValueAsString(webhookReturn));
            webhookHandlerManager.handleWebhook(webhookReturn, webhookId, targetId, targetType, createdByUserId);
        }catch (Exception ex) {
            LOGGER.info(objectMapper.writeValueAsString(ex));
            throw ex;
        }
    }
}
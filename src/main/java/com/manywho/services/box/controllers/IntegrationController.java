package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.entities.webhook.Item;
import com.manywho.services.box.managers.CacheManagerInterface;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/integration")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class IntegrationController extends AbstractController {
    @Inject
    CacheManagerInterface cacheManager;


    @Path("/source-item")
    @POST
    @AuthorizationRequired
    public ServiceResponse fetchItemIntegration(ServiceRequest serviceRequest) throws Exception {
        String ITEM_NAME = com.manywho.services.box.types.Item.NAME;

        Item item = cacheManager.getIntegrationItem(serviceRequest.getStateId());

        // if there isn't any item for this state we don't return the object
        if (item == null) {
            return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
        }

        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", item.getId()));
        properties.add(new Property("Type", item.getType()));

        MObject objectItem = new MObject(ITEM_NAME, item.getId(), properties);

        return new ServiceResponse(InvokeType.Forward, new EngineValue(ITEM_NAME, ContentType.Object, ITEM_NAME, objectItem), serviceRequest.getToken());
    }
}

package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractListenerController;
import com.manywho.services.box.managers.CacheManagerInterface;
import com.manywho.services.box.managers.ListenerManager;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Folder;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import static com.box.sdk.BoxWebHook.*;
import static com.box.sdk.BoxWebHook.Trigger.*;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ListenerController extends AbstractListenerController{

    @Inject
    CacheManagerInterface cacheManager;

    @Inject
    ListenerManager listenerManager;

    @Override
    @AuthorizationRequired
    public void createListener(ListenerServiceRequest listenerServiceRequest) throws Exception {
        Trigger triggerType = fromValue(listenerServiceRequest.getListenType());

        switch(listenerServiceRequest.getValueForListening().getTypeElementDeveloperName()) {
            case File.NAME:
                listenerManager.createListener(getAuthenticatedWho(), listenerServiceRequest, triggerType, "FILE");
                break;
            case Folder.NAME:
                listenerManager.createListener(getAuthenticatedWho(), listenerServiceRequest, triggerType, "FOLDER");
                break;
            default:
                throw new Exception("Target for listener not supported");
        }
    }
}

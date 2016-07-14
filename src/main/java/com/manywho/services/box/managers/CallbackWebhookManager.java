package com.manywho.services.box.managers;

import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.box.services.DatabaseLoadService;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Folder;

import javax.inject.Inject;

public class CallbackWebhookManager {
    @Inject
    private CacheManager cacheManager;

    @Inject
    private DatabaseLoadService databaseLoadService;

    @Inject
    private EventManager eventManager;

    public void processEventFile(String webhookId, String targetId, String triggerType) throws Exception {
        AuthenticatedWho authenticatedWho;
        MObject object;

        for (ListenerServiceRequest request:cacheManager.getListenerServiceRequest(webhookId, triggerType)) {
            authenticatedWho = cacheManager.getAuthenticatedWhoForWebhook(webhookId, request.getStateId());
            object = databaseLoadService.loadFile(authenticatedWho.getToken(), targetId);
            eventManager.sendEvent(request, object, File.NAME);
            eventManager.cleanEvent(authenticatedWho.getToken(), webhookId, "FILE", targetId, triggerType, request.getStateId());
        }
    }

    public void processEventFolder(String webhookId, String targetId, String triggerType) throws Exception {
        AuthenticatedWho authenticatedWho;
        MObject object;

        for (ListenerServiceRequest request:cacheManager.getListenerServiceRequest(webhookId, triggerType)) {
            authenticatedWho = cacheManager.getAuthenticatedWhoForWebhook(webhookId, request.getStateId());
            object = databaseLoadService.loadFolder(authenticatedWho.getToken(), targetId);
            eventManager.sendEvent(request, object, Folder.NAME);
            eventManager.cleanEvent(authenticatedWho.getToken(), webhookId, "FOLDER", targetId, triggerType, request.getStateId());
        }
    }
}

package com.manywho.services.box.managers;

import com.manywho.sdk.RunService;
import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceResponse;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.enums.ContentType;

import javax.inject.Inject;

public class EventManager {
    @Inject
    private WebhookManager webhookManager;

    @Inject
    private RunService runService;

    @Inject
    private CacheManager cacheManager;

    public void sendEvent(ListenerServiceRequest listenerServiceRequest, MObject object, String name) throws Exception {
        ListenerServiceResponse listenerServiceResponse = new ListenerServiceResponse();
        EngineValue fileValue = new EngineValue(name, ContentType.Object, name, object);
        listenerServiceResponse.setListeningEventValue(fileValue);
        listenerServiceResponse.setToken(listenerServiceRequest.getToken());
        listenerServiceResponse.setAnnotations(listenerServiceRequest.getAnnotations());
        listenerServiceResponse.setCulture(listenerServiceRequest.getCulture());
        listenerServiceResponse.setTenantId(listenerServiceRequest.getTenantId());

        runService.sendEvent(null, null, listenerServiceRequest.getTenantId(), listenerServiceRequest.getCallbackUri(), listenerServiceResponse);
    }

    public void cleanEvent(String userToken, String webhookId, String targetType, String targetId, String triggerType, String stateId) throws Exception {
        cacheManager.deleteListenerServiceRequest(webhookId, triggerType);
        cacheManager.deleteAuthenticatedWhoForWebhook(webhookId, stateId);

        if( !cacheManager.areAnyListenerServiceRequestForThisWebhook(webhookId) && cacheManager.getFlowListener(targetType, targetId, triggerType) == null) {
            webhookManager.deleteWebhook(userToken, webhookId);
            cacheManager.deleteWebhook(targetType, targetId);
        }
    }
}

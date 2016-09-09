package com.manywho.services.box.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.manywho.sdk.entities.run.EngineInitializationResponse;
import com.manywho.sdk.entities.run.EngineInvokeRequest;
import com.manywho.sdk.entities.run.EngineInvokeResponse;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.run.elements.map.MapElementInvokeRequest;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.services.DatabaseLoadService;
import com.manywho.services.box.services.FlowService;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Folder;
import com.manywho.services.box.types.Task;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CallbackWebhookManager {
    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());
    @Inject
    private CacheManagerInterface cacheManager;

    @Inject
    private DatabaseLoadService databaseLoadService;

    @Inject
    private EventManager eventManager;

    @Inject
    private FlowService flowService;

    @Inject
    private ObjectMapper objectMapper;

    public void processEventFile(String webhookId, String targetId, String triggerType) throws Exception {
        AuthenticatedWho authenticatedWho;
        MObject object;
        LOGGER.debug("processEventFile");

        for (ListenerServiceRequest request:cacheManager.getListenerServiceRequest(webhookId, triggerType)) {
            LOGGER.debug(objectMapper.writeValueAsString(request));
            authenticatedWho = cacheManager.getAuthenticatedWhoForWebhook(webhookId, request.getStateId());

            if(Objects.equals(request.getValueForListening().getTypeElementDeveloperName(), "Folder")) {
                LOGGER.debug("webhook in folder");
                object = databaseLoadService.loadFolder(authenticatedWho.getToken(),
                        request.getValueForListening().getObjectData().get(0).getExternalId());

                LOGGER.debug(objectMapper.writeValueAsString(object));
                eventManager.sendEvent(request, object, Folder.NAME);
                eventManager.cleanEvent(authenticatedWho.getToken(), webhookId, "FOLDER", targetId, triggerType, request.getStateId(), getFolderIdFromRequest(request));
            } else {
                LOGGER.debug("webhook in file");
                object = databaseLoadService.loadFile(authenticatedWho.getToken(), targetId);
                LOGGER.debug(objectMapper.writeValueAsString(object));
                eventManager.sendEvent(request, object, File.NAME);
                eventManager.cleanEvent(authenticatedWho.getToken(), webhookId, "FILE", targetId, triggerType, request.getStateId(), null);
            }
        }
    }

    public String getFolderIdFromRequest(ListenerServiceRequest request) {
        return request.getValueForListening().getObjectData().get(0).getExternalId();
    }

    public void processEventTask(String webhookId, String targetId, String triggerType) throws Exception {
        AuthenticatedWho authenticatedWho;
        MObject object;

        for (ListenerServiceRequest request:cacheManager.getListenerServiceRequest(webhookId, triggerType)) {
            authenticatedWho = cacheManager.getAuthenticatedWhoForWebhook(webhookId, request.getStateId());

            if(Objects.equals(request.getValueForListening().getTypeElementDeveloperName(), "File")) {

                object = databaseLoadService.loadFile(authenticatedWho.getToken(),
                        request.getValueForListening().getObjectData().get(0).getExternalId());
                eventManager.sendEvent(request, object, File.NAME);
                eventManager.cleanEvent(authenticatedWho.getToken(), webhookId, "FILE", targetId, triggerType, request.getStateId(), getFolderIdFromRequest(request));

            } else if (Objects.equals(request.getValueForListening().getTypeElementDeveloperName(), "Folder")) {
                object = databaseLoadService.loadFolder(authenticatedWho.getToken(),
                        request.getValueForListening().getObjectData().get(0).getExternalId());

                eventManager.sendEvent(request, object, Folder.NAME);
                eventManager.cleanEvent(authenticatedWho.getToken(), webhookId, "FOLDER", targetId, triggerType, request.getStateId(), getFolderIdFromRequest(request));

            } else {
                object = databaseLoadService.loadTaskAssignment(authenticatedWho.getToken(), targetId);
                eventManager.sendEvent(request, object, Task.NAME);
                eventManager.cleanEvent(authenticatedWho.getToken(), webhookId, "TASK", targetId, triggerType, request.getStateId(), null);
            }


        }
    }

    public void processEventFolder(String webhookId, String targetId, String triggerType) throws Exception {
        AuthenticatedWho authenticatedWho;
        MObject object;

        for (ListenerServiceRequest request:cacheManager.getListenerServiceRequest(webhookId, triggerType)) {
            authenticatedWho = cacheManager.getAuthenticatedWhoForWebhook(webhookId, request.getStateId());
            object = databaseLoadService.loadFolder(authenticatedWho.getToken(), targetId);
            eventManager.sendEvent(request, object, Folder.NAME);
            eventManager.cleanEvent(authenticatedWho.getToken(), webhookId, "FOLDER", targetId, triggerType, request.getStateId(), null);
        }
    }

    public void processEventForFlow(String boxWebhookCreatorId, String targetType, String targetId, String triggerType) throws Exception {
        ExecutionFlowMetadata executionFlowMetadata = cacheManager.getFlowListener(targetType, targetId, triggerType);
        if (executionFlowMetadata == null) return;

        AuthenticatedWho authenticationWho = getAuthenticatedWhoObject(cacheManager.getFlowHeaderByUser(boxWebhookCreatorId));
        try {
            EngineInitializationResponse flow;
            flow = flowService.initializeFlowWithoutAuthentication(executionFlowMetadata);
            String code = flowService.getFlowAuthenticationCode(flow.getStateId(), executionFlowMetadata.getTenantId(), authenticationWho, null, null, null, null);
            flow = flowService.initializeFlowWithAuthentication(executionFlowMetadata, executionFlowMetadata.getTenantId(), targetType, targetId, code);

            EngineInvokeRequest engineInvokeRequest = new EngineInvokeRequest();
            engineInvokeRequest.setStateId(flow.getStateId());
            engineInvokeRequest.setInvokeType(InvokeType.Forward);
            engineInvokeRequest.setStateToken(flow.getStateToken());
            engineInvokeRequest.setStateId(flow.getStateId());
            engineInvokeRequest.setCurrentMapElementId(flow.getCurrentMapElementId());
            engineInvokeRequest.setMapElementInvokeRequest(new MapElementInvokeRequest());

            EngineInvokeResponse engineInvokeResponse = flowService.executeFlow(executionFlowMetadata.getTenantId(), code, engineInvokeRequest);
        } catch (Exception e) {
            throw e;
        }
    }

    public void processEventTaskForFlow(String boxWebhookCreatorId, String targetType, String targetId,
                                        String triggerType, String itemId, String itemType) throws Exception {

        ExecutionFlowMetadata executionFlowMetadata = cacheManager.getFlowListener(itemType, itemId, triggerType);
        if (executionFlowMetadata == null) return;

        AuthenticatedWho authenticationWho = getAuthenticatedWhoObject(cacheManager.getFlowHeaderByUser(boxWebhookCreatorId));
        try {
            EngineInitializationResponse flow;
            flow = flowService.initializeFlowWithoutAuthentication(executionFlowMetadata);
            String code = flowService.getFlowAuthenticationCode(flow.getStateId(), executionFlowMetadata.getTenantId(), authenticationWho, null, null, null, null);
            flow = flowService.initializeFlowWithAuthentication(executionFlowMetadata, executionFlowMetadata.getTenantId(), targetType, targetId, code);

            EngineInvokeRequest engineInvokeRequest = new EngineInvokeRequest();
            engineInvokeRequest.setStateId(flow.getStateId());
            engineInvokeRequest.setInvokeType(InvokeType.Forward);
            engineInvokeRequest.setStateToken(flow.getStateToken());
            engineInvokeRequest.setStateId(flow.getStateId());
            engineInvokeRequest.setCurrentMapElementId(flow.getCurrentMapElementId());
            engineInvokeRequest.setMapElementInvokeRequest(new MapElementInvokeRequest());

            EngineInvokeResponse engineInvokeResponse = flowService.executeFlow(executionFlowMetadata.getTenantId(), code, engineInvokeRequest);
        } catch (Exception e) {
            throw e;
        }
    }

    private AuthenticatedWho getAuthenticatedWhoObject(String authorizationHeader) {
        if (authorizationHeader == null) {
            return null;
        }

        String decodedHeader;

        try {
            decodedHeader = URLDecoder.decode(authorizationHeader, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to deserialize the incoming AuthenticatedWho", e);
        }

        Map<String, String> pairs = URLEncodedUtils.parse(decodedHeader, Charsets.UTF_8)
                .stream()
                .map(pair -> new BasicNameValuePair(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, pair.getName()), pair.getValue()))
                .collect(Collectors.toMap(
                        entry -> CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, entry.getName()),
                        BasicNameValuePair::getValue
                ));

        return objectMapper.convertValue(pairs, AuthenticatedWho.class);
    }

}
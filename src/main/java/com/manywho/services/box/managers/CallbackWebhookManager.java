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
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.stream.Collectors;

public class CallbackWebhookManager {
    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());

    @Inject
    private CacheManager cacheManager;

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

    public void processEventFileForFlow(String boxWebhookCreatorId, String targetType, String targetId, String triggerType) throws Exception {
        ExecutionFlowMetadata executionFlowMetadata = cacheManager.getFlowListener(targetType, targetId, triggerType);
        LOGGER.info(objectMapper.writeValueAsString(executionFlowMetadata));

        if (executionFlowMetadata == null) return;

        AuthenticatedWho authenticationWho = getAuthenticatedWhoObject(cacheManager.getFlowHeaderByUser(boxWebhookCreatorId));
        try {
            EngineInitializationResponse flow;
            flow = flowService.initializeFlowWithoutAuthentication(executionFlowMetadata);
            LOGGER.info(objectMapper.writeValueAsString(flow));
            String code = flowService.getFlowAuthenticationCode(flow.getStateId(), authenticationWho, null, null, null, null);
            LOGGER.info(objectMapper.writeValueAsString(code));
            flow = flowService.initializeFlowWithAuthentication(executionFlowMetadata, targetType, targetId, code);
            LOGGER.info(objectMapper.writeValueAsString(flow));
            EngineInvokeRequest engineInvokeRequest = new EngineInvokeRequest();
            engineInvokeRequest.setStateId(flow.getStateId());
            engineInvokeRequest.setInvokeType(InvokeType.Forward);
            engineInvokeRequest.setStateToken(flow.getStateToken());
            engineInvokeRequest.setStateId(flow.getStateId());
            engineInvokeRequest.setCurrentMapElementId(flow.getCurrentMapElementId());
            engineInvokeRequest.setMapElementInvokeRequest(new MapElementInvokeRequest());

            EngineInvokeResponse engineInvokeResponse = flowService.executeFlow(executionFlowMetadata.getTenantId(), code, engineInvokeRequest);
            LOGGER.info(objectMapper.writeValueAsString(engineInvokeResponse));
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
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
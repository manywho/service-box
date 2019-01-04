package com.manywho.services.box.managers;

import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.entities.webhook.Item;

import java.util.List;

public interface CacheManagerInterface {

    void saveFlowHeaderByUser(String boxUserId, String header) throws Exception;

    String getFlowHeaderByUser(String boxUserId) throws Exception;

    void saveUserIdByTokenKey(String accessTokenKey, String userId) throws Exception;

    String getUserIdByTokenKey(String accessTokenKey) throws Exception;

    Credentials getCredentialsByTokenKey(String tokenKey) throws Exception;

    void saveCredentials(String boxUserId, Credentials credentials) throws Exception;

    Credentials getCredentials(String boxUserId) throws Exception;

    void saveFlowListener(String targetType, String targetId, String trigger, ExecutionFlowMetadata executionFlowMetadata) throws Exception;

    ExecutionFlowMetadata getFlowListener(String targetType, String targetId, String trigger) throws Exception;

    void deleteFlowListener(String targetType, String targetId);

    void saveWebhook(String targetType, String targetId, String webhookId) throws Exception;

    String getWebhook(String targetType, String targetId) throws Exception;

    void deleteWebhook(String targetType, String targetId);

    void saveListenerServiceRequest(String webhookId, String trigger, String stateId, ListenerServiceRequest listenerServiceRequest) throws Exception;

    void deleteListenerServiceRequest(String webhookId, String trigger);

    List<ListenerServiceRequest> getListenerServiceRequest(String webhookId, String trigger) throws Exception;

    Boolean areAnyListenerServiceRequestForThisWebhook(String webhookId) throws Exception;

    AuthenticatedWho getAuthenticatedWhoForWebhook(String webhookId, String state) throws Exception;

    void saveAuthenticatedWhoForWebhook(String webhookId, String state, AuthenticatedWho authenticatedWho) throws Exception;

    void deleteAuthenticatedWhoForWebhook(String webhookId, String state);

    void saveIntegrationItem(String state, Item item) throws Exception;

    Item getIntegrationItem(String state) throws Exception;
}

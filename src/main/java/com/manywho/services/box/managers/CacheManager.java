package com.manywho.services.box.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CacheManager {
    private final static String REDIS_BOX_LISTENER_REQUEST = "service:box:listener-request:webhook:%s:trigger:%s:state:%s";
    private final static String REDIS_BOX_LISTENER_REQUEST_SEARCH_TRIGGERS = "service:box:listener-request:webhook:%s:trigger:%s:*";
    private final static String REDIS_BOX_LISTENER_REQUEST_SEARCH = "service:box:listener-request:webhook:%s:*";
    private final static String REDIS_BOX_AUTHENTICATEDWHO = "service:box:autenticatedwho:webhook:%s:state:%s";
    private final static String REDIS_BOX_WEBHOOK = "service:box:webhook:targettype:%s:targetid:%s";

    @Inject
    private JedisPool jedisPool;

    @Inject
    private ObjectMapper objectMapper;

    public void saveWebhook(String targetType, String targetId, String webhookId) throws Exception {
        String key = String.format(REDIS_BOX_WEBHOOK, targetType, targetId);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(webhookId));
        }
    }

    public String getWebhook(String targetType, String targetId) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String webhookId = jedis.get(String.format(REDIS_BOX_WEBHOOK, targetType, targetId));

            if (StringUtils.isNotEmpty(webhookId)) {
                return objectMapper.readValue(webhookId, String.class);
            }
        }

        return null;
    }

    public void deleteWebhook(String targetType, String targetId) {
        String key = String.format(REDIS_BOX_WEBHOOK, targetType, targetId);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    public void saveListenerServiceRequest(String webhookId, String trigger, String stateId, ListenerServiceRequest listenerServiceRequest) throws Exception {
        String key = String.format(REDIS_BOX_LISTENER_REQUEST, webhookId, trigger, stateId);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(listenerServiceRequest));
        }
    }

    public void deleteListenerServiceRequest(String webhookId, String trigger) {
        try (Jedis jedis = jedisPool.getResource()) {
            ScanParams params = new ScanParams();
            params.match(String.format(REDIS_BOX_LISTENER_REQUEST_SEARCH_TRIGGERS, webhookId, trigger));
            ScanResult<String> scanResult = jedis.scan("0", params);
            List<String> keys = scanResult.getResult();

            for (String key:keys) {
                jedis.del(key);
            }
        }
    }

    public List<ListenerServiceRequest> getListenerServiceRequest(String webhookId, String trigger) throws Exception {

        List<ListenerServiceRequest> listenerServiceRequest = new ArrayList<>();

        try (Jedis jedis = jedisPool.getResource()) {
            ScanParams params = new ScanParams();
            params.match(String.format(REDIS_BOX_LISTENER_REQUEST_SEARCH_TRIGGERS, webhookId, trigger));
            ScanResult<String> scanResult = jedis.scan("0", params);
            List<String> keys = scanResult.getResult();

            for (String key:keys) {
                String json = jedis.get(key);

                if (StringUtils.isNotEmpty(json)) {
                    listenerServiceRequest.add(objectMapper.readValue(json, ListenerServiceRequest.class));
                }
            }
        }

        return listenerServiceRequest;
    }

    public Boolean areAnyListenerServiceRequestForThisWebhook(String webhookId) throws Exception {

        try (Jedis jedis = jedisPool.getResource()) {
            ScanParams params = new ScanParams();
            params.match(String.format(REDIS_BOX_LISTENER_REQUEST_SEARCH, webhookId));
            ScanResult<String> scanResult = jedis.scan("0", params);

            return scanResult.getResult().size() > 0;
        }
    }

    public AuthenticatedWho getAuthenticatedWhoForWebhook(String webhookId, String state) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = jedis.get(String.format(REDIS_BOX_AUTHENTICATEDWHO, webhookId, state));

            if (StringUtils.isNotEmpty(json)) {
                return objectMapper.readValue(json, AuthenticatedWho.class);
            }
        }

        throw new Exception("Could not find any stored authenticatedWho for webhook with ID: " + webhookId);
    }

    public void saveAuthenticatedWhoForWebhook(String webhookId, String state, AuthenticatedWho authenticatedWho) throws Exception {
        String key = String.format(REDIS_BOX_AUTHENTICATEDWHO, webhookId, state);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(authenticatedWho));
        }
    }

    public void deleteAuthenticatedWhoForWebhook(String webhookId, String state) {
        String key = String.format(REDIS_BOX_AUTHENTICATEDWHO, webhookId, state);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }
}

package com.manywho.services.box.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class CacheManager implements CacheManagerInterface{
    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());
    protected final static String REDIS_BOX_LISTENER_REQUEST =                 "service:box:listener-request:webhook:%s:trigger:%s:state:%s";
    protected final static String REDIS_BOX_LISTENER_REQUEST_SEARCH_TRIGGERS = "service:box:listener-request:webhook:%s:trigger:%s:state:*";
    protected final static String REDIS_BOX_LISTENER_REQUEST_SEARCH =          "service:box:listener-request:webhook:%s:*";

    protected final static String REDIS_BOX_AUTHENTICATEDWHO = "service:box:autenticatedwho:webhook:%s:state:%s";

    protected final static String REDIS_BOX_WEBHOOK = "service:box:webhook:targettype:%s:targetid:%s";

    protected final static String REDIS_BOX_FLOW_LISTENING = "service:box:listen:targettype:%s:targetid:%s:trigger:%s";

    protected final static String REDIS_BOX_CREDENTIALS = "service:box:user:%s:credentials";
    protected final static String REDIS_BOX_TOKEN_AS_A_KEY = "service:box:user:token:%s";
    protected final static String REDIS_BOX_FLOW_HEADER ="service:box:box-userid:%s:flow-auth-header";

    private JedisPool jedisPool;
    private ObjectMapper objectMapper;

    @Inject
    public CacheManager(JedisPool jedisPool, ObjectMapper objectMapper) {
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

    public void saveFlowHeaderByUser(String boxUserId, String header) throws Exception {
        String key = String.format(REDIS_BOX_FLOW_HEADER, boxUserId);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(header));
        }
    }

    public String getFlowHeaderByUser(String boxUserId) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String credentials = jedis.get(String.format(REDIS_BOX_FLOW_HEADER, boxUserId));

            if (StringUtils.isNotEmpty(credentials)) {
                return objectMapper.readValue(credentials, String.class);
            }
        }

        return null;
    }

    public void saveUserIdByTokenKey(String accessTokenKey, String userId) throws Exception {
        String key = String.format(REDIS_BOX_TOKEN_AS_A_KEY, accessTokenKey);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(userId));
        }
    }

    public String getUserIdByTokenKey(String accessTokenKey) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String credentials = jedis.get(String.format(REDIS_BOX_TOKEN_AS_A_KEY, accessTokenKey));

            if (StringUtils.isNotEmpty(credentials)) {
                return objectMapper.readValue(credentials, String.class);
            }
        }

        return null;
    }

    public Credentials getCredentialsByTokenKey(String tokenKey) throws Exception {
        return getCredentials(this.getUserIdByTokenKey(tokenKey));
    }

    public void saveCredentials(String boxUserId, Credentials credentials) throws Exception {
        String key = String.format(REDIS_BOX_CREDENTIALS, boxUserId);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(credentials));
        }
    }

    public Credentials getCredentials(String boxUserId) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String credentials = jedis.get(String.format(REDIS_BOX_CREDENTIALS, boxUserId));

            if (StringUtils.isNotEmpty(credentials)) {
                return objectMapper.readValue(credentials, Credentials.class);
            }
        }

        return null;
    }

    public void saveFlowListener(String targetType, String targetId, String trigger, ExecutionFlowMetadata executionFlowMetadata) throws Exception {
        String key = String.format(REDIS_BOX_FLOW_LISTENING, targetType, targetId, trigger);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(executionFlowMetadata));
        }
    }

    public ExecutionFlowMetadata getFlowListener(String targetType, String targetId, String trigger) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String executionFlowMetadata = jedis.get(String.format(REDIS_BOX_FLOW_LISTENING, targetType, targetId, trigger));

            if (StringUtils.isNotEmpty(executionFlowMetadata)) {
                return objectMapper.readValue(executionFlowMetadata, ExecutionFlowMetadata.class);
            }
        }

        return null;
    }

    /**
     * not used at the moment, but there is a limit in the number of triggers that can be used
     * so we should allow to delete listeners
     *
     * @param targetType
     * @param targetId
     * @param trigger
     */
    public void deleteFlowListener(String targetType, String targetId, String trigger) {
        String key = String.format(REDIS_BOX_FLOW_LISTENING, targetType, targetId, trigger);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

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
        LOGGER.debug("saveListenerServiceRequest : " + key );
        LOGGER.debug(objectMapper.writeValueAsString(listenerServiceRequest));
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, objectMapper.writeValueAsString(listenerServiceRequest));
            LOGGER.debug(jedis.get(key));
            LOGGER.debug("end saveListenerServiceRequest " );
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
        String pattern = String.format(REDIS_BOX_LISTENER_REQUEST_SEARCH_TRIGGERS, webhookId, trigger);

        try (Jedis jedis = jedisPool.getResource()) {
            ScanParams params = new ScanParams();
            params.match(pattern);
            ScanResult<String> scanResult = jedis.scan("0", params);
            List<String> keys = scanResult.getResult();

            for (String key:keys) {
                String json = jedis.get(key);

                if (StringUtils.isNotEmpty(json)) {
                    listenerServiceRequest.add(objectMapper.readValue(json, ListenerServiceRequest.class));
                }
            }
        }
        LOGGER.debug("getListenerServiceRequest : " + pattern );
        LOGGER.debug(objectMapper.writeValueAsString(listenerServiceRequest));

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

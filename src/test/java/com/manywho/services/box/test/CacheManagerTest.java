package com.manywho.services.box.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import com.manywho.services.box.managers.CacheManager;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Some methods are not supported by jedis mock (like scan method) so I have created this class to do the same
 * functionality (less efficient) in a way that can be supported by the mock jedis
 */
public class CacheManagerTest extends CacheManager {
    private JedisPool jedisPool;
    private ObjectMapper objectMapper;
    @Inject
    public CacheManagerTest(JedisPool jedisPool, ObjectMapper objectMapper) {
        super(jedisPool, objectMapper);
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

    /**
     * We can not mock scan search using jedis mock, so we are using the keys in the test, but this should never be
     * used in production
     *
     * @param webhookId
     * @param trigger
     * @return
     * @throws Exception
     */
    @Override
    public List<ListenerServiceRequest> getListenerServiceRequest(String webhookId, String trigger) throws Exception {

        List<ListenerServiceRequest> listenerServiceRequest = new ArrayList<>();

        try (Jedis jedis = jedisPool.getResource()) {
            String pattern = String.format(REDIS_BOX_LISTENER_REQUEST_SEARCH_TRIGGERS, webhookId, trigger);
            Set<String> keys = jedis.keys(pattern);

            for (String key:keys) {
                String json = jedis.get(key);

                if (StringUtils.isNotEmpty(json)) {
                    listenerServiceRequest.add(objectMapper.readValue(json, ListenerServiceRequest.class));
                }
            }
        }

        return listenerServiceRequest;
    }


    /**
     * We can not mock scan search using jedis mock, so we are using the keys in the test, but this should never be
     * used in production
     *
     * @param webhookId
     * @param trigger
     */
    @Override
    public void deleteListenerServiceRequest(String webhookId, String trigger) {
        try (Jedis jedis = jedisPool.getResource()) {
            String pattern = String.format(REDIS_BOX_LISTENER_REQUEST_SEARCH_TRIGGERS, webhookId, trigger);
            Set<String> keys = jedis.keys(pattern);

            for (String key : keys) {
                jedis.del(key);
            }
        }
    }

    public Boolean areAnyListenerServiceRequestForThisWebhook(String webhookId) throws Exception {
        try (Jedis jedis = jedisPool.getResource()) {
            String pattern = String.format(REDIS_BOX_LISTENER_REQUEST_SEARCH, webhookId);
            Set<String> keys = jedis.keys(pattern);
            return keys.size()>0;
        }
    }
}

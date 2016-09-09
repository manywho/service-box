package com.manywho.services.box.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manywho.sdk.entities.run.elements.config.ListenerServiceRequest;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class have been created to temporary solution for scan problem in tickcet CORE-2605
 * todo this should be removed and used instead CacheManager.class
 */
public class CacheManagerWithoutScan extends CacheManager {
    private JedisPool jedisPool;
    private ObjectMapper objectMapper;
    @Inject
    public CacheManagerWithoutScan(JedisPool jedisPool, ObjectMapper objectMapper) {
        super(jedisPool, objectMapper);
        this.jedisPool = jedisPool;
        this.objectMapper = objectMapper;
    }

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

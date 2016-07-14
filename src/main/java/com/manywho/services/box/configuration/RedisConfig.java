package com.manywho.services.box.configuration;

import com.manywho.sdk.services.config.RedisConfiguration;
import com.manywho.sdk.services.config.ServiceConfiguration;

import javax.inject.Inject;

public class RedisConfig implements RedisConfiguration {
    @Inject
    private ServiceConfiguration configuration;

    @Override
    public String getEndpoint() {
        return configuration.get("redis.url");
    }

    @Override
    public int getPort() {
        return 6379;
    }
}

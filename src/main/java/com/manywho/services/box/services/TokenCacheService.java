package com.manywho.services.box.services;

import com.box.sdk.IAccessTokenCache;
import com.box.sdk.InMemoryLRUAccessTokenCache;

public class TokenCacheService {
    public IAccessTokenCache  getAccessTokenCache() {
        return new InMemoryLRUAccessTokenCache(100);
    }
}

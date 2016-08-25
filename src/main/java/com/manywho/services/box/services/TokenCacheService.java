package com.manywho.services.box.services;

import com.box.sdk.IAccessTokenCache;

public class TokenCacheService {
    public IAccessTokenCache  getAccessTokenCache() {
        return null;
        //return new InMemoryLRUAccessTokenCache(300);
    }
}

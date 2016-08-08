package com.manywho.services.box.services;

import com.box.sdk.IAccessTokenCache;
import com.box.sdk.InMemoryLRUAccessTokenCache;

/**
 * Created by Jose on 08/08/2016.
 */
public class TokenCacheService {
    public IAccessTokenCache  getAccessTokenCache() {
        return new InMemoryLRUAccessTokenCache(100);
    }
}

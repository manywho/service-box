package com.manywho.services.box.test;

import com.box.sdk.BoxJSONResponse;
import com.box.sdk.InMemoryLRUAccessTokenCache;
import com.box.sdk.RequestInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiftyonred.mock_jedis.MockJedis;
import com.fiftyonred.mock_jedis.MockJedisPool;
import com.google.common.io.Resources;
import com.manywho.sdk.client.raw.RawRunClient;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.services.providers.ObjectMapperProvider;
import com.manywho.sdk.test.FunctionalTest;
import com.manywho.sdk.test.MockFactory;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.managers.CacheManagerInterface;
import com.manywho.services.box.services.TokenCacheService;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.test.TestProperties;
import org.json.JSONException;
import redis.clients.jedis.JedisPool;

import javax.inject.Singleton;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BoxServiceFunctionalTest extends FunctionalTest {

    protected MockJedis mockJedis;
    protected SecurityConfiguration mockSecurityConfiguration;
    protected RequestIntersectorTestsImpl requestIntersectorTests;
    protected TokenCacheService mockTokenCacheService;
    protected HttpClientUnirestForTest httpClientUnirestForTest;
    protected RawRunClient rawRunClient;
    protected HttpClientApacheForTest httpClientApacheForTest;

    @Override
    protected javax.ws.rs.core.Application configure(){
        //take first available port, to fix problem in deployment
        forceSet(TestProperties.CONTAINER_PORT, "0");
        System.setProperty("user.timezone", "UTC");
        MockJedisPool mockJedisPool = new MockJedisPool(new GenericObjectPoolConfig(), "localhost");
        mockJedis = (MockJedis) mockJedisPool.getResource();
        mockSecurityConfiguration = mock(SecurityConfiguration.class);
        mockSecurityConfigurationResponses();
        mockTokenCacheService = mock(TokenCacheService.class);
        mockTokenCache();
        requestIntersectorTests = new RequestIntersectorTestsImpl();

        httpClientUnirestForTest = new HttpClientUnirestForTest();
        httpClientApacheForTest = new HttpClientApacheForTest();

        rawRunClient = new RawRunClient(httpClientApacheForTest);

        CacheManagerInterface cacheManager = new CacheManagerTest(mockJedisPool, new ObjectMapper());

        return new com.manywho.services.box.Application().register(new AbstractBinder() {

            @Override
            protected void configure() {
                bindFactory(new MockFactory<MockJedisPool>(mockJedisPool)).to(JedisPool.class).ranked(1);
                bindFactory(new MockFactory<RequestIntersectorTestsImpl>(requestIntersectorTests)).to(RequestInterceptor.class).ranked(1);
                bindFactory(new MockFactory<SecurityConfiguration>(mockSecurityConfiguration)).to(SecurityConfiguration.class).in(Singleton.class).ranked(1);
                bindFactory(new MockFactory<TokenCacheService>(mockTokenCacheService)).to(TokenCacheService.class).ranked(1);
                bindFactory(new MockFactory<CacheManagerInterface>(cacheManager)).to(CacheManagerInterface.class).ranked(1);
                bindFactory(new MockFactory<RawRunClient>(rawRunClient)).to(RawRunClient.class).ranked(1);
            }
        });
    }

    private void mockSecurityConfigurationResponses() {
        when(mockSecurityConfiguration.getOauth2ContentApiClientId()).thenReturn("xxx");
        when(mockSecurityConfiguration.getOauth2ContentApiClientSecret()).thenReturn("yyy");
        when(mockSecurityConfiguration.getOauth2DeveloperEditionClientId()).thenReturn("zzz");
        when(mockSecurityConfiguration.getOauth2DeveloperEditionClientSecret()).thenReturn("www");
        try {
            String credentialsPath = Resources.getResource("credentials/example-credentials.test").toURI().getPath();
            when(mockSecurityConfiguration.getPrivateKeyLocation()).thenReturn(credentialsPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        when(mockSecurityConfiguration.getPrivateKeyPassword()).thenReturn("ppp");
    }

    private void mockTokenCache() {
        InMemoryLRUAccessTokenCache mockInMemoryLRUAccessTokenCache = mock(InMemoryLRUAccessTokenCache.class);
        when(mockInMemoryLRUAccessTokenCache.get(anyString())).thenReturn(
                String.format("{\"accessToken\": \"%s\",\"lastRefresh\": %s, \"expires\": %s}",
                        "aaa",
                        System.currentTimeMillis(),
                        9000000
                ));
        when(mockTokenCacheService.getAccessTokenCache()).thenReturn(mockInMemoryLRUAccessTokenCache);
    }

    public BoxJSONResponse createBoxApiResponse(String path, int code) throws IOException, URISyntaxException, JSONException {
        InputStream stream = new ByteArrayInputStream(getJsonFormatFileContent(path).getBytes(StandardCharsets.UTF_8));
        HttpURLConnection connection = mock(HttpURLConnection.class);

        when(connection.getResponseCode()).thenReturn(code);
        when(connection.getInputStream()).thenReturn(stream);
        when(connection.getContentEncoding()).thenReturn("UTF-8");

        return new BoxJSONResponse(connection);
    }

    protected static Entity<ObjectDataRequest> getObjectDataRequestFromFile(String filePath) throws URISyntaxException, IOException {
        ObjectDataRequest objectDataRequest = ObjectMapperProvider
                .getObjectMapper()
                .readValue(
                        getFile(filePath),
                        ObjectDataRequest.class
                );

        return Entity.entity(objectDataRequest, MediaType.APPLICATION_JSON_TYPE);
    }

    protected static Entity<WebhookReturn> getWebhookFromFile(String filePath) throws URISyntaxException, IOException {
        WebhookReturn webhookReturn = ObjectMapperProvider.getObjectMapper().readValue(getFile(filePath), WebhookReturn.class);
        return Entity.entity(webhookReturn, MediaType.APPLICATION_JSON_TYPE);
    }
}

package com.manywho.services.box.facades;

import com.box.sdk.*;
import com.box.sdk.BoxWebHook.Trigger;
import com.google.common.collect.Lists;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.entities.MetadataSearch;
import com.manywho.services.box.managers.CacheManager;
import com.manywho.services.box.services.TokenCacheService;
import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BoxFacade {
    private final SecurityConfiguration securityConfiguration;
    private com.box.sdk.RequestInterceptor requestInterceptor;
    private TokenCacheService tokenCacheService;
    private CacheManager cacheManager;

    @Inject
    public BoxFacade(SecurityConfiguration securityConfiguration, RequestInterceptor requestInterceptor,
                     TokenCacheService tokenCacheService, CacheManager cacheManager) {

        this.securityConfiguration = securityConfiguration;
        this.requestInterceptor = requestInterceptor;
        this.tokenCacheService = tokenCacheService;
        this.cacheManager = cacheManager;
    }

    public BoxAPIConnection authenticateUser(String clientId, String clientSecret, String authorizationCode) {
        return createApiConnection(clientId, clientSecret, authorizationCode);
    }

    public BoxAPIConnection confirmUserAuthentication(String accessToken) {
        return createApiConnection(accessToken);
    }

    public BoxDeveloperEditionAPIConnection createDeveloperApiConnection(String enterpriseId) throws IOException {
        String privateKey = new String(Files.readAllBytes(Paths.get(securityConfiguration.getPrivateKeyLocation())));

        JWTEncryptionPreferences encryptionPreferences = new JWTEncryptionPreferences();
        encryptionPreferences.setEncryptionAlgorithm(EncryptionAlgorithm.RSA_SHA_256);
        encryptionPreferences.setPrivateKey(privateKey);
        encryptionPreferences.setPrivateKeyPassword(securityConfiguration.getPrivateKeyPassword());

        return BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(
                enterpriseId,
                securityConfiguration.getOauth2DeveloperEditionClientId(),
                securityConfiguration.getOauth2DeveloperEditionClientSecret(),
                encryptionPreferences,
                tokenCacheService.getAccessTokenCache()
        );
    }

    public BoxUser.Info getCurrentUser(String accessToken) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxUser.Info boxUser =  BoxUser.getCurrentUser(boxAPIConnection).getInfo();
        boxAPIConnection.getRefreshToken();

        return boxUser;
    }

    public BoxFolder getFolder(String accessToken, String id) {
        return new BoxFolder(createApiConnection(accessToken), id);
    }

    public BoxFile getFile(String accessToken, String id) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxFile boxFile = new BoxFile(boxAPIConnection, id);
        updateCredentials(boxAPIConnection, accessToken);

        return boxFile;
    }

    public BoxAPIConnection getValidBoxApiConnection(String accessToken, String refreshToken) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxUser.getCurrentUser(boxAPIConnection);
        updateCredentials(boxAPIConnection, accessToken);

        return boxAPIConnection;
    }

    public Iterable<BoxGroup.Info> loadGroups(String accessToken) {
        return BoxGroup.getAllGroups(createApiConnection(accessToken));
    }

    public Iterable<BoxUser.Info> loadUsers(String accessToken) {
        return BoxUser.getAllEnterpriseUsers(createApiConnection(accessToken));
    }

    public Iterable<BoxItem.Info> searchByMetadata(String accessToken, String metadataType, MetadataSearch metadataSearch) {
        BoxMetadataFilter metadataFilter = new BoxMetadataFilter();
        metadataFilter.setScope("enterprise");
        metadataFilter.setTemplateKey(metadataType);

        // Add filters for each of the fields in the metadataSearch object
        for (Map.Entry<String, String> field : metadataSearch.getFields().entrySet()) {
            metadataFilter.addFilter(field.getKey(), field.getValue());
        }

        BoxSearchParameters searchParameters = new BoxSearchParameters();
        searchParameters.setAncestorFolderIds(Lists.newArrayList("0"));
        searchParameters.setMetadataFilter(metadataFilter);

        return new BoxSearch(createApiConnection(accessToken)).searchRange(0, 500, searchParameters);
    }

    public BoxTask getTask(String accessToken, String id) {
        return new BoxTask(createApiConnection(accessToken), id);
    }

    public void copyFile(String accessToken, String fileId, String folderId, String newName) {
        BoxAPIConnection apiConnection = createApiConnection(accessToken);

        new BoxFile(apiConnection, fileId).copy(new BoxFolder(apiConnection, folderId), newName);
    }

    public void moveFile(String accessToken, String fileId, String folderId, String newName) {
        BoxAPIConnection apiConnection = createApiConnection(accessToken);

        new BoxFile(apiConnection, fileId).move(new BoxFolder(apiConnection, folderId), newName);
    }

    public BoxFolder.Info createFolder(String accessToken, String parentFolderId, String name) {
        return new BoxFolder(createApiConnection(accessToken), parentFolderId).createFolder(name);
    }

    public BoxWebHook.Info createWebhook(String accessToken, String targetId, String targetType, String callbackUri, Set<Trigger> triggersToSend) throws MalformedURLException {
        BoxResource target;

        switch (targetType) {
            case "FOLDER":
                target = new BoxFolder(createApiConnection(accessToken), targetId);
                break;
            case "FILE":
                target = new BoxFile(createApiConnection(accessToken), targetId);
                break;
            default:
                throw new RuntimeException("The target "+ targetType + " does not support triggers");
        }

        URL address = new URL(callbackUri);
        return BoxWebHook.create(target, address, triggersToSend);
    }

    public List<BoxMetadataTemplate.Info> getEnterpriseTemplates(String accessToken) {
        return BoxMetadataTemplate.getEnterpriseTemplates(createApiConnection(accessToken));
    }

    public BoxWebHook.Info getWebhook(String accessToken, String webhookId) throws MalformedURLException {
        return new BoxWebHook(createApiConnection(accessToken), webhookId).getInfo();
    }

    public void deleteWebhook(String accessToken, String id) {
        BoxWebHook boxWebHook = new BoxWebHook(createApiConnection(accessToken), id);
        boxWebHook.delete();
    }

    public void updateWebhook(String accessToken, String webhookId, BoxWebHook.Info info) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxWebHook boxWebHook = new BoxWebHook(createApiConnection(accessToken), webhookId);
        boxWebHook.updateInfo(info);
        updateCredentials(boxAPIConnection, accessToken);
    }

    public BoxSharedLink createSharedLink(String accessToken, String id) {
        BoxSharedLink.Permissions permissions = new BoxSharedLink.Permissions();
        permissions.setCanDownload(true);

        return new BoxFile(createApiConnection(accessToken), id).createSharedLink(BoxSharedLink.Access.OPEN, null, permissions);
    }

    private BoxAPIConnection createApiConnection(String clientId, String clientSecret, String authorizationCode) {
        BoxAPIConnection boxAPIConnection = new BoxAPIConnection(clientId, clientSecret, authorizationCode);
        boxAPIConnection.setRequestInterceptor(requestInterceptor);

        return boxAPIConnection;
    }

    private BoxAPIConnection createApiConnection(String accessToken) {
        Credentials credentials = getLastCredentials(accessToken);

        BoxAPIConnection boxAPIConnection;
        boxAPIConnection = new BoxAPIConnection(securityConfiguration.getOauth2ContentApiClientId(),
                    securityConfiguration.getOauth2ContentApiClientSecret(), credentials.getAccessToken(), credentials.getRefreshToken());

//        if(boxAPIConnection.needsRefresh()) {
//            boxAPIConnection = new BoxAPIConnection(securityConfiguration.getOauth2ContentApiClientId(),
//                    securityConfiguration.getOauth2ContentApiClientSecret(), credentials.getAccessToken(), credentials.getRefreshToken());
//        }

         if(boxAPIConnection.getRefreshToken() != null && !Objects.equals(credentials.getRefreshToken(), boxAPIConnection.getRefreshToken())) {
            try {
                cacheManager.saveCredentials(credentials.getBoxUserId(), credentials);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        boxAPIConnection.setRequestInterceptor(requestInterceptor);

        return boxAPIConnection;
    }

    private void updateCredentials(BoxAPIConnection boxAPIConnection, String accessToken) {
        Credentials credentials;

        try {
            credentials = getLastCredentials(accessToken);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        if (!Objects.equals(boxAPIConnection.getRefreshToken(), credentials.getRefreshToken())) {
            credentials.setRefreshToken(boxAPIConnection.getRefreshToken());
        }

        if (!Objects.equals(boxAPIConnection.getAccessToken(), credentials.getAccessToken())) {
            credentials.setAccessToken(boxAPIConnection.getAccessToken());
        }

        try {
            if(credentials.getBoxUserId() != null) {
                cacheManager.saveCredentials(credentials.getBoxUserId(), credentials);
                cacheManager.saveUserIdByTokenKey(credentials.getAccessToken(), credentials.getBoxUserId());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Credentials getLastCredentials(String accessTokenKey) {
        try {
            Credentials credentials = cacheManager.getCredentialsByTokenKey(accessTokenKey);
            if (credentials == null) {

                return new Credentials(accessTokenKey, null, null);
            } else {

                return credentials;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

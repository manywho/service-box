package com.manywho.services.box.facades;

import com.box.sdk.*;
import com.box.sdk.BoxWebHook.Trigger;
import com.google.common.collect.Lists;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.entities.MetadataSearch;
import com.manywho.services.box.services.TokenCacheService;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoxFacade {
    private final SecurityConfiguration securityConfiguration;
    private com.box.sdk.RequestInterceptor requestInterceptor;
    private TokenCacheService tokenCacheService;


    @Inject
    public BoxFacade(SecurityConfiguration securityConfiguration, RequestInterceptor requestInterceptor,
                     TokenCacheService tokenCacheService) {

        this.securityConfiguration = securityConfiguration;
        this.requestInterceptor = requestInterceptor;
        this.tokenCacheService = tokenCacheService;
    }

    public BoxAPIConnection authenticateUser(String clientId, String clientSecret, String authorizationCode) {
        return createApiConnection(clientId, clientSecret, authorizationCode);
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
        return BoxUser.getCurrentUser(createApiConnection(accessToken)).getInfo();
    }

    public BoxFolder getFolder(String accessToken, String id) {
        return new BoxFolder(createApiConnection(accessToken), id);
    }

    public BoxFile getFile(String accessToken, String id) {
        return new BoxFile(createApiConnection(accessToken), id);
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

    private BoxAPIConnection createApiConnection(String accessToken) {
        BoxAPIConnection boxAPIConnection = new BoxAPIConnection(accessToken);
        boxAPIConnection.setRequestInterceptor(requestInterceptor);

        return boxAPIConnection;
    }

    private BoxAPIConnection createApiConnection(String clientId, String clientSecret, String authorizationCode) {
        BoxAPIConnection boxAPIConnection = new BoxAPIConnection(clientId, clientSecret, authorizationCode);
        boxAPIConnection.setRequestInterceptor(requestInterceptor);

        return boxAPIConnection;
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
        BoxWebHook boxWebHook = new BoxWebHook(createApiConnection(accessToken), webhookId);
        boxWebHook.updateInfo(info);
    }

    public BoxSharedLink createSharedLink(String accessToken, String id) {
        BoxSharedLink.Permissions permissions = new BoxSharedLink.Permissions();
        permissions.setCanDownload(true);

        return new BoxFile(createApiConnection(accessToken), id).createSharedLink(BoxSharedLink.Access.OPEN, null, permissions);
    }
}

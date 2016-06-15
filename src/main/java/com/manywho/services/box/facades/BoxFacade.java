package com.manywho.services.box.facades;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxGroup;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxMetadataFilter;
import com.box.sdk.BoxSearch;
import com.box.sdk.BoxSearchParameters;
import com.box.sdk.BoxSharedLink;
import com.box.sdk.BoxTask;
import com.box.sdk.BoxUser;
import com.box.sdk.EncryptionAlgorithm;
import com.box.sdk.IAccessTokenCache;
import com.box.sdk.InMemoryLRUAccessTokenCache;
import com.box.sdk.JWTEncryptionPreferences;
import com.google.common.collect.Lists;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.entities.MetadataSearch;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class BoxFacade {
    private final SecurityConfiguration securityConfiguration;

    @Inject
    public BoxFacade(SecurityConfiguration securityConfiguration) {
        this.securityConfiguration = securityConfiguration;
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

        IAccessTokenCache accessTokenCache = new InMemoryLRUAccessTokenCache(100);

        return BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(
                enterpriseId,
                securityConfiguration.getOauth2DeveloperEditionClientId(),
                securityConfiguration.getOauth2DeveloperEditionClientSecret(),
                encryptionPreferences,
                accessTokenCache
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
        return new BoxAPIConnection(accessToken);
    }

    private BoxAPIConnection createApiConnection(String clientId, String clientSecret, String authorizationCode) {
        return new BoxAPIConnection(clientId, clientSecret, authorizationCode);
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

    public BoxSharedLink createSharedLink(String accessToken, String id) {
        BoxSharedLink.Permissions permissions = new BoxSharedLink.Permissions();
        permissions.setCanDownload(true);

        return new BoxFile(createApiConnection(accessToken), id).createSharedLink(BoxSharedLink.Access.OPEN, null, permissions);
    }
}

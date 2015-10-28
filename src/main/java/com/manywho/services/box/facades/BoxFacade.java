package com.manywho.services.box.facades;

import com.box.sdk.AdvancedSearchParams;
import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxGroup;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxTask;
import com.box.sdk.BoxUser;
import com.box.sdk.EncryptionAlgorithm;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.oauth2.BoxProvider;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BoxFacade {
    @Inject
    private SecurityConfiguration securityConfiguration;

    public BoxAPIConnection authenticateUser(String clientId, String clientSecret, String authorizationCode) {
        return createApiConnection(clientId, clientSecret, authorizationCode);
    }

    public BoxDeveloperEditionAPIConnection createDeveloperApiConnection(String enterpriseId) throws IOException {
        String privateKey = new String(Files.readAllBytes(Paths.get(securityConfiguration.getPrivateKeyLocation())));

        return BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(
                enterpriseId,
                BoxProvider.DEVELOPER_EDITION_CLIENT_ID,
                BoxProvider.DEVELOPER_EDITION_CLIENT_SECRET,
                privateKey,
                securityConfiguration.getPrivateKeyPassword(),
                EncryptionAlgorithm.RSA_SHA_256
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

    public Iterable<BoxItem.Info> searchByMetadata(String accessToken, String metadataType) {
        AdvancedSearchParams.MetadataFilter metadataFilter = new AdvancedSearchParams.MetadataFilter();
        metadataFilter.setScope("enterprise");
        metadataFilter.setTemplateKey(metadataType);

        return BoxFolder.getRootFolder(createApiConnection(accessToken))
                .search(null, new AdvancedSearchParams().addMetadataFilter(metadataFilter));
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
}

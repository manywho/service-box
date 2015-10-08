package com.manywho.services.box.facades;

import com.box.sdk.AdvancedSearchParams;
import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxTask;
import com.box.sdk.BoxUser;

public class BoxFacade {
    public BoxAPIConnection authenticateUser(String clientId, String clientSecret, String authorizationCode) {
        return createApiConnection(clientId, clientSecret, authorizationCode);
    }

    public BoxUser.Info getCurrentUser(String accessToken) {
        return BoxUser.getCurrentUser(createApiConnection(accessToken)).getInfo();
    }

    public BoxFolder getFolder(String accessToken, String id) {
        return new BoxFolder(createApiConnection(accessToken), id);
    }

    private BoxAPIConnection createApiConnection(String accessToken) {
        return new BoxAPIConnection(accessToken);
    }

    private BoxAPIConnection createApiConnection(String clientId, String clientSecret, String authorizationCode) {
        return new BoxAPIConnection(clientId, clientSecret, authorizationCode);
    }

    public BoxFile getFile(String accessToken, String id) {
        return new BoxFile(createApiConnection(accessToken), id);
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
}

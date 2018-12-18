package com.manywho.services.box.client;

import com.box.sdk.*;
import com.box.sdk.BoxWebHook.Trigger;
import com.google.common.collect.Lists;
import com.manywho.sdk.entities.run.elements.type.ListFilter;
import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.entities.MetadataSearch;
import com.manywho.services.box.facades.BoxFacadeInterface;
import com.manywho.services.box.managers.CacheManagerInterface;
import com.manywho.services.box.services.box.WebhookSingatureValidator;

import javax.inject.Inject;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BoxClient {
    private com.box.sdk.RequestInterceptor requestInterceptor;
    private CacheManagerInterface cacheManager;
    private WebhookSingatureValidator webhookSingatureValidator;
    private BoxFacadeInterface boxFacade;

    @Inject
    public BoxClient(RequestInterceptor requestInterceptor, CacheManagerInterface cacheManager,
                     WebhookSingatureValidator webhookSingatureValidator, BoxFacadeInterface boxFacade) {

        this.requestInterceptor = requestInterceptor;
        this.cacheManager = cacheManager;
        this.webhookSingatureValidator = webhookSingatureValidator;
        this.boxFacade = boxFacade;
    }

    public BoxAPIConnection authenticateUser(String clientId, String clientSecret, String authorizationCode) {
        return createApiConnectionByAuthCode(authorizationCode);
    }

    public Boolean validateWebhookSignature(String signatureVersion, String algorithm, String signaturePrimary,
                                            String signatureSecondary, String payload, String deliveryTimestamp ) {

        return webhookSingatureValidator.validateWebhookSignature(signatureVersion, algorithm, signaturePrimary,
                 signatureSecondary, payload, deliveryTimestamp);
    }

    public BoxAPIConnection confirmUserAuthentication(String accessToken) {
        return createApiConnection(accessToken);
    }

    public BoxDeveloperEditionAPIConnection createDeveloperApiConnection(String enterpriseId) throws IOException {
        BoxDeveloperEditionAPIConnection appEnterpriseConnection =  boxFacade.createDeveloperApiConnection(enterpriseId);
        appEnterpriseConnection.setRequestInterceptor(requestInterceptor);

        return appEnterpriseConnection;
    }

    public BoxUser.Info getCurrentUser(String accessToken) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxUser.Info boxUser =  BoxUser.getCurrentUser(boxAPIConnection).getInfo();
        boxAPIConnection.getRefreshToken();

        return boxUser;
    }

    public BoxFolder getFolder(String accessToken, String id) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxFolder boxFolder = new BoxFolder(boxAPIConnection, id);
        updateCredentials(boxAPIConnection, accessToken);

        return boxFolder;
    }

    public PartialCollection<BoxItem.Info>  getFolders(String accessToken, BoxSearchParameters searchParameters, ListFilter listFilter) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        PartialCollection<BoxItem.Info> boxFolders = new BoxSearch(createApiConnection(accessToken))
                .searchRange(listFilter.getOffset(), listFilter.getLimit() + 1, searchParameters);

        updateCredentials(boxAPIConnection, accessToken);

        return boxFolders;
    }

    /**
     * This method is called using the web integration credentials, these credentials are not valid for webhooks
     * we don't save these refresh token
     *
     * @param accessToken
     * @param id
     * @return
     */
    public BoxFile getFileWithWebIntegretionCredentials(String accessToken, String id) {
        return new BoxFile(new BoxAPIConnection(accessToken), id);
    }

    public BoxFile getFile(String accessToken, String id) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxFile boxFile = new BoxFile(boxAPIConnection, id);
        updateCredentials(boxAPIConnection, accessToken);

        return boxFile;
    }

    public BoxTaskAssignment getTaskAssignment(String accessToken, String id) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxTaskAssignment taskAssignment = new BoxTaskAssignment(boxAPIConnection, id);
        updateCredentials(boxAPIConnection, accessToken);

        return taskAssignment;
    }

    public BoxAPIConnection getValidBoxApiConnection(Credentials credentials) {
        BoxAPIConnection boxAPIConnection = createApiConnection(credentials);
        BoxUser.getCurrentUser(boxAPIConnection);
        updateCredentials(boxAPIConnection, credentials.getAccessToken());

        return boxAPIConnection;
    }

    public Iterable<BoxGroup.Info> loadGroups(String accessToken) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        Iterable<BoxGroup.Info> groups = BoxGroup.getAllGroups(boxAPIConnection);
        updateCredentials(boxAPIConnection, accessToken);

        return groups;
    }

    public Iterable<BoxUser.Info> loadUsers(String accessToken) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        Iterable<BoxUser.Info> users = BoxUser.getAllEnterpriseUsers(boxAPIConnection);
        updateCredentials(boxAPIConnection, accessToken);

        return users;
    }

    public Iterable<BoxItem.Info> searchByMetadata(String accessToken, String metadataType, MetadataSearch metadataSearch, ListFilter filter) {
        BoxMetadataFilter metadataFilter = new BoxMetadataFilter();
        metadataFilter.setScope("enterprise");
        metadataFilter.setTemplateKey(metadataType);

        // Add filters for each of the fields in the metadataSearch object
        for (Map.Entry<String, String> field : metadataSearch.getFields().entrySet()) {
            metadataFilter.addFilter(field.getKey(), field.getValue());
        }

        BoxSearchParameters searchParameters = new BoxSearchParameters();
        searchParameters.setMetadataFilter(metadataFilter);

        return new BoxSearch(createApiConnection(accessToken)).searchRange(filter.getOffset(), (filter.getLimit()), searchParameters);
    }

    public BoxTask getTask(String accessToken, String id) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxTask boxTask = new BoxTask(boxAPIConnection, id);
        updateCredentials(boxAPIConnection, accessToken);

        return boxTask;
    }

    public void copyFile(String accessToken, String fileId, String folderId, String newName) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);

        new BoxFile(boxAPIConnection, fileId).copy(new BoxFolder(boxAPIConnection, folderId), newName);
        updateCredentials(boxAPIConnection, accessToken);
    }

    public void moveFile(String accessToken, String fileId, String folderId, String newName) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        new BoxFile(boxAPIConnection, fileId).move(new BoxFolder(boxAPIConnection, folderId), newName);
        updateCredentials(boxAPIConnection, accessToken);
    }

    public BoxFolder.Info createFolder(String accessToken, String parentFolderId, String name) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxFolder.Info boxFolder = new BoxFolder(boxAPIConnection, parentFolderId).createFolder(name);
        updateCredentials(boxAPIConnection, accessToken);

        return boxFolder;
    }

    public BoxWebHook.Info createWebhook(String accessToken, String targetId, String targetType, String callbackUri, Set<Trigger> triggersToSend) throws MalformedURLException {
        BoxResource target;
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        switch (targetType) {
            case "FOLDER":
                target = new BoxFolder(boxAPIConnection, targetId);
                break;
            case "FILE":
                target = new BoxFile(boxAPIConnection, targetId);
                break;
            default:
                throw new RuntimeException("The target "+ targetType + " does not support triggers");
        }

        URL address = new URL(callbackUri);
        BoxWebHook.Info webhook = BoxWebHook.create(target, address, triggersToSend);
        updateCredentials(boxAPIConnection, accessToken);

        return  webhook;
    }

    public Iterable<MetadataTemplate> getEnterpriseTemplates(String accessToken) {
        return MetadataTemplate.getEnterpriseMetadataTemplates(createApiConnection(accessToken));
    }

    public BoxWebHook.Info getWebhook(String accessToken, String webhookId) throws MalformedURLException {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxWebHook.Info webhook = new BoxWebHook(boxAPIConnection, webhookId).getInfo();
        updateCredentials(boxAPIConnection, accessToken);

        return webhook;
    }

    public void deleteWebhook(String accessToken, String id) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxWebHook boxWebHook = new BoxWebHook(boxAPIConnection, id);
        boxWebHook.delete();
        updateCredentials(boxAPIConnection, accessToken);
    }

    public void updateWebhookTriggers(String accessToken, String webhookId, Set<BoxWebHook.Trigger> triggers) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxWebHook boxWebHook = new BoxWebHook(boxAPIConnection, webhookId);
        BoxWebHook.Info info = boxWebHook.new Info().setTriggers(triggers);
        boxWebHook.updateInfo(info);
        updateCredentials(boxAPIConnection, accessToken);
    }

    public BoxSharedLink createSharedLink(String accessToken, String id) {
        BoxSharedLink.Permissions permissions = new BoxSharedLink.Permissions();
        permissions.setCanDownload(true);

        return new BoxFile(createApiConnection(accessToken), id).createSharedLink(BoxSharedLink.Access.OPEN, null, permissions);
    }

    private BoxAPIConnection createApiConnectionByAuthCode(String authorizationCode) {
        BoxAPIConnection boxAPIConnection = boxFacade.createApiConnection(authorizationCode);
        boxAPIConnection.setRequestInterceptor(requestInterceptor);

        return boxAPIConnection;
    }

    private BoxAPIConnection createApiConnection(String accessToken) {
        Credentials credentials = getLastCredentials(accessToken);

        BoxAPIConnection boxAPIConnection;
        boxAPIConnection = boxFacade.createApiConnection(credentials.getAccessToken(), credentials.getRefreshToken());

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


    private BoxAPIConnection createApiConnection(Credentials credentials) {
        BoxAPIConnection boxAPIConnection;
        boxAPIConnection = boxFacade.createApiConnection(credentials.getAccessToken(), credentials.getRefreshToken());

        if (boxAPIConnection.getRefreshToken() != null && !Objects.equals(credentials.getRefreshToken(), boxAPIConnection.getRefreshToken())) {
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
            if (credentials.getBoxUserId() != null) {
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

    public BoxComment getComment(String accessToken, String id) {
        BoxAPIConnection boxAPIConnection = createApiConnection(accessToken);
        BoxComment boxComment = new BoxComment(boxAPIConnection, id);
        updateCredentials(boxAPIConnection, accessToken);

        return boxComment;
    }
}

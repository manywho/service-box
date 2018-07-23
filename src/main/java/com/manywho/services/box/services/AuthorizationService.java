package com.manywho.services.box.services;

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxGroup;
import com.box.sdk.BoxGroupMembership;
import com.box.sdk.BoxUser;
import com.manywho.sdk.entities.run.elements.config.Authorization;
import com.manywho.sdk.entities.run.elements.config.Group;
import com.manywho.sdk.entities.run.elements.config.User;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.utils.StreamUtils;
import com.manywho.services.box.client.BoxClient;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthorizationService {
    private BoxClient boxClient;
    private ObjectMapperService objectMapperService;

    @Inject
    public AuthorizationService(BoxClient boxClient, ObjectMapperService objectMapperService) {
        this.boxClient = boxClient;
        this.objectMapperService = objectMapperService;
    }

    public String getUserAuthorizationStatus(Authorization authorization, AuthenticatedWho user) {
        switch (authorization.getGlobalAuthenticationType()) {
            case Public:
                return "200";
            case AllUsers:
                if (!user.getUserId().equalsIgnoreCase("PUBLIC_USER")) {
                    return "200";
                } else {
                    return "401";
                }
            case Specified:
                if (!user.getUserId().equalsIgnoreCase("PUBLIC_USER")) {
                    BoxUser.Info boxUser = boxClient.getCurrentUser(user.getToken());

                    if (CollectionUtils.isNotEmpty(authorization.getUsers())) {
                        for (User allowedUser:authorization.getUsers()) {
                            if (allowedUser.getAttribute().equalsIgnoreCase("accountId")
                                    && Objects.equals(allowedUser.getAuthenticationId(), boxUser.getID())) {

                                return "200";
                            }
                        }
                    }

                    if (CollectionUtils.isNotEmpty(authorization.getGroups())) {
                        Stream<BoxGroupMembership.Info> memberships  = boxUser.getResource().getMemberships().stream();

                        // Look in the logged-in users memberships to see if they're a member of the desired group
                        for (Group group : authorization.getGroups()) {
                            if (memberships.anyMatch(m -> m.getGroup().getID().equals(group.getAuthenticationId()))) {
                                return "200";
                            }
                        }
                    }
                }
            default:
                return "401";
        }
    }

    public ObjectCollection loadGroups(String enterpriseId) throws IOException {
        BoxDeveloperEditionAPIConnection apiConnection = boxClient.createDeveloperApiConnection(enterpriseId);

        Iterable<BoxGroup.Info> groups = boxClient.loadGroups(apiConnection.getAccessToken());

        return StreamUtils.asStream(groups.iterator())
                .map(objectMapperService::convertGroupObjectToManyWhoGroup)
                .collect(Collectors.toCollection(ObjectCollection::new));
    }

    public Object loadGroupAttributes() {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Label", "Users"));
        properties.add(new Property("Value", "users"));

        Object object = new Object();
        object.setDeveloperName("AuthenticationAttribute");
        object.setExternalId("users");
        object.setProperties(properties);

        return object;
    }

    public ObjectCollection loadUsers(String enterpriseId) throws IOException {
        BoxDeveloperEditionAPIConnection apiConnection = boxClient.createDeveloperApiConnection(enterpriseId);

        Iterable<BoxUser.Info> users = boxClient.loadUsers(apiConnection.getAccessToken());

        return StreamUtils.asStream(users.iterator())
                .map(objectMapperService::convertUserObjectToManyWhoUser)
                .collect(Collectors.toCollection(ObjectCollection::new));
    }

    public Object loadUsersAttributes() {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Label", "Account ID"));
        properties.add(new Property("Value", "accountId"));

        Object object = new Object();
        object.setDeveloperName("AuthenticationAttribute");
        object.setExternalId("accountID");
        object.setProperties(properties);

        return object;
    }
}

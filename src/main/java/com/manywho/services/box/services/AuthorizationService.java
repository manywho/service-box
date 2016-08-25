package com.manywho.services.box.services;

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxGroup;
import com.box.sdk.BoxGroupMembership;
import com.box.sdk.BoxUser;
import com.manywho.sdk.entities.run.elements.config.Authorization;
import com.manywho.sdk.entities.run.elements.config.Group;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.utils.StreamUtils;
import com.manywho.services.box.facades.BoxFacade;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthorizationService {
    private BoxFacade boxFacade;
    private ObjectMapperService objectMapperService;

    @Inject
    public AuthorizationService(BoxFacade boxFacade, ObjectMapperService objectMapperService) {
        this.boxFacade = boxFacade;
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
                    BoxUser.Info boxUser = boxFacade.getCurrentUser(user.getToken());

                    // Check if group access is being used
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
        BoxDeveloperEditionAPIConnection apiConnection = boxFacade.createDeveloperApiConnection(enterpriseId);

        Iterable<BoxGroup.Info> groups = boxFacade.loadGroups(apiConnection.getAccessToken());

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
        BoxDeveloperEditionAPIConnection apiConnection = boxFacade.createDeveloperApiConnection(enterpriseId);

        Iterable<BoxUser.Info> users = boxFacade.loadUsers(apiConnection.getAccessToken());

        return StreamUtils.asStream(users.iterator())
                .map(objectMapperService::convertUserObjectToManyWhoUser)
                .collect(Collectors.toCollection(ObjectCollection::new));
    }
}

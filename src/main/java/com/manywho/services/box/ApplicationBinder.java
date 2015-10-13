package com.manywho.services.box;

import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.facades.BoxFacade;
import com.manywho.services.box.managers.AuthManager;
import com.manywho.services.box.managers.DataManager;
import com.manywho.services.box.managers.DescribeManager;
import com.manywho.services.box.managers.FileManager;
import com.manywho.services.box.managers.TaskManager;
import com.manywho.services.box.oauth2.BoxProvider;
import com.manywho.services.box.services.*;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(BoxProvider.class).to(AbstractOauth2Provider.class);

        bind(BoxFacade.class).to(BoxFacade.class).in(Singleton.class);

        bind(AuthManager.class).to(AuthManager.class);
        bind(AuthenticationService.class).to(AuthenticationService.class);
        bind(DataManager.class).to(DataManager.class);
        bind(DatabaseLoadService.class).to(DatabaseLoadService.class);
        bind(DatabaseSaveService.class).to(DatabaseSaveService.class);
        bind(DescribeManager.class).to(DescribeManager.class);
        bind(DescribeService.class).to(DescribeService.class);
        bind(FileManager.class).to(FileManager.class);
        bind(FileService.class).to(FileService.class);
        bind(FileUploadService.class).to(FileUploadService.class);
        bind(ObjectMapperService.class).to(ObjectMapperService.class);
        bind(SecurityConfiguration.class).to(SecurityConfiguration.class);
        bind(TaskManager.class).to(TaskManager.class);
        bind(TaskService.class).to(TaskService.class);
    }
}

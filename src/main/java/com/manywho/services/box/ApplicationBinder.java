package com.manywho.services.box;

import com.box.sdk.RequestInterceptor;
import com.manywho.sdk.client.raw.RawRunClient;
import com.manywho.sdk.services.config.RedisConfiguration;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.configuration.FlowConfiguration;
import com.manywho.services.box.configuration.RedisConfig;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.client.BoxClient;
import com.manywho.services.box.facades.BoxFacade;
import com.manywho.services.box.facades.BoxFacadeInterface;
import com.manywho.services.box.interceptor.RequestInterceptorImpl;
import com.manywho.services.box.managers.*;
import com.manywho.services.box.oauth2.BoxProvider;
import com.manywho.services.box.services.*;
import com.manywho.services.box.services.box.WebhookSingatureValidator;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;

public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(BoxProvider.class).to(AbstractOauth2Provider.class);
        bind(RedisConfig.class).to(RedisConfiguration.class).in(Singleton.class);
        bind(BoxClient.class).to(BoxClient.class).in(Singleton.class);
        bind(AuthManager.class).to(AuthManager.class);
        bind(AuthenticationService.class).to(AuthenticationService.class);
        bind(AuthorizationService.class).to(AuthorizationService.class);
        bind(DataManager.class).to(DataManager.class);
        bind(DatabaseLoadService.class).to(DatabaseLoadService.class);
        bind(DatabaseSaveService.class).to(DatabaseSaveService.class);
        bind(DescribeManager.class).to(DescribeManager.class);
        bind(DescribeService.class).to(DescribeService.class);
        bind(FileManager.class).to(FileManager.class);
        bind(FileService.class).to(FileService.class);
        bind(FolderManager.class).to(FolderManager.class);
        bind(FolderService.class).to(FolderService.class);
        bind(FileUploadService.class).to(FileUploadService.class);
        bind(ObjectMapperService.class).to(ObjectMapperService.class);
        bind(SecurityConfiguration.class).to(SecurityConfiguration.class);
        bind(FlowConfiguration.class).to(FlowConfiguration.class);
        bind(TaskManager.class).to(TaskManager.class);
        bind(TaskService.class).to(TaskService.class);
        bind(WebhookManager.class).to(WebhookManager.class);
        bind(WebhookTriggersService.class).to(WebhookTriggersService.class);
        bind(CacheManager.class).to(CacheManagerInterface.class);
        bind(EventManager.class).to(EventManager.class);
        bind(CallbackWebhookManager.class).to(CallbackWebhookManager.class);
        bind(ListenerManager.class).to(ListenerManager.class);
        bind(RequestInterceptorImpl.class).to(RequestInterceptor.class);
        bind(TokenCacheService.class).to(TokenCacheService.class);
        bind(CallbackService.class).to(CallbackService.class);
        bind(LaunchFlowManager.class).to(LaunchFlowManager.class);
        bind(FlowService.class).to(FlowService.class);
        bind(RawRunClient.class).to(RawRunClient.class);
        bind(AssignFlowManager.class).to(AssignFlowManager.class);
        bind(ListenerService.class).to(ListenerService.class);
        bind(WebhookHandlerManager.class).to(WebhookHandlerManager.class);
        bind(WebhookSingatureValidator.class).to(WebhookSingatureValidator.class);
        bind(BoxFacade.class).to(BoxFacadeInterface.class);
    }
}

package com.manywho.services.box.unit.managers;

import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.entities.WebhookReturn;
import com.manywho.services.box.entities.webhook.Item;
import com.manywho.services.box.entities.webhook.Source;
import com.manywho.services.box.managers.CallbackWebhookManager;
import com.manywho.services.box.managers.WebhookHandlerManager;
import com.manywho.services.box.services.AuthenticationService;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;

@RunWith(DataProviderRunner.class)
public class WebhookHandlerManagerTest {

    @DataProvider
    public static Object[][] data() {
        return new Object[][] {
                { "file" },
                { "folder" },
                { "task" },
                { "xxx" }
        };
    }

    @Test
    @UseDataProvider("data")
    public void testHandleWebhookEmptyCredentials(String target) throws Exception {
        AuthenticationService authenticationService = mock(AuthenticationService.class);
        CallbackWebhookManager callbackWebhookManager = mock(CallbackWebhookManager.class);
        WebhookHandlerManager webhookHandlerManager = new WebhookHandlerManager(authenticationService,
                callbackWebhookManager);

        WebhookReturn webhookReturn = new WebhookReturn();
        when(authenticationService.updateCredentials("123")).thenReturn(null);

        webhookHandlerManager.handleWebhook(webhookReturn, "123456", "1234", target, "123");
        verifyZeroInteractions(callbackWebhookManager);
    }

    @Test
    public void testHandleWebhookForFile() throws Exception {
        String webhookId = "123456";
        String targetId = "1234";
        String createdByUserId = "123";
        String triggerType = "triggerX";
        String targetType = "file";

        AuthenticationService authenticationService = mock(AuthenticationService.class);
        CallbackWebhookManager callbackWebhookManager = mock(CallbackWebhookManager.class);
        WebhookHandlerManager webhookHandlerManager = new WebhookHandlerManager(authenticationService,
                callbackWebhookManager);

        Credentials credentialsMock = mock(Credentials.class);

        WebhookReturn webhookReturn = mock(WebhookReturn.class);
        when(webhookReturn.getTrigger()).thenReturn(triggerType);

        when(authenticationService.updateCredentials("123")).thenReturn(credentialsMock);
        webhookHandlerManager.handleWebhook(webhookReturn, webhookId, targetId, targetType, createdByUserId);

        verify(callbackWebhookManager, times(1)).processEventFile(webhookId, targetId, triggerType);
        verify(callbackWebhookManager, times(1)).processEventForFlow(createdByUserId, targetType, targetId, triggerType);
        verify(authenticationService, times(1)).updateCredentials(any());
    }

    @Test
    public void testHandleWebhookForFolder() throws Exception {
        String webhookId = "123456";
        String targetId = "1234";
        String createdByUserId = "123";
        String triggerType = "triggerX";
        String targetType = "folder";

        AuthenticationService authenticationService = mock(AuthenticationService.class);
        CallbackWebhookManager callbackWebhookManager = mock(CallbackWebhookManager.class);
        WebhookHandlerManager webhookHandlerManager = new WebhookHandlerManager(authenticationService,
                callbackWebhookManager);

        Credentials credentialsMock = mock(Credentials.class);
        WebhookReturn webhookReturn = mock(WebhookReturn.class);
        when(webhookReturn.getTrigger()).thenReturn(triggerType);

        when(authenticationService.updateCredentials("123")).thenReturn(credentialsMock);
        webhookHandlerManager.handleWebhook(webhookReturn, webhookId, targetId, targetType, createdByUserId);

        verify(callbackWebhookManager, times(1)).processEventFolder(webhookId, targetId, triggerType);
        verify(callbackWebhookManager, times(1)).processEventForFlow(createdByUserId, targetType, targetId, triggerType);
        verify(authenticationService, times(1)).updateCredentials(any());
    }

    @Test
    public void testHandleWebhookForTaskAssignment() throws Exception {
        String webhookId = "123456";
        String targetId = "1234";
        String createdByUserId = "123";
        String triggerType = "triggerX";
        String targetType = "task_assignment";
        String itemId = "555";
        String itemType = "file";

        AuthenticationService authenticationService = mock(AuthenticationService.class);
        CallbackWebhookManager callbackWebhookManager = mock(CallbackWebhookManager.class);
        WebhookHandlerManager webhookHandlerManager = new WebhookHandlerManager(authenticationService,
                callbackWebhookManager);

        Credentials credentialsMock = mock(Credentials.class);
        WebhookReturn webhookReturn = mock(WebhookReturn.class);
        when(webhookReturn.getTrigger()).thenReturn(triggerType);
        Source source = mock(Source.class);
        Item item = mock(Item.class);
        when(webhookReturn.getSource()).thenReturn(source);
        when(source.getItem()).thenReturn(item);
        when(item.getId()).thenReturn(itemId);
        when(item.getType()).thenReturn(itemType);

        when(authenticationService.updateCredentials("123")).thenReturn(credentialsMock);
        webhookHandlerManager.handleWebhook(webhookReturn, webhookId, targetId, targetType, createdByUserId);

        verify(callbackWebhookManager, times(1)).processEventTask(webhookId, targetId, triggerType);
        verify(callbackWebhookManager, times(1)).processEventTaskForFlow(createdByUserId, targetType, targetId,
                triggerType, itemId, itemType);
        verify(authenticationService, times(1)).updateCredentials(any());
    }
}

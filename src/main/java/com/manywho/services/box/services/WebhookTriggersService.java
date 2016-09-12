package com.manywho.services.box.services;

import com.box.sdk.BoxWebHook;

import java.util.Objects;
import java.util.Set;

public class WebhookTriggersService {

    public Set<BoxWebHook.Trigger> listOfTriggerForWebhook(BoxWebHook.Info webhook, BoxWebHook.Trigger newTrigger) {
        Set<BoxWebHook.Trigger> currentTriggers = webhook.getTriggers();
        boolean found = false;

        for (BoxWebHook.Trigger t:currentTriggers) {
            if (Objects.equals(t.toString(), newTrigger.toString())) {
                found = true;
            }
        }

        if(!found) {
            currentTriggers.add(newTrigger);
        }

        return currentTriggers;
    }

    // workaround bug in library
    public Boolean haveTrigger(BoxWebHook.Info webhook, BoxWebHook.Trigger newTrigger) {

        for (BoxWebHook.Trigger t:webhook.getTriggers()) {
            if (Objects.equals(t.toString(), newTrigger.toString())) {
                return true;
            }
        }

        return false;
    }

}

package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.BoxTask;
import com.box.sdk.BoxTaskAssignment;
import com.manywho.services.box.client.BoxClient;
import org.joda.time.DateTime;

import javax.inject.Inject;
import java.util.Date;

public class TaskService {
    private BoxClient boxClient;

    @Inject
    public TaskService(BoxClient boxClient) {
        this.boxClient = boxClient;
    }

    public BoxTask.Info addTaskToFile(String token, String fileId, String message, DateTime dueAt) throws Exception {
        // Load the requested file from Box so we can add a new task to it
        BoxFile file = boxClient.getFile(token, fileId);
        if (file != null) {
            Date date = null;
            if(dueAt != null) {
                date = dueAt.toDate();
            }

            // Add a new task to the file with the given details
            return file.addTask(BoxTask.Action.REVIEW, message, date);
        }

        throw new Exception("The requested file could not be loaded from Box");
    }

    public BoxTaskAssignment.Info addAssignmentToTask(String token, String id, String assigneeEmail) {
        return boxClient.getTask(token, id).addAssignmentByLogin(assigneeEmail);
    }
}

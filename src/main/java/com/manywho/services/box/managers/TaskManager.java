package com.manywho.services.box.managers;

import com.box.sdk.BoxTask;
import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.box.entities.requests.TaskAddAssignment;
import com.manywho.services.box.entities.requests.TaskCreate;
import com.manywho.services.box.services.ObjectMapperService;
import com.manywho.services.box.services.TaskService;
import com.manywho.services.box.types.Task;

import javax.inject.Inject;

public class TaskManager {
    @Inject
    private PropertyCollectionParser propertyCollectionParser;

    @Inject
    private TaskService taskService;

    @Inject
    private ObjectMapperService objectMapperService;

    public ServiceResponse createTask(AuthenticatedWho user, ServiceRequest serviceRequest) throws Exception {
        // Parse the received ManyWho objects into POJOs
        TaskCreate taskCreate = propertyCollectionParser.parse(serviceRequest.getInputs(), TaskCreate.class);
        if (taskCreate != null) {
            // Add a new task to the requested file on Box
            BoxTask.Info taskInfo = taskService.addTaskToFile(
                    user.getToken(),
                    taskCreate.getFile().getId(),
                    taskCreate.getMessage(),
                    taskCreate.getDueAt()
            );

            // Convert the newly created BoxTask object into a ManyWho object
            Object taskObject = objectMapperService.convertBoxTask(taskInfo);

            return new ServiceResponse(
                    InvokeType.Forward,
                    new EngineValue("Task", ContentType.Object, Task.NAME, new ObjectCollection(taskObject)),
                    serviceRequest.getToken()
            );
        }

        throw new Exception("An invalid task creation request was given");
    }

    public ServiceResponse addAssignment(AuthenticatedWho user, ServiceRequest serviceRequest) throws Exception {
        // Parse the received ManyWho objects into POJOs
        TaskAddAssignment assignment = propertyCollectionParser.parse(serviceRequest.getInputs(), TaskAddAssignment.class);
        if (assignment != null) {
            taskService.addAssignmentToTask(user.getToken(), assignment.getTask().getId(), assignment.getAssigneeEmail());
        }

        return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
    }
}

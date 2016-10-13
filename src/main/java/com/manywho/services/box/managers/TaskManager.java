package com.manywho.services.box.managers;

import com.box.sdk.BoxTask;
import com.box.sdk.BoxTaskAssignment;
import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.box.entities.requests.TaskAddAssignment;
import com.manywho.services.box.property.Task;
import com.manywho.services.box.services.ObjectMapperService;
import com.manywho.services.box.services.TaskService;
import com.manywho.services.box.types.TaskAssignment;

import javax.inject.Inject;

public class TaskManager {
    @Inject
    private PropertyCollectionParser propertyCollectionParser;

    @Inject
    private TaskService taskService;

    @Inject
    private ObjectMapperService objectMapperService;

    @Inject
    private PropertyCollectionParser propertyParser;

    public Object createTask(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        Task taskCreate = propertyParser.parse(objectDataRequest.getObjectData().get(0).getProperties(), Task.class);

        if (taskCreate != null) {
            // Add a new task to the requested file on Box
            BoxTask.Info taskInfo = taskService.addTaskToFile(
                    user.getToken(),
                    taskCreate.getFile().getId(),
                    taskCreate.getMessage(),
                    taskCreate.getDueAt()
            );

            return objectMapperService.convertBoxTask(taskInfo);

        }

        throw new Exception("An invalid task creation request was given");
    }

    public ServiceResponse addAssignment(AuthenticatedWho user, ServiceRequest serviceRequest) throws Exception {
        // Parse the received ManyWho objects into POJOs
        TaskAddAssignment assignment = propertyCollectionParser.parse(serviceRequest.getInputs(), TaskAddAssignment.class);
        if (assignment != null) {
            BoxTaskAssignment.Info taskInfo = taskService.addAssignmentToTask(user.getToken(), assignment.getTask().getId(), assignment.getAssigneeEmail());
            Object taskObject = objectMapperService.convertBoxTaskAssignment(taskInfo);

            return new ServiceResponse(
                    InvokeType.Forward,
                    new EngineValue("Task Assignment", ContentType.Object, TaskAssignment.NAME, new ObjectCollection(taskObject)),
                    serviceRequest.getToken()
            );
        }


        throw new Exception("An invalid task assignment creation request was given");
    }
}

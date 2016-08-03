package com.manywho.services.box.entities;

/**
 * Created by Jose on 03/08/2016.
 */
public class TaskWebhook {
    private String id;
    private String taskId;
    private String targetId;
    private String targetType;

    public TaskWebhook(){}

    public TaskWebhook( String taskId, String targetId, String targetType) {
        this.taskId = taskId;
        this.targetId = targetId;
        this.targetType = targetType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}

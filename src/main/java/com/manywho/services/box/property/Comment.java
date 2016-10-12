package com.manywho.services.box.property;

import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

@Type(com.manywho.services.box.types.Comment.NAME)
public class Comment {
    @Id
    @Property("ID")
    private String id;

    @Property("Message")
    @NotEmpty(message = "Message cannot be null or empty")
    private String message;

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
}

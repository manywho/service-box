package com.manywho.services.box.types;

import com.manywho.sdk.entities.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.entities.draw.elements.type.TypeElementPropertyCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class Task extends AbstractType {
    public final static String NAME = "Task";

    @Override
    public String getDeveloperName() {
        return NAME;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        return new TypeElementPropertyCollection() {{
            add(new TypeElementProperty("ID", ContentType.String));
            add(new TypeElementProperty("Due At", ContentType.DateTime));
            add(new TypeElementProperty("Message", ContentType.String));
            add(new TypeElementProperty("Is Completed?", ContentType.Boolean));
            add(new TypeElementProperty("Created At", ContentType.DateTime));
        }};
    }
}

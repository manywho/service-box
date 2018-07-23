package com.manywho.services.box.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;


public class Task extends AbstractType {
    public final static String NAME = "Task";

    @Override
    public String getDeveloperName() {
        return NAME;
    }

    @Override
    public TypeElementBindingCollection getBindings() {
        TypeElementPropertyBindingCollection typeElementPropertyBindings = new TypeElementPropertyBindingCollection();
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("ID", "ID"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Due At", "Due At"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Message", "Message"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Is Completed?", "Is Completed?"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Created At", "Created At"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("File", "File"));

        TypeElementBindingCollection typeElementBindings = new TypeElementBindingCollection();
        typeElementBindings.add(new TypeElementBinding(NAME, "Details about a Task", NAME, typeElementPropertyBindings));

        return typeElementBindings;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        TypeElementPropertyCollection typeElementProperties = new TypeElementPropertyCollection();
        typeElementProperties.add(new TypeElementProperty("ID", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Due At", ContentType.DateTime));
        typeElementProperties.add(new TypeElementProperty("Message", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Is Completed?", ContentType.Boolean));
        typeElementProperties.add(new TypeElementProperty("Created At", ContentType.DateTime));
        typeElementProperties.add(new TypeElementProperty("File", ContentType.Object, File.NAME));

        return typeElementProperties;
    }
}

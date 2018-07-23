package com.manywho.services.box.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class TaskAssignment extends AbstractType {
    public final static String NAME = "Task Assignment";

    @Override
    public String getDeveloperName() {
        return NAME;
    }

    @Override
    public TypeElementBindingCollection getBindings() {
        TypeElementPropertyBindingCollection typeElementPropertyBindings = new TypeElementPropertyBindingCollection();
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("ID", "ID"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Assignee Email", "Assignee Email"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("File", "File"));
        TypeElementBindingCollection typeElementBindings = new TypeElementBindingCollection();
        typeElementBindings.add(new TypeElementBinding(NAME, "Details about a Task Assignment", NAME, typeElementPropertyBindings));

        return typeElementBindings;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        TypeElementPropertyCollection typeElementProperties = new TypeElementPropertyCollection();
        typeElementProperties.add(new TypeElementProperty("ID", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Assignee Email", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("File", ContentType.Object, File.NAME));

        return typeElementProperties;
    }
}

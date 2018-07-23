package com.manywho.services.box.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class File extends AbstractType {
    public final static String NAME = "File";

    @Override
    public String getDeveloperName() {
        return NAME;
    }

    @Override
    public TypeElementBindingCollection getBindings() {
        TypeElementPropertyBindingCollection typeElementPropertyBindings = new TypeElementPropertyBindingCollection();
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("ID", "ID"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Name", "Name"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Description", "Description"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Content", "Content"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Parent Folder", "Parent Folder"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Created At", "Created At"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Modified At", "Modified At"));

        TypeElementBindingCollection typeElementBindings = new TypeElementBindingCollection();
        typeElementBindings.add(new TypeElementBinding(NAME, "Details about a file", NAME, typeElementPropertyBindings));

        return typeElementBindings;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        TypeElementPropertyCollection typeElementProperties = new TypeElementPropertyCollection();
        typeElementProperties.add(new TypeElementProperty("ID", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Name", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Description", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Content", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Parent Folder", ContentType.Object, Folder.NAME));
        typeElementProperties.add(new TypeElementProperty("Created At", ContentType.DateTime));
        typeElementProperties.add(new TypeElementProperty("Modified At", ContentType.DateTime));

        return typeElementProperties;
    }
}

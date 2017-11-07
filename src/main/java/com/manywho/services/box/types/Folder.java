package com.manywho.services.box.types;

import com.manywho.sdk.entities.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.entities.draw.elements.type.TypeElementBindingCollection;
import com.manywho.sdk.entities.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.entities.draw.elements.type.TypeElementPropertyBinding;
import com.manywho.sdk.entities.draw.elements.type.TypeElementPropertyBindingCollection;
import com.manywho.sdk.entities.draw.elements.type.TypeElementPropertyCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class Folder extends AbstractType {
    public final static String NAME = "Folder";

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
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Parent Folder ID", "Parent Folder ID"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Created At", "Created At"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Modified At", "Modified At"));


        TypeElementBindingCollection typeElementBindings = new TypeElementBindingCollection();
        typeElementBindings.add(new TypeElementBinding(NAME, "Details about a folder", NAME, typeElementPropertyBindings));

        return typeElementBindings;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        TypeElementPropertyCollection typeElementProperties = new TypeElementPropertyCollection();
        typeElementProperties.add(new TypeElementProperty("ID", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Name", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Description", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Parent Folder ID", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Created At", ContentType.DateTime));
        typeElementProperties.add(new TypeElementProperty("Modified At", ContentType.DateTime));

        return typeElementProperties;
    }
}
package com.manywho.services.box.types;

import com.manywho.sdk.entities.draw.elements.type.*;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.types.AbstractType;

public class Webhook extends AbstractType {
    public final static String NAME = "Webhook";

    @Override
    public String getDeveloperName() {
        return NAME;
    }

    @Override
    public TypeElementBindingCollection getBindings() {
        TypeElementPropertyBindingCollection typeElementPropertyBindings = new TypeElementPropertyBindingCollection();
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("ID", "ID"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Target Type", "Target Type"));
        typeElementPropertyBindings.add(new TypeElementPropertyBinding("Target ID", "Target ID"));

        TypeElementBindingCollection typeElementBindings = new TypeElementBindingCollection();
        typeElementBindings.add(new TypeElementBinding(NAME, "Details about a Webhook", NAME, typeElementPropertyBindings));

        return typeElementBindings;
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        TypeElementPropertyCollection typeElementProperties = new TypeElementPropertyCollection();
        typeElementProperties.add(new TypeElementProperty("ID", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Target Type", ContentType.String));
        typeElementProperties.add(new TypeElementProperty("Target ID", ContentType.String));

        return typeElementProperties;
    }
}

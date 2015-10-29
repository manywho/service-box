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
        return new TypeElementBindingCollection() {{
            add(new TypeElementBinding(NAME, "Details about a file", NAME, new TypeElementPropertyBindingCollection() {{
                add(new TypeElementPropertyBinding("ID", "ID"));
                add(new TypeElementPropertyBinding("Name", "Name"));
                add(new TypeElementPropertyBinding("Description", "Description"));
                add(new TypeElementPropertyBinding("Content", "Content"));
                add(new TypeElementPropertyBinding("Parent Folder", "Parent Folder"));
                add(new TypeElementPropertyBinding("Comments", "Comments"));
                add(new TypeElementPropertyBinding("Created At", "Created At"));
                add(new TypeElementPropertyBinding("Modified At", "Modified At"));
            }}));
        }};
    }

    @Override
    public TypeElementPropertyCollection getProperties() {
        return new TypeElementPropertyCollection() {{
            add(new TypeElementProperty("ID", ContentType.String));
            add(new TypeElementProperty("Name", ContentType.String));
            add(new TypeElementProperty("Description", ContentType.String));
            add(new TypeElementProperty("Content", ContentType.String));
            add(new TypeElementProperty("Parent Folder", ContentType.Object, Folder.NAME));
            add(new TypeElementProperty("Comments", ContentType.List, Comment.NAME));
            add(new TypeElementProperty("Created At", ContentType.DateTime));
            add(new TypeElementProperty("Modified At", ContentType.DateTime));
        }};
    }
}

package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaTargetFile;
import com.iorga.ivif.tag.bean.AttributeType;
import com.iorga.ivif.tag.bean.Entity;
import com.iorga.ivif.util.JavaClassGeneratorUtil;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

import javax.xml.bind.JAXBElement;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class EntityTargetFile extends JavaTargetFile {
    private List<AttributeType> idAttributes;
    private Entity entity;
    private String idClassname;

    public EntityTargetFile(String classSimpleName, JAGeneratorContext context) {
        super(classSimpleName, "entity", true, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        // Now computes values for the rendering
        idAttributes = new ArrayList<>();
        for (JAXBElement<? extends AttributeType> attributeElement : entity.getEntityAttribute()) {
            AttributeType attribute = attributeElement.getValue();
            // group id attributes
            if (attribute.isId()) {
                idAttributes.add(attribute);
            }
        }
        // And full id class name if necessary
        if (hasMultipleIds()) {
            idClassname = getClassName() + "." + entity.getName() + "Id";
        } else {
            idClassname = null;
        }
    }

    /*
    @Override
    public void applyModifications(JAGeneratorContext context) {
        super.applyModifications(context);
        // Now computes values for the rendering
        idAttributes = Lists.newArrayList();
        for (JAXBElement<? extends AttributeType> attributeElement : entity.getEntityAttribute()) {
            AttributeType attribute = attributeElement.getValue();
            // group id attributes
            if (attribute.isId()) {
                idAttributes.add(attribute);
            }
        }
        // Computing full class name
        String entityName = entity.getName();
        fullClassName = getPackageName() + entityName;
        // And full id class name if necessary
        if (hasMultipleIds()) {
            fullIdClassname = fullClassName + "." + entityName + "Id";
        } else {
            fullIdClassname = null;
        }
    }
    */

    @Override
    public void render(JAGeneratorContext context) throws Exception {
        JavaClassGeneratorUtil util = new JavaClassGeneratorUtil();
        SimpleHash freemarkerContext = context.createSimpleHash();
        freemarkerContext.put("model", this);
        freemarkerContext.put("util", util);
        freemarkerContext.put("entity", entity);
        // First process body
        Template template = context.getTemplate("EntityClass.ftl");
        ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
        template.process(freemarkerContext, new OutputStreamWriter(bodyStream));
        // Now add the header
        template = context.getTemplate("JavaHeader.ftl");
        File file = getPath(context).toFile();
        // create file structure
        file.getParentFile().mkdirs();
        // before writing to it
        FileOutputStream outputStream = new FileOutputStream(file);
        template.process(freemarkerContext, new OutputStreamWriter(outputStream));
        // And append the body
        bodyStream.writeTo(outputStream);
    }


    /// Methods used for rendering
    public boolean hasMultipleIds() {
        return idAttributes.size() > 1;
    }

    /// Getters & setters
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public List<AttributeType> getIdAttributes() {
        return idAttributes;
    }

    public String getIdClassname() {
        return idClassname;
    }
}

package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaTargetFile;
import com.iorga.ivif.tag.bean.AttributeType;
import com.iorga.ivif.tag.bean.Entity;
import com.iorga.ivif.tag.bean.ManyToOne;
import com.iorga.ivif.util.JavaClassGeneratorUtil;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBElement;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

public class EntityTargetFile extends JavaTargetFile {
    private List<EntityAttribute> idAttributes;
    private Entity entity;
    private String idClassName;
    private String idSimpleClassName;
    private Map<String, EntityAttribute> attributes = new LinkedHashMap<>();
    // Declare attribute tag names to Class mapping
    private final static Map<String, Class<?>> attributeTypesToClass = new HashMap<>();
    static {
        attributeTypesToClass.put("string", String.class);
        attributeTypesToClass.put("long", Long.class);
        attributeTypesToClass.put("boolean", Boolean.class);
        attributeTypesToClass.put("date", Date.class);
        attributeTypesToClass.put("integer", Integer.class);
    }

    public EntityTargetFile(String classSimpleName, JAGeneratorContext context) {
        super(classSimpleName, "entity", true, context);
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        // Prepare the attributes
        idAttributes = new ArrayList<>();
        for (JAXBElement<? extends AttributeType> attributeElement : entity.getEntityAttribute()) {
            EntityAttribute entityAttribute = new EntityAttribute(attributeElement);

            AttributeType attribute = attributeElement.getValue();
            String attributeName = attribute.getName();

            if (attribute instanceof ManyToOne) {
                // Handle many to one
                // Try to solve reference
                String ref = ((ManyToOne) attribute).getRef();
                if (ref.contains(".")) {
                    // This is an absolute reference, we can use it as it
                    entityAttribute.setType(ref);
                } else {
                    // This is a "relative to this package" class name
                    String packageName = getPackageName();
                    entityAttribute.setType(StringUtils.isNotBlank(packageName) ? packageName + "." + ref : ref);
                }
            } else {
                // This is a simple attribute, let's set its type and declare as resolved
                String attributeType = attributeElement.getName().getLocalPart();
                entityAttribute.setType(attributeTypesToClass.get(attributeType).getName());
            }
            attributes.put(attributeName, entityAttribute);
            declarePartPrepared(entityAttribute, attributeName);
            // group id attributes
            if (attribute.isId()) {
                idAttributes.add(entityAttribute);
            }
        }
        // And full id class name if necessary
        if (hasMultipleIds()) {
            idClassName = getClassName() + "." + entity.getName() + "Id";
            idSimpleClassName = entity.getName() + "Id";
        } else {
            idClassName = null;
            idSimpleClassName = null;
        }
    }

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

    public Collection<EntityAttribute> getAttributes() {
        return attributes.values();
    }

    /// Getters & setters
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public List<EntityAttribute> getIdAttributes() {
        return idAttributes;
    }

    public String getIdClassName() {
        return idClassName;
    }

    public String getIdSimpleClassName() {
        return idSimpleClassName;
    }
}

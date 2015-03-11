package com.iorga.ivif.ja.tag.entities;

import com.iorga.ivif.ja.tag.JAGeneratorContext;
import com.iorga.ivif.ja.tag.JavaStaticField;
import com.iorga.ivif.ja.tag.JavaTargetFile;
import com.iorga.ivif.ja.tag.configurations.JAConfiguration;
import com.iorga.ivif.ja.tag.configurations.JAConfigurationPreparedWaiter;
import com.iorga.ivif.ja.tag.entities.EntityTargetFile.EntityTargetFileId;
import com.iorga.ivif.ja.tag.entities.EnumSelectionTargetFile.EnumSelectionTargetFileId;
import com.iorga.ivif.ja.tag.views.JavaParser;
import com.iorga.ivif.tag.TargetPartPreparedEvent;
import com.iorga.ivif.tag.bean.*;
import com.iorga.ivif.tag.bean.Enum;
import org.apache.commons.lang3.StringUtils;
import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;

import javax.xml.bind.JAXBElement;
import java.lang.Boolean;
import java.lang.String;
import java.util.*;

public class EntityTargetFile extends JavaTargetFile<EntityTargetFileId> {

    private List<EntityAttribute> idAttributes;
    private Entity entity;
    private String idClassName;
    private String idSimpleClassName;
    private Map<String, EntityAttribute> attributes = new LinkedHashMap<>();
    private List<JavaStaticField> staticFields;
    private EntityAttribute versionAttribute;
    private String implementsCode;
    private List<EntityAttribute> displayNameAttributes;

    // Declare attribute tag names to Class mapping
    public final static Map<String, Class<?>> ATTRIBUTE_TYPES_TO_CLASS = new HashMap<>();
    static {
        ATTRIBUTE_TYPES_TO_CLASS.put("string", String.class);
        ATTRIBUTE_TYPES_TO_CLASS.put("long", Long.class);
        ATTRIBUTE_TYPES_TO_CLASS.put("boolean", Boolean.class);
        ATTRIBUTE_TYPES_TO_CLASS.put("date", Date.class);
        ATTRIBUTE_TYPES_TO_CLASS.put("datetime", Date.class); // TODO handle difference between datetime & date during generation
        ATTRIBUTE_TYPES_TO_CLASS.put("integer", Integer.class);
        ATTRIBUTE_TYPES_TO_CLASS.put("character", Character.class);
    }


    public static class EntityTargetFileId extends JavaTargetFileId {
        public EntityTargetFileId(String simpleOrFullClassName, String packageNameOrNull, JAConfiguration configuration) {
            super(simpleOrFullClassName, packageNameOrNull, "entity", configuration);
        }

        public EntityTargetFileId(String simpleOrFullClassName, JAConfiguration configuration) {
            this(simpleOrFullClassName, null, configuration);
        }
    }

    public EntityTargetFile(EntityTargetFileId id, JAGeneratorContext context, Entity entity) {
        super(id, context);
        this.entity = entity;
    }

    @Override
    public void prepare(JAGeneratorContext context) throws Exception {
        super.prepare(context);

        // Prepare the attributes
        idAttributes = new ArrayList<>();
        staticFields = new ArrayList<>();
        displayNameAttributes = new ArrayList<>();
        for (JAXBElement<? extends AttributeType> attributeElement : entity.getEntityAttribute()) {
            final EntityAttribute entityAttribute = new EntityAttribute(attributeElement, this);

            final AttributeType attribute = attributeElement.getValue();
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
            } else if (attribute instanceof com.iorga.ivif.tag.bean.Enum) {
                // this is an enum, we will resolve its type after the enum will be resolved

                context.waitForEvent(new JAConfigurationPreparedWaiter(this) {
                    @Override
                    protected void onConfigurationPrepared(final JAConfiguration configuration) throws Exception {

                        entityAttribute.setType(new EnumSelectionTargetFileId(((Enum) attribute).getRef(), configuration).getClassName());
                    }
                });
            } else {
                // This is a simple attribute, let's set its type and declare as resolved
                String attributeType = attributeElement.getName().getLocalPart();
                entityAttribute.setType(ATTRIBUTE_TYPES_TO_CLASS.get(attributeType).getName());
            }
            addEntityAttribute(entityAttribute, context);
            // group id attributes
            if (attribute.isId()) {
                idAttributes.add(entityAttribute);
            }
            if (attribute instanceof VersionableAttributeType && ((VersionableAttributeType) attribute).isVersion()) {
                if (versionAttribute != null) {
                    throw new IllegalStateException("Cannot have multiple version attribute");
                } else {
                    versionAttribute = entityAttribute;
                }
            }
            if (attribute.isDisplayName()) {
                displayNameAttributes.add(entityAttribute);
            }
        }
        // And full id class name if necessary
        if (hasMultipleIds()) {
            idClassName = getClassName() + "." + entity.getName() + "Id";
            idSimpleClassName = entity.getName() + "Id";
        } else {
            final EntityAttribute idAttribute = idAttributes.get(0);
            idClassName = idAttribute.getType();
            idSimpleClassName = StringUtils.substringAfterLast(idClassName, ".");
        }

        // Handle implements
        final String entityImplements = entity.getImplements();
        if (StringUtils.isNotBlank(entityImplements)) {
            implementsCode = JavaParser.parseImplements(entityImplements);
        } else {
            implementsCode = null;
        }
    }

    protected Class<? extends AttributeType> getAttributeTypeClassFromElementType(String elementType) {
        try {
            return (Class<? extends AttributeType>) Class.forName(AttributeType.class.getPackage() + "." + StringUtils.capitalize(elementType));
        } catch (ClassNotFoundException e) {
            return AttributeType.class;
        }
    }

    protected void addEntityAttribute(EntityAttribute entityAttribute, JAGeneratorContext context) throws Exception {
        String attributeName = entityAttribute.getElement().getValue().getName();
        attributes.put(attributeName, entityAttribute);
        context.throwEvent(new TargetPartPreparedEvent<>(entityAttribute));
    }

    @Override
    protected String getFreemarkerBodyTemplateName() {
        return "entities/Entity_body.java.ftl";
    }

    /// Methods used for rendering
    public boolean hasMultipleIds() {
        return idAttributes.size() > 1;
    }

    public Collection<EntityAttribute> getAttributes() {
        return attributes.values();
    }

    public EntityAttribute getIdAttribute() {
        return idAttributes.get(0);
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

    public List<JavaStaticField> getStaticFields() {
        return staticFields;
    }

    public EntityAttribute getVersionAttribute() {
        return versionAttribute;
    }

    public String getImplementsCode() {
        return implementsCode;
    }

    public List<EntityAttribute> getDisplayNameAttributes() {
        return displayNameAttributes;
    }
}

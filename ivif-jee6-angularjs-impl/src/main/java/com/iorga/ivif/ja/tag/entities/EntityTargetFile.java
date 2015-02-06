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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.lang.Boolean;
import java.util.*;

import com.iorga.ivif.ja.tag.JavaTargetFile.JavaTargetFileId;

public class EntityTargetFile extends JavaTargetFile<EntityTargetFileId> {

    private List<EntityAttribute> idAttributes;
    private Entity entity;
    private String idClassName;
    private String idSimpleClassName;
    private Map<String, EntityAttribute> attributes = new LinkedHashMap<>();
    private List<JavaStaticField> staticFields;
    private EntityAttribute versionAttribute;
    private String implementsCode;

    // Declare attribute tag names to Class mapping
    private final static Map<String, Class<?>> attributeTypesToClass = new HashMap<>();
    static {
        attributeTypesToClass.put("string", String.class);
        attributeTypesToClass.put("long", Long.class);
        attributeTypesToClass.put("boolean", Boolean.class);
        attributeTypesToClass.put("date", Date.class);
        attributeTypesToClass.put("datetime", Date.class); // TODO handle difference between datetime & date during generation
        attributeTypesToClass.put("integer", Integer.class);
        attributeTypesToClass.put("character", Character.class);
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
                entityAttribute.setType(attributeTypesToClass.get(attributeType).getName());
                if (attribute instanceof com.iorga.ivif.tag.bean.Boolean) {
                    com.iorga.ivif.tag.bean.Boolean booleanAttribute = (com.iorga.ivif.tag.bean.Boolean) attribute;
                    String fromType = booleanAttribute.getFromType();
                    if (StringUtils.isNotBlank(fromType)) {
                        // this is a <boolean> attribute define with a "fromType", we must add a convert logic
                        Class<?> fromTypeClass = attributeTypesToClass.get(fromType);
                        String fromTypeClassName = fromTypeClass.getName();
                        entityAttribute.setFromType(fromTypeClassName);
                        // create an attribute named "<attributeName>_value" which will be mapped to the real value
                        Class<? extends AttributeType> valueAttributeClass = getAttributeTypeClassFromElementType(fromType);
                        AttributeType valueAttribute = valueAttributeClass.newInstance();
                        // copy fields
                        String attributeColumn = attribute.getColumn();
                        valueAttribute.setColumn(attributeColumn);
                        attribute.setColumn(null);
                        valueAttribute.setRequired(attribute.isRequired());
                        attribute.setRequired(false);
                        valueAttribute.setName(attributeName +"_value");
                        EntityAttribute valueEntityAttribute = new EntityAttribute(new JAXBElement(new QName(null, fromType), valueAttributeClass, valueAttribute), this);
                        valueEntityAttribute.setType(fromTypeClassName);
                        addEntityAttribute(valueEntityAttribute, context);

                        // add a static field
                        JavaStaticField trueValueStaticField = JavaStaticField.createFromVariableName(attributeName + "TrueValue", fromTypeClassName, booleanAttribute.getTrueValue());
                        staticFields.add(trueValueStaticField);
                        JavaStaticField falseValueStaticField = JavaStaticField.createFromVariableName(attributeName + "FalseValue", fromTypeClassName, booleanAttribute.getFalseValue());
                        staticFields.add(falseValueStaticField);
                        entityAttribute.setTrueValueStaticField(trueValueStaticField);
                        entityAttribute.setFalseValueStaticField(falseValueStaticField);
                        // define the formula
                        boolean stringOrChar = String.class.isAssignableFrom(fromTypeClass) || Character.class.isAssignableFrom(fromTypeClass);
                        String quote = (stringOrChar ? "'" : "");
                        if (attributeColumn == null) {
                            attributeColumn = attributeName;
                        }
                        attribute.setFormula("CASE " +
                                "WHEN " + attributeColumn + " = " + quote + booleanAttribute.getTrueValue() + quote + " THEN 1 " +
                                "WHEN " + attributeColumn + " = " + quote + booleanAttribute.getFalseValue() + quote + " THEN 0 " +
                                "ELSE NULL END");
                    }
                }
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
}

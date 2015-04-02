<#include "../utils/utils.ftl"/>
<#assign entity=model.entity>
@${util.useClass("javax.persistence.Entity", false)}
<#if model.hasMultipleIds()>
@${util.useClass("javax.persistence.IdClass", false)}(${util.useClass(model.idClassName, false)}.class)
</#if>
<#if entity.table?has_content>
@${util.useClass("javax.persistence.Table", false)}(name = "${entity.table}")
</#if>
public class ${entity.name} implements ${util.useClass("java.io.Serializable", false)}, ${util.useClass("com.iorga.ivif.ja.IEntity", false)}<${util.useClass(model.idClassName, false)}><#if model.implementsCode?has_content>, <@model.implementsCode?interpret/></#if> {

<#if model.staticFields?size &gt; 0>
    <#list model.staticFields as staticField>
    public static ${util.useClass(staticField.type)} ${staticField.name} = <#if staticField.type == "java.lang.Character">new ${util.useClass("java.lang.Character")}('${staticField.value}')<#elseif staticField.type == "java.lang.String">"${staticField.value}"<#else>new ${util.useClass(staticField.type)}("${staticField.value}")</#if>;
    </#list>

</#if>
<#if model.hasMultipleIds()>
    private ${util.useClass(model.idClassName)} _entityId = new ${util.useClass(model.idClassName)}();

</#if>
<#list model.attributes as attribute>
    <#assign element=attribute.element.value>
    <#if attribute.manyToOne>
    @${util.useClass("javax.persistence.ManyToOne")}(fetch = ${util.useClass("javax.persistence.FetchType")}.LAZY, cascade = {${util.useClass("javax.persistence.CascadeType")}.PERSIST, ${util.useClass("javax.persistence.CascadeType")}.MERGE})
    </#if>
    <#if element.id>
    @${util.useClass("javax.persistence.Id")}
    </#if>
    <#assign hasColumn=element.column?has_content>
    <#if hasColumn>
        <#assign columnName=element.column>
    <#else>
        <#assign columnName=element.name>
    </#if>
    <#assign hasJoinColumns=element.joinColumn?size &gt; 0>
    <#if !hasJoinColumns && (hasColumn || !element.insertable || !element.updatable)>
        <#if attribute.manyToOne>
    @${util.useClass("javax.persistence.JoinColumn")}(name = "<@escapeJavaString str=columnName/>"<#if !element.insertable>, insertable = false</#if><#if !element.updatable>, updatable = false</#if>)
        <#else>
    @${util.useClass("javax.persistence.Column")}(name = "<@escapeJavaString str=columnName/>"<#if !element.insertable>, insertable = false</#if><#if !element.updatable>, updatable = false</#if>)
        </#if>
    </#if>
    <#if hasJoinColumns>
    @${util.useClass("javax.persistence.JoinColumns")}({
        <#list element.joinColumn as joinColumn>
        @${util.useClass("javax.persistence.JoinColumn")}(name = "<@escapeJavaString str=joinColumn.column/>", referencedColumnName="<@escapeJavaString str=joinColumn.refColumn/>"<#if !joinColumn.insertable>, insertable = false</#if><#if !joinColumn.updatable>, updatable = false</#if>)<#if joinColumn_has_next>,</#if>
        </#list>
    })
    </#if>
    <#if element.required || element.id>
    @${util.useClass("javax.validation.constraints.NotNull")}
    </#if>
    <#if element.formula?has_content>
    @${util.useClass("org.hibernate.annotations.Formula")}("(${element.formula})")
    </#if>
    <#if element.version!false>
    @${util.useClass("javax.persistence.Version")}
    </#if>
    <#if element.sequence?has_content>
        <#assign sequenceName>
            <@escapeJavaString str=element.sequence.name/><#t>
        </#assign>
    @${util.useClass("javax.persistence.SequenceGenerator")}(name = "${sequenceName}", sequenceName = "${sequenceName}"<#if element.sequence.allocationSize?has_content>, allocationSize = ${element.sequence.allocationSize}</#if>)
    @${util.useClass("javax.persistence.GeneratedValue")}(strategy = ${util.useClass("javax.persistence.GenerationType")}.SEQUENCE, generator="${sequenceName}")
    </#if>
    <#if attribute.enum>
    @${util.useClass("org.hibernate.annotations.Type")}(type = "${attribute.type}$UserType")
    </#if>
    <#if element.transient>
    @${util.useClass("javax.persistence.Transient")}
    </#if>
    private ${util.useClass(attribute.type)} ${element.name};

</#list>
<#if model.hasMultipleIds()>
    <#-- Generating the IdClass -->
    public static class ${model.idSimpleClassName} implements ${util.useClass("java.io.Serializable")} {
    <#list model.idAttributes as attribute>
        <#assign element=attribute.element.value>
        private ${util.useClass(attribute.type)} ${element.name};
    </#list>

        public ${model.idSimpleClassName}() {}

        public ${model.idSimpleClassName}(<#list model.idAttributes as attribute>${util.useClass(attribute.type)} ${attribute.element.value.name}<#if attribute_has_next>, </#if></#list>) {
    <#list model.idAttributes as attribute>
        <#assign element=attribute.element.value>
            this.${element.name} = ${element.name};
    </#list>
        }

    <#list model.idAttributes as attribute>
        <#assign element=attribute.element.value>
        public ${util.useClass(attribute.type)} ${attribute.getterName}() {
            return ${element.name};
        }
        public void ${attribute.setterName}(${util.useClass(attribute.type)} ${element.name}) {
            this.${element.name} = ${element.name};
        }
    </#list>
    }
</#if>

    @${util.useClass("java.lang.Override")}
    public ${util.useClass(model.idClassName)} entityId() {
<#if model.hasMultipleIds()>
        return _entityId;
<#else>
        return ${model.idAttribute.element.value.name};
</#if>
    }

    @${util.useClass("java.lang.Override")}
    public void entityId(${util.useClass(model.idClassName)} id) {
<#if model.hasMultipleIds()>
        if (id == null) {
    <#list model.idAttributes as idAttribute>
            ${idAttribute.setterName}(null);
    </#list>
        } else {
    <#list model.idAttributes as idAttribute>
            ${idAttribute.setterName}(id.${idAttribute.getterName}());
    </#list>
        }
<#else>
        ${model.idAttribute.setterName}(id);
</#if>
    }

    @${util.useClass("java.lang.Override")}
    public String displayName() {
        ${util.useClass("java.lang.StringBuilder")} displayNameBuilder = new ${util.useClass("java.lang.StringBuilder")}(<#rt>
<#if model.displayNameAttributes?size &gt; 0>
);
        displayNameBuilder
    <#list model.displayNameAttributes as attribute>
            .append(${attribute.element.value.name})<#if attribute_has_next>.append(", ")<#else>;</#if>
    </#list>
<#else>
    <#-- No display name attributes, let's display the class name + its ids -->
"${entity.name}#<#if model.hasMultipleIds()>[</#if>");
        displayNameBuilder
    <#list model.idAttributes as attribute>
            .append(${attribute.element.value.name})<#if attribute_has_next>.append(", ")<#else><#if model.hasMultipleIds()>.append("]")</#if>;</#if>
    </#list>
</#if>
        return displayNameBuilder.toString();
    }

    /// Getters & Setters
<#list model.attributes as attribute>
    <#assign element=attribute.element.value>
    public ${util.useClass(attribute.type)} ${attribute.getterName}() {
        return ${element.name};
    }

    public void ${attribute.setterName}(${util.useClass(attribute.type)} ${element.name}) {
        this.${element.name} = ${element.name};
    <#if model.hasMultipleIds() && element.id>
        _entityId.${element.name} = ${element.name};
    </#if>
    <#if element.fromType?has_content>
        <#-- this is a boolean attribute with a "from-type" defined, that is to say, we must set the original _value attribute -->
        // set the original value
        if (${util.useClass("java.lang.Boolean")}.TRUE.equals(${element.name})) {
            ${attribute.setterName}_value(${attribute.trueValueStaticField.name});
        } else if (${util.useClass("java.lang.Boolean")}.FALSE.equals(${element.name})) {
            ${attribute.setterName}_value(${attribute.falseValueStaticField.name});
        } else {
            ${attribute.setterName}_value(null);
        }
    </#if>
    }

</#list>
}
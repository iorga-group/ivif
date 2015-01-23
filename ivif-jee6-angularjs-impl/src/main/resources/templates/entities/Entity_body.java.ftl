<#assign entity=model.entity>
@${util.useClass("javax.persistence.Entity")}
<#if model.hasMultipleIds()>
@${util.useClass("javax.persistence.IdClass")}(${util.useClass(model.idClassName)}.class)
</#if>
<#if entity.table?has_content>
@${util.useClass("javax.persistence.Table")}(name = "${entity.table}")
</#if>
public class ${entity.name} {

<#list model.staticFields as staticField>
    public static ${util.useClass(staticField.type)} ${staticField.name} = <#if staticField.type == "java.lang.Character">new ${util.useClass("java.lang.Character")}('${staticField.value}')<#elseif staticField.type == "java.lang.String">"${staticField.value}"<#else>new ${util.useClass(staticField.type)}("${staticField.value}")</#if>;
</#list>

<#list model.attributes as attribute>
    <#assign element=attribute.element.value>
    <#if attribute.manyToOne>
    @${util.useClass("javax.persistence.ManyToOne")}(fetch = ${util.useClass("javax.persistence.FetchType")}.LAZY, cascade = {${util.useClass("javax.persistence.CascadeType")}.PERSIST, ${util.useClass("javax.persistence.CascadeType")}.MERGE})
    </#if>
    <#if element.id>
    @${util.useClass("javax.persistence.Id")}
    </#if>
    <#if element.column?has_content>
        <#if attribute.manyToOne>
    @${util.useClass("javax.persistence.JoinColumn")}(name = "${element.column}"<#if !element.insertable>, insertable = false</#if><#if !element.updatable>, updatable = false</#if>)
        <#else>
    @${util.useClass("javax.persistence.Column")}(name = "${element.column}"<#if !element.insertable>, insertable = false</#if><#if !element.updatable>, updatable = false</#if>)
        </#if>
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

    /// Getters & Setters
<#list model.attributes as attribute>
    <#assign element=attribute.element.value>
    public ${util.useClass(attribute.type)} ${attribute.getterName}() {
        return ${element.name};
    }

    public void ${attribute.setterName}(${util.useClass(attribute.type)} ${element.name}) {
        this.${element.name} = ${element.name};
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
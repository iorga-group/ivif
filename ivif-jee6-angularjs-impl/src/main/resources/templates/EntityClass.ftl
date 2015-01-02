@${util.useClass("javax.persistence.Entity")}
<#if model.hasMultipleIds()>
@${util.useClass("javax.persistence.IdClass")}(${util.useClass(model.idClassName)}.class)
</#if>
<#if entity.table?has_content>
@${util.useClass("javax.persistence.Table")}(name = "${entity.table}")
</#if>
public class ${entity.name} {
<#list model.attributes as attribute>
    <#assign element=attribute.element.value>
    <#if element.id>
    @${util.useClass("javax.persistence.Id")}
    </#if>
    <#if element.column?has_content>
    @${util.useClass("javax.persistence.Column")}(name = "${element.column}")
    </#if>
    <#if element.required || element.id>
    @${util.useClass("javax.validation.constraints.NotNull")}
    </#if>
    <#if attribute.manyToOne>
    @${util.useClass("javax.persistence.ManyToOne")}(fetch = ${util.useClass("javax.persistence.FetchType")}.LAZY, cascade = {${util.useClass("javax.persistence.CascadeType")}.PERSIST, ${util.useClass("javax.persistence.CascadeType")}.MERGE})
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
        public ${util.useClass(attribute.type)} get${attribute.capitalizedName}() {
            return ${element.name};
        }
        public void set${attribute.capitalizedName}(${util.useClass(attribute.type)} ${element.name}) {
            this.${element.name} = ${element.name};
        }
    </#list>
    }
</#if>

    /// Getters & Setters
<#list model.attributes as attribute>
    <#assign element=attribute.element.value>
    public ${util.useClass(attribute.type)} get${attribute.capitalizedName}() {
        return ${element.name};
    }

    public void set${attribute.capitalizedName}(${util.useClass(attribute.type)} ${element.name}) {
        this.${element.name} = ${element.name};
    }

</#list>
}
@${util.useClass("javax.persistence.Entity")}
<#if model.hasMultipleIds()>
@${util.useClass("javax.persistence.IdClass")}(${util.useClass(model.fullIdClassname)}.class)
</#if>
<#if entity.table?has_content>
@${util.useClass("javax.persistence.Table")}(name = "${entity.table}")
</#if>
public class ${entity.name} {
<#list entity.entityAttribute as attributeElement>

    <#assign attribute=attributeElement.value>
    <#if attribute.id>
    @${util.useClass("javax.persistence.Id")}
    </#if>
    <#if attribute.column?has_content>
    @${util.useClass("javax.persistence.Column")}(name = "${attribute.column}")
    </#if>
    <#if attribute.required>
    @${util.useClass("javax.validation.constraints.NotNull")}
    </#if>
    //TODO private // retrieve type and continue
</#list>

}
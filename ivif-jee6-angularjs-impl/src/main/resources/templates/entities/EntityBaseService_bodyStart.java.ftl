<#macro useEntityClass>
${util.useClass(model.entityClassName)}<#rt>
</#macro>
<#assign entity=model.entityTargetFile>
@${util.useClass("com.iorga.ivif.ja.Generated", false)}
@${util.useClass("javax.ejb.Stateless", false)}
public class ${model.simpleClassName} extends ${util.useClass("com.iorga.ivif.ja.EntityBaseService", false)}<${util.useClass(model.entityClassName, false)}, ${util.useClass(entity.idClassName, false)}> {

    @${util.useClass("javax.persistence.PersistenceContext")}
    protected ${util.useClass("javax.persistence.EntityManager")} entityManager;

<#list util.injections.iterator() as injection>
    @${util.useClass("javax.inject.Inject")}
    protected ${util.useClass(injection.className)} ${injection.variableName};

</#list>

    @${util.useClass("java.lang.Override")}
    protected ${util.useClass(entity.idClassName)} getId(${util.useClass(model.entityClassName)} entity) {
<#if entity.hasMultipleIds()>
        if (<#list entity.idAttributes as idAttribute>entity.${idAttribute.getterName}() == null<#if idAttribute_has_next> && </#if></#list>) {
            return null;
        } else {
            return new ${util.useClass(entity.idClassName)}(<#rt>
    <#list entity.idAttributes as idAttribute>entity.${idAttribute.getterName}()<#if idAttribute_has_next>, </#if></#list>);<#lt>
        }
<#else>
        return entity.${entity.idAttribute.getterName}();
</#if>
    }

    @${util.useClass("java.lang.Override")}
    protected void setId(${util.useClass(model.entityClassName)} entity, ${util.useClass(entity.idClassName)} id) {
<#if entity.hasMultipleIds()>
        if (id == null) {
    <#list entity.idAttributes as idAttribute>
            entity.${idAttribute.setterName}(null);
    </#list>
        } else {
    <#list entity.idAttributes as idAttribute>
            entity.${idAttribute.setterName}(id.${idAttribute.getterName});
    </#list>
        }
<#else>
        entity.${entity.idAttribute.setterName}(id);
</#if>
    }

<#if entity.hasMultipleIds()>
    public <@useEntityClass/> find(<#list entity.idAttributes as attribute>${util.useClass(attribute.type)} ${attribute.element.value.name}<#if attribute_has_next>, </#if></#list>) {
        return find(new ${util.useClass(entity.idClassName)}(<#list entity.idAttributes as attribute>${attribute.element.value.name}<#if attribute_has_next>, </#if></#list>));
    }

    @${util.useClass("java.lang.Override")}
    public boolean isNew(<@useEntityClass/> entity) {
        return <#list entity.idAttributes as attribute>entity.${attribute.getterName}() == null<#if attribute_has_next> && </#if></#list>;
    }

</#if>

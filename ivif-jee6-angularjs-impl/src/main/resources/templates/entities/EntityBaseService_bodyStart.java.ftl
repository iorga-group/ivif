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

<#if entity.hasMultipleIds()>
    public <@useEntityClass/> find(<#list entity.idAttributes as attribute>${util.useClass(attribute.type)} ${attribute.element.value.name}<#if attribute_has_next>, </#if></#list>) {
        return find(new ${util.useClass(entity.idClassName)}(<#list entity.idAttributes as attribute>${attribute.element.value.name}<#if attribute_has_next>, </#if></#list>));
    }

    @${util.useClass("java.lang.Override")}
    public boolean isNew(<@useEntityClass/> entity) {
        return <#list entity.idAttributes as attribute>entity.${attribute.getterName}() == null<#if attribute_has_next> && </#if></#list>;
    }

</#if>
    /**
     * Apply pagination sort : sort on Id columns in order to paginate later<#-- see http://java.dzone.com/articles/jpql-pagination-oracle#comment-87017 -->
     **/
    protected void applyPaginationSort(${util.useClass("com.iorga.ivif.ja.EntityBaseService.SearchState")}<${util.useClass(model.qEntityClassName)}, ?> searchParam) {
        searchParam.jpaQuery.orderBy(<#list entity.idAttributes as attribute>searchParam.$record.${attribute.element.value.name}.asc()<#if attribute_has_next>, </#if></#list>);
    }

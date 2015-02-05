<#macro useEntityClass>
${util.useClass(model.entityClassName)}<#rt>
</#macro>
<#assign entity=model.entityTargetFile>
@${util.useClass("com.iorga.ivif.ja.Generated", false)}
@${util.useClass("javax.ejb.Stateless", false)}
public class ${model.simpleClassName} {
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

    public <@useEntityClass/> find(${util.useClass(entity.idClassName)} id) {
        return entityManager.find(<@useEntityClass/>.class, id);
    }
<#else>
    <#assign attribute=entity.idAttributes[0]>
    <#assign varName=attribute.element.value.name>
    public <@useEntityClass/> find(${util.useClass(attribute.type)} ${varName}) {
        return entityManager.find(<@useEntityClass/>.class, ${varName});
    }
</#if>

    public boolean isNew(<@useEntityClass/> entity) {
        return <#list entity.idAttributes as attribute>entity.${attribute.getterName}() == null<#if attribute_has_next> || </#if></#list>;
    }

    @${util.useClass("javax.ejb.TransactionAttribute")}
    public <@useEntityClass/> save(<@useEntityClass/> entityToSave) {
        return save(entityToSave, true);
    }

    @${util.useClass("javax.ejb.TransactionAttribute")}
    protected <@useEntityClass/> save(<@useEntityClass/> entityToSave, boolean flush) {
        if (isNew(entityToSave)) {
            entityToSave = create(entityToSave);
        } else {
            entityToSave = update(entityToSave);
        }
        if (flush) {
            entityManager.flush();
        }
        return entityToSave;
    }

    @${util.useClass("javax.ejb.TransactionAttribute")}
    protected <@useEntityClass/> create(<@useEntityClass/> entity) {
        entityManager.persist(entity);
        return entity;
    }

    @${util.useClass("javax.ejb.TransactionAttribute")}
    protected <@useEntityClass/> update(<@useEntityClass/> entity) {
        return entityManager.merge(entity);
    }

    @${util.useClass("javax.ejb.TransactionAttribute")}
    public ${util.useClass("java.util.List")}<<@useEntityClass/>> save(${util.useClass("java.util.List")}<<@useEntityClass/>> entitiesToSave) {
        for (<@useEntityClass/> entityToSave : entitiesToSave) {
            save(entityToSave, false);
        }
        entityManager.flush();
        return entitiesToSave;
    }

    public void detach(<@useEntityClass/> entity) {
        entityManager.detach(entity);
    }


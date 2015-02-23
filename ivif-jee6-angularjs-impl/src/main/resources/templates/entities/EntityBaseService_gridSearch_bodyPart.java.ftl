<#include "../utils/rolesAllowedMacro.java.ftl"/>
<#assign grid=model.grid>
<@rolesAllowed rolesAllowed=grid.element.rolesAllowed util=util nbTabs=1/>
    public ${util.useClass("com.mysema.query.SearchResults")}<${util.useClass(model.searchResultClassName)}> search(${util.useClass(model.searchParamClassName)} searchParam) {
        ${util.useClass("com.mysema.query.jpa.impl.JPAQuery")} jpaQuery = new ${util.useClass("com.mysema.query.jpa.impl.JPAQuery")}(entityManager, ${util.useClass("com.mysema.query.jpa.JPQLTemplates")}.DEFAULT);
        ${util.useClass(baseModel.qEntityClassName)} $record = new ${util.useClass(baseModel.qEntityClassName)}("${baseModel.entityVariableName}");
        jpaQuery.from($record);
<#if grid.queryModel.queryDslCode?exists>
        // Applying static query
        jpaQuery.where(<@grid.queryModel.queryDslCode?interpret/>);
</#if>
        // Applying filter
        ${util.useClass(model.searchFilterClassName)} filter = searchParam.filter;
<#list grid.nonTransientDisplayedColumns as column>
    <#-- can filter only on non transient fields -->
        if (filter.${column.refVariableName} != null) {
            jpaQuery.where($record.${column.ref}.<#rt>
    <#switch column.entityAttribute.element.name.localPart>
        <#case "string">
            containsIgnoreCase(filter.${column.refVariableName})<#t>
            <#break>
        <#case "integer">
            like("%" + filter.${column.refVariableName} + "%")<#t>
            <#break>
        <#default>
            eq(filter.${column.refVariableName})<#t>
    </#switch>
            );<#lt>
        }
</#list>
        // Applying action filters
<#list model.openViewActions as openViewAction>
        if (filter.${openViewAction.variableName} != null) {
    <#if openViewAction.rolesAllowed?size &gt; 0>
            // Check additionnal rights for that action
            ${util.useInject("com.iorga.ivif.ja.SecurityService")}.check(<#list openViewAction.rolesAllowed as roleAllowed>"${roleAllowed}"<#if roleAllowed_has_next>, </#if></#list>);
    </#if>
            ${util.useClass(openViewAction.className)} parameters = filter.${openViewAction.variableName};
            jpaQuery.where(<@openViewAction.queryModel.queryDslCode?interpret/>);
        }
</#list>
        // Applying sorting
        ${util.useClass("com.iorga.ivif.ja.Sorting")} sorting = searchParam.sorting;
        ${util.useClass("com.mysema.query.types.expr.ComparableExpressionBase")} sortingExpression = null;
<#list grid.nonTransientDisplayedColumns as column>
    <#-- can sort only on non transient fields -->
        <#if column_index != 0>} else </#if>if ("${column.refVariableName}".equals(sorting.ref)) {
            sortingExpression = $record.${column.ref};
    <#if !column_has_next>
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(${util.useClass("com.iorga.ivif.ja.SortingType")}.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }
    </#if>
</#list>
        // Applying limit & offset
        jpaQuery.limit(searchParam.limit);
        jpaQuery.offset(searchParam.offset);
        // Returning projection
        return jpaQuery.listResults(${util.useClass("com.mysema.query.types.ConstructorExpression")}.create(${util.useClass(model.searchResultClassName)}.class, <#list grid.nonTransientSelectedColumns as column>$record.${column.ref}<#if column_has_next>, </#if></#list>));
    }


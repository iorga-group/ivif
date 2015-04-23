<#include "../utils/rolesAllowedMacro.java.ftl"/>
<#assign grid=model.grid>
<#assign queryModel=grid.queryModel>
<#assign searchStateClassName=grid.element.name+"SearchState">

    protected class ${searchStateClassName} extends ${util.useClass("com.iorga.ivif.ja.EntityBaseService.SearchState")}<${util.useClass(baseModel.qEntityClassName)}, ${util.useClass(model.searchParamClassName)}> {
        protected ${searchStateClassName}(${util.useClass(model.searchParamClassName)} searchParam) {
            super(new ${util.useClass(baseModel.qEntityClassName)}("${baseModel.entityVariableName}"), searchParam);
        }
    }

<@rolesAllowed rolesAllowed=grid.element.rolesAllowed util=util nbTabs=1/>
    public ${util.useClass("com.mysema.query.SearchResults")}<${util.useClass(model.searchResultClassName)}> search(${util.useClass(model.searchParamClassName)} searchParam) {
        ${searchStateClassName} searchState = new ${searchStateClassName}(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return listSearchResults(searchState);
    }

    protected void applyQueryAndFiltersAndSorting(${searchStateClassName} searchState) {
        ${util.useClass(baseModel.qEntityClassName)} $record = searchState.$record;
        ${util.useClass(model.searchParamClassName)} searchParam = searchState.searchParam;
        ${util.useClass("com.mysema.query.jpa.impl.JPAQuery")} jpaQuery = searchState.jpaQuery;
<#if queryModel.queryDslCode?has_content>
        // Applying static query
        jpaQuery.where(<@queryModel.queryDslCode?interpret/>);
</#if>
        // Applying filter
        ${util.useClass(model.searchFilterClassName)} filter = searchParam.filter;
<#list grid.filterGridColumns as column>
    <#assign columnType=column.entityAttribute.element.name.localPart/>
    <#-- can filter only on non transient fields -->
        if (<#if columnType == "string">${util.useClass("org.apache.commons.lang3.StringUtils")}.isNotEmpty(filter.${column.refVariableName})<#else>filter.${column.refVariableName} != null</#if>) {
            jpaQuery.where($record.${column.ref}.<#rt>
    <#switch columnType>
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
        if (sorting != null) {
<#list grid.sortableGridColumns as column>
    <#-- can sort only on non transient fields -->
            <#if column_index != 0>} else </#if>if ("${column.refVariableName}".equals(sorting.ref)) {
                sortingExpression = $record.${column.ref};
    <#if !column_has_next>
            }
        }
        if (sortingExpression != null) {
            jpaQuery.orderBy(${util.useClass("com.iorga.ivif.ja.SortingType")}.ASCENDING.equals(sorting.type) ? sortingExpression.asc() : sortingExpression.desc());
        }<#if (queryModel.defaultOrderBy)!?has_content> else {
            // default sorting
            jpaQuery.orderBy(<#list queryModel.defaultOrderBy as orderBy>$record.${orderBy.ref}.<#if orderBy.direction.toString() == 'ASCENDING'>asc<#else>desc</#if>()<#if orderBy_has_next>, </#if></#list>);<#-- TODO take into account default-order-by specified in openViewActions -->
        }</#if>
    </#if>
</#list>
    }

    protected ${util.useClass("com.mysema.query.SearchResults")}<${util.useClass(model.searchResultClassName)}> listSearchResults(${searchStateClassName} searchState) {
        return searchState.jpaQuery.listResults(listExpression(searchState));
    }

    protected ${util.useClass("com.mysema.query.types.ConstructorExpression")}<${util.useClass(model.searchResultClassName)}> listExpression(${searchStateClassName} searchState) {
        ${util.useClass(baseModel.qEntityClassName)} $record = searchState.$record;
        return ${util.useClass("com.mysema.query.types.ConstructorExpression")}.create(${util.useClass(model.searchResultClassName)}.class, <#list grid.resultGridColumns as column>$record.${column.ref}<#if column_has_next>, </#if></#list>);
    }

<@rolesAllowed rolesAllowed=grid.element.rolesAllowed util=util nbTabs=1/>
    public ${util.useClass("java.util.List")}<${util.useClass(model.searchResultClassName)}> find(${util.useClass(model.searchParamClassName)} searchParam) {
        ${searchStateClassName} searchState = new ${searchStateClassName}(searchParam);
        searchState.jpaQuery.from(searchState.$record);
        applyQueryAndFiltersAndSorting(searchState);
        applyLimitAndOffset(searchState);
        return list(searchState);
    }

    protected ${util.useClass("java.util.List")}<${util.useClass(model.searchResultClassName)}> list(${searchStateClassName} searchState) {
        return searchState.jpaQuery.list(listExpression(searchState));
    }


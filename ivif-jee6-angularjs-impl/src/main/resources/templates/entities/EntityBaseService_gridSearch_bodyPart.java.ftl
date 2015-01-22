<#assign grid=model.grid>
    public ${util.useClass("com.mysema.query.SearchResults")}<${util.useClass(model.searchResultClassName)}> search(${util.useClass(model.searchParamClassName)} searchParam) {
        ${util.useClass("com.mysema.query.jpa.impl.JPAQuery")} jpaQuery = new ${util.useClass("com.mysema.query.jpa.impl.JPAQuery")}(entityManager, ${util.useClass("com.mysema.query.jpa.JPQLTemplates")}.DEFAULT);
        ${util.useClass(baseModel.qEntityClassName)} record = new ${util.useClass(baseModel.qEntityClassName)}("${baseModel.entityVariableName}");
        jpaQuery.from(record);
        // Applying filter
        ${util.useClass(model.searchFilterClassName)} filter = searchParam.filter;
<#list grid.displayedColumns as column>
        if (filter.${column.refVariableName} != null) {
            jpaQuery.where(record.${column.ref}.${model.getSearchRelationMethodForGridColumn(column)}(filter.${column.refVariableName}));
        }
</#list>
        // Applying action filters
<#list model.openViewActions as openViewAction>
        if (filter.${openViewAction.variableName} != null) {
            ${util.useClass(openViewAction.className)} parameters = filter.${openViewAction.variableName};
            jpaQuery.where(${util.fromTemplateString(openViewAction.action.queryDslCode)});
        }
</#list>
        // Applying sorting
        ${util.useClass("com.iorga.ivif.ja.Sorting")} sorting = searchParam.sorting;
        ${util.useClass("com.mysema.query.types.expr.ComparableExpressionBase")} sortingExpression = null;
<#list grid.displayedColumns as column>
        <#if column_index != 0>} else </#if>if ("${column.refVariableName}".equals(sorting.ref)) {
            sortingExpression = record.${column.ref};
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
        return jpaQuery.listResults(${util.useClass("com.mysema.query.types.ConstructorExpression")}.create(${util.useClass(model.searchResultClassName)}.class, <#list grid.selectedColumns as column>record.${column.ref}<#if column_has_next>, </#if></#list>));
    }


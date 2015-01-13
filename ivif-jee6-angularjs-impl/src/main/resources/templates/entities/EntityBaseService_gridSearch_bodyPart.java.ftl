    public ${util.useClass("com.mysema.query.SearchResults")}<${util.useClass(model.searchResultClassName)}> search(${util.useClass(model.searchParamClassName)} searchParam) {
        ${util.useClass("com.mysema.query.jpa.impl.JPAQuery")} jpaQuery = new ${util.useClass("com.mysema.query.jpa.impl.JPAQuery")}(entityManager, ${util.useClass("com.mysema.query.jpa.JPQLTemplates")}.DEFAULT);
        ${util.useClass(baseModel.qEntityClassName)} qEntity = new ${util.useClass(baseModel.qEntityClassName)}("${baseModel.entityTargetFile.variableName}");
        jpaQuery.from(qEntity);
        // Applying filter
        ${util.useClass(model.searchFilterClassName)} filter = searchParam.filter;
<#list model.columns as column>
        if (filter.${column.refVariableName} != null) {
            jpaQuery.where(qEntity.${column.element.ref}.${model.getSearchRelationMethodForGridColumn(column)}(filter.${column.refVariableName}));
        }
</#list>
        // Applying sorting
        ${util.useClass("com.iorga.ivif.ja.Sorting")} sorting = searchParam.sorting;
        ${util.useClass("com.mysema.query.types.expr.ComparableExpressionBase")} sortingExpression = null;
<#list model.columns as column>
        <#if column_index != 0>} else </#if>if ("${column.refVariableName}".equals(sorting.ref)) {
            sortingExpression = qEntity.${column.element.ref};
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
        return jpaQuery.listResults(${util.useClass("com.mysema.query.types.ConstructorExpression")}.create(${util.useClass(model.searchResultClassName)}.class, <#list model.columns as column>qEntity.${column.element.ref}<#if column_has_next>, </#if></#list>));
    }
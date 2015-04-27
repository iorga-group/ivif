<#include "../utils/rolesAllowedMacro.java.ftl"/>
<#assign grid=model.grid>
<#assign gridName=grid.element.name>
<#assign editable=grid.editable>
<#assign serviceVariableName=model.baseService.variableName>
<#assign entityClassName=model.baseService.entityClassName>
@${util.useClass("javax.ws.rs.Path", false)}("/${grid.variableName}")
@${util.useClass("com.iorga.ivif.ja.Generated", false)}
@${util.useClass("javax.ejb.Stateless", false)}
<@rolesAllowed rolesAllowed=grid.element.rolesAllowed util=util inCurrentClass=false/>
public class ${gridName}BaseWS {

    @${util.useClass("javax.inject.Inject")} @${util.useClass("com.iorga.ivif.ja.Generated")}
    private ${util.useClass(model.baseService.className)} ${serviceVariableName};
<#if grid.serviceSaveClassName?has_content>

    @${util.useClass("javax.inject.Inject")}
    private ${util.useClass(grid.serviceSaveClassName)} saveService;
</#if>
<#if grid.serviceSearchClassName?has_content>

    @${util.useClass("javax.inject.Inject")}
    private ${util.useClass(grid.serviceSearchClassName)} searchService;
</#if>


<#assign hasEditableResultFilter=grid.editableResultFilterIntersectionGridColumns?size &gt; 0>
<#if editable>
    <#-- Save class -->
    <#if hasEditableResultFilter>
    public static class ${gridName}EditableFilterResult {
        <#list grid.editableResultFilterIntersectionGridColumns as column>
        public ${util.useClass(column.entityAttribute.type)} ${column.refVariableName};
        </#list>
    }
    </#if>
    @${util.useClass("org.codehaus.jackson.annotate.JsonIgnoreProperties")}(ignoreUnknown = true)
    public static class ${model.saveParamSimpleClassName} <#if hasEditableResultFilter>extends ${gridName}EditableFilterResult </#if>{
    <#list grid.editableOnlyAndResultIntersectionGridColumns as column>
        public ${util.useClass(column.entityAttribute.type)} ${column.refVariableName};
    </#list>
    }
    @${util.useClass("javax.ws.rs.POST")}
    @${util.useClass("javax.ws.rs.Path")}("/save")
    @${util.useClass("javax.ws.rs.Consumes")}("application/json")
    @${util.useClass("javax.ejb.TransactionAttribute")}
    public void save(${util.useClass("java.util.List")}<${model.saveParamSimpleClassName}> saveParams) {
        ${util.useClass("java.util.List")}<${util.useClass(entityClassName)}> entitiesToSave = new ${util.useClass("java.util.ArrayList")}<>(saveParams.size());
        for (${model.saveParamSimpleClassName} saveParam : saveParams) {
            // Search for this entityToSave
            ${util.useClass(entityClassName)} entityToSave = ${serviceVariableName}.find(<#list grid.idColumns as column>saveParam.${column.ref}<#if column_has_next>, </#if></#list>);
            if (entityToSave == null) {
                // Must create a new entityToSave
                entityToSave = new ${util.useClass(entityClassName)}();
            }
            // Apply modifications
    <#list grid.editableGridColumnsWithIdsAndVersion as column>
        <#-- TODO should check if one can edit this column, checking editable-if expression -->
            entityToSave.<#list column.refEntityAttributes as entityAttribute><#if entityAttribute_has_next>${entityAttribute.getterName}().<#else>${entityAttribute.setterName}(</#if></#list>saveParam.${column.refVariableName});
    </#list>
    <#if grid.versionColumn?exists>
        <#-- TODO should set versions for each modified sub entities also -->
            // Set version for optimistic lock
            ${serviceVariableName}.detach(entityToSave);
            entityToSave.${grid.versionColumn.entityAttribute.setterName}(saveParam.${grid.versionColumn.refVariableName});
    </#if>

            entitiesToSave.add(entityToSave);
        }

        // Ask for save
    <#if grid.serviceSaveMethod?has_content>
        saveService.${grid.serviceSaveMethod}(entitiesToSave);
    <#else>
        ${serviceVariableName}.save(entitiesToSave);
    </#if>
    }

</#if>
<#-- SearchResult class -->
<#assign hasFilterResult=grid.filterResultIntersectionGridColumns?size &gt; 0>
<#if hasFilterResult>
    public static class ${gridName}FilterResult <#if hasEditableResultFilter>extends ${gridName}EditableFilterResult </#if>{
    <#list grid.filterResultIntersectionGridColumns as column>
        public ${util.useClass(column.entityAttribute.type)} ${column.refVariableName};
    </#list>
    }
</#if>
    public static class ${model.searchResultSimpleClassName} <#if hasFilterResult>extends ${gridName}FilterResult <#elseif hasEditableResultFilter>extends ${gridName}EditableFilterResult </#if>{
<#list grid.resultOnlyAndEditableIntersectionGridColumns as column>
        public ${util.useClass(column.entityAttribute.type)} ${column.refVariableName};
</#list>

        public ${model.searchResultSimpleClassName}() {}
        public ${model.searchResultSimpleClassName}(<#list grid.resultGridColumns as column>${util.useClass(column.entityAttribute.type)} ${column.refVariableName}<#if column_has_next>, </#if></#list>) {
<#list grid.resultGridColumns as column>
            this.${column.refVariableName} = ${column.refVariableName};
</#list>
        }
    }
<#-- Actions class -->
<#list model.openViewActions as openViewAction>
    @${util.useClass("org.codehaus.jackson.annotate.JsonIgnoreProperties")}(ignoreUnknown = true)
    public static class ${openViewAction.simpleClassName} {
    <#list openViewAction.queryModel.parameters as parameter>
        <#if !parameter.value?exists>
        public ${util.useClass(parameter.className)} ${parameter.name};
        </#if>
    </#list>
    }
</#list>
<#-- Filter class -->
    @${util.useClass("org.codehaus.jackson.annotate.JsonIgnoreProperties")}(ignoreUnknown = true)
    public static class ${model.searchFilterSimpleClassName} <#if hasFilterResult>extends ${gridName}FilterResult <#elseif hasEditableResultFilter>extends ${gridName}EditableFilterResult </#if>{
<#-- TODO add target entity id attribute -->
<#list grid.filterOnlyGridColumns as column>
        public ${util.useClass(column.entityAttribute.type)} ${column.refVariableName};
</#list>
<#list grid.columnFilterParams as param>
        public ${util.useClass(param.className)} ${param.name};
</#list>
<#list model.openViewActions as openViewAction>
        public ${util.useClass(openViewAction.className)} ${openViewAction.variableName};
</#list>
    }
    public static class ${model.searchParamSimpleClassName} extends ${util.useClass("com.iorga.ivif.ja.GridSearchParam")}<${util.useClass(model.searchFilterClassName)}> {}
    @${util.useClass("javax.ws.rs.POST")}
    @${util.useClass("javax.ws.rs.Path")}("/search")
    @${util.useClass("javax.ws.rs.Consumes")}(${util.useClass("javax.ws.rs.core.MediaType")}.APPLICATION_JSON)
    @${util.useClass("javax.ws.rs.Produces")}(${util.useClass("javax.ws.rs.core.MediaType")}.APPLICATION_JSON)
    public ${util.useClass("com.mysema.query.SearchResults")}<${util.useClass(model.searchResultClassName)}> search(${util.useClass(model.searchParamClassName)} searchParam) {
<#if grid.serviceSearchMethod?has_content>
        return searchService.${grid.serviceSearchMethod}(searchParam);
<#else>
        return ${serviceVariableName}.search(searchParam);
</#if>
    }
}

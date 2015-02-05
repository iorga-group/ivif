<#include "../utils/rolesAllowedMacro.java.ftl"/>
<#assign grid=model.grid>
<#assign editable=grid.element.editable>
<#assign serviceVariableName=model.baseService.variableName>
<#assign entityClassName=model.baseService.entityClassName>
@${util.useClass("javax.ws.rs.Path", false)}("/${grid.variableName}")
@${util.useClass("com.iorga.ivif.ja.Generated", false)}
@${util.useClass("javax.ejb.Stateless", false)}
<@rolesAllowed rolesAllowed=grid.element.rolesAllowed util=util inCurrentClass=false/>
public class ${grid.element.name}BaseWS {

    @${util.useClass("javax.inject.Inject")} @${util.useClass("com.iorga.ivif.ja.Generated")}
    private ${util.useClass(model.baseService.className)} ${serviceVariableName};

<#if grid.serviceSaveClassname?has_content>
    @${util.useClass("javax.inject.Inject")}
    private ${util.useClass(grid.serviceSaveClassname)} saveService;

</#if>

<#if editable>
    public static class ${model.saveParamSimpleClassName} {
    <#list grid.saveColumns as column>
        public ${util.useClass(column.entityAttribute.type)} ${column.refVariableName};
    </#list>
    }
    @${util.useClass("javax.ws.rs.POST")}
    @${util.useClass("javax.ws.rs.Path")}("/save")
    @${util.useClass("javax.ws.rs.Consumes")}("application/json")
    @${util.useClass("javax.ejb.TransactionAttribute")}
    public void save(${util.useClass("java.util.List")}<${model.saveParamSimpleClassName}> saveParams) {
        for (${model.saveParamSimpleClassName} saveParam : saveParams) {
            // Search for this entityToSave
            ${util.useClass(entityClassName)} entityToSave = ${serviceVariableName}.find(<#list grid.idColumns as column>saveParam.${column.ref}<#if column_has_next>, </#if></#list>);
            if (entityToSave == null) {
                // Must create a new entityToSave
                entityToSave = new ${util.useClass(entityClassName)}();
            }
            // Apply modifications
    <#list grid.editableColumns as column>
            entityToSave.<#list column.refEntityAttributes as entityAttribute><#if entityAttribute_has_next>${entityAttribute.getterName}().<#else>${entityAttribute.setterName}(</#if></#list>saveParam.${column.refVariableName});
    </#list>
    <#if grid.versionColumn?exists>
        <#-- TODO should set versions for each modified sub entities also -->
            // Set version for optimistic lock
            ${serviceVariableName}.detach(entityToSave);
            entityToSave.${grid.versionColumn.entityAttribute.setterName}(saveParam.${grid.versionColumn.refVariableName});
    </#if>

            // Ask for save
    <#if grid.serviceSaveMethod?has_content>
            saveService.${grid.serviceSaveMethod}(entityToSave);
    <#else>
            ${serviceVariableName}.save(entityToSave);
    </#if>
        }
    }

</#if>
<#-- SearchResult class -->
    public static class ${model.searchResultSimpleClassName} <#if editable>extends ${model.saveParamSimpleClassName} </#if>{
<#list grid.selectedWithoutSaveColumns as column>
        public ${util.useClass(column.entityAttribute.type)} ${column.refVariableName};
</#list>

        public ${model.searchResultSimpleClassName}() {}
        public ${model.searchResultSimpleClassName}(<#list grid.selectedColumns as column>${util.useClass(column.entityAttribute.type)} ${column.refVariableName}<#if column_has_next>, </#if></#list>) {
<#list grid.selectedColumns as column>
            this.${column.refVariableName} = ${column.refVariableName};
</#list>
        }
    }
<#-- Actions class -->
<#list model.openViewActions as openViewAction>
    public static class ${openViewAction.simpleClassName} {
    <#list openViewAction.queryModel.parameters as parameter>
        <#if !parameter.value?exists>
        public ${util.useClass(parameter.className)} ${parameter.name};
        </#if>
    </#list>
    }
</#list>
<#-- Filter class -->
    public static class ${model.searchFilterSimpleClassName} extends ${util.useClass(model.searchResultClassName)} {
<#-- TODO add target entity id attribute -->
<#list model.openViewActions as openViewAction>
        public ${util.useClass(openViewAction.className)} ${openViewAction.variableName};
</#list>
    }
    public static class ${model.searchParamSimpleClassName} extends ${util.useClass("com.iorga.ivif.ja.GridSearchParam")}<${util.useClass(model.searchFilterClassName)}> {}
    @${util.useClass("javax.ws.rs.POST")}
    @${util.useClass("javax.ws.rs.Path")}("/search")
    @${util.useClass("javax.ws.rs.Consumes")}("application/json")
    @${util.useClass("javax.ws.rs.Produces")}("application/json")
    public ${util.useClass("com.mysema.query.SearchResults")}<${util.useClass(model.searchResultClassName)}> search(${util.useClass(model.searchParamClassName)} searchParam) {
        return ${serviceVariableName}.search(searchParam);
    }
}

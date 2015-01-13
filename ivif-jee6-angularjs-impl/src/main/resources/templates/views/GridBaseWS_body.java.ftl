<#assign grid=model.grid>
@${util.useClass("javax.ws.rs.Path")}("/${model.variableName}")
@${util.useClass("com.iorga.ivif.ja.Generated")}
@${util.useClass("javax.ejb.Stateless")}
public class ${grid.name}BaseWS {
    @${util.useClass("javax.inject.Inject")} @${util.useClass("com.iorga.ivif.ja.Generated")}
    private ${util.useClass(model.baseService.className)} ${model.baseService.variableName};

    public static class ${model.searchResultSimpleClassName} {
<#list model.columns as column>
        public ${util.useClass(column.entityAttribute.type)} ${column.refVariableName};
</#list>

        public ${model.searchResultSimpleClassName}() {}
        public ${model.searchResultSimpleClassName}(<#list model.columns as column>${util.useClass(column.entityAttribute.type)} ${column.refVariableName}<#if column_has_next>, </#if></#list>) {
<#list model.columns as column>
    <#assign element=column.entityAttribute.element.value>
            this.${column.refVariableName} = ${column.refVariableName};
</#list>
        }
    }
    public static class ${model.searchFilterSimpleClassName} extends ${util.useClass(model.searchResultClassName)} {
<#-- TODO add target entity id attribute -->
    }
    public static class ${model.searchParamSimpleClassName} extends ${util.useClass("com.iorga.ivif.ja.GridSearchParam")}<${util.useClass(model.searchFilterClassName)}> {}
    @${util.useClass("javax.ws.rs.POST")}
    @${util.useClass("javax.ws.rs.Path")}("/search")
    @${util.useClass("javax.ws.rs.Consumes")}("application/json")
    @${util.useClass("javax.ws.rs.Produces")}("application/json")
    public ${util.useClass("com.mysema.query.SearchResults")}<${util.useClass(model.searchResultClassName)}> search(${util.useClass(model.searchParamClassName)} searchParam) {
        return ${model.baseService.variableName}.search(searchParam);
    }
}


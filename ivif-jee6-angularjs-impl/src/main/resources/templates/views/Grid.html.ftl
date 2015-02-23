<#include "../utils/fieldEditorMacro.html.ftl">
<#assign grid=model.grid>
<#assign editable=grid.element.editable>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <fieldset>
                <legend>${grid.title}</legend>
<#if editable || grid.element.toolbar?exists>
                <nav class="navbar navbar-default">
                    <div class="collapse navbar-collapse">
    <#if editable>
                        <button type="button" class="btn btn-default navbar-btn" ng-click="edit()" ng-if="!$edit">Edit</button>
                        <button type="button" class="btn btn-default navbar-btn" ng-click="save()" ng-if="$edit">Save</button>
                        <button type="button" class="btn btn-default navbar-btn" ng-click="cancel()" ng-if="$edit">Cancel</button>
    </#if>
    <#list grid.toolbarButtons as toolbarButton>
                        <button type="button" class="btn btn-default navbar-btn" ng-click="clickOnButton${toolbarButton_index}()"<#rt>
        <#if toolbarButton.disabledIfExpression?has_content> ng-disabled="${toolbarButton.disabledIfExpression.expression}"</#if><#t>
        <#if toolbarButton.rolesAllowed?size &gt; 0> ng-if="<#list toolbarButton.rolesAllowed as rolesAllowed>hasRole(<#list rolesAllowed as roleAllowed>'${roleAllowed}'<#if roleAllowed_has_next>, </#if></#list>)<#if rolesAllowed_has_next> && </#if></#list>"</#if><#t>
        >${toolbarButton.element.title}</button><#lt>
    </#list>
                    </div>
                </nav>
</#if>
                <table ng-table="${grid.variableName}TableParams" show-filter="true" class="table table-bordered table-condensed table-hover">
                    <tr ng-repeat="line in $data"<#if grid.element.onOpen?has_content || grid.singleSelection> ng-click="clickLine(line)"</#if><#rt>
                        <#if grid.highlights?size &gt; 0> ng-class="{<#list grid.highlights as highlight>'${highlight.colorClass}': ${highlight.if}<#if highlight_has_next>, </#if></#list>}"</#if>><#lt>
<#list grid.displayedColumnsOrCode as columnOrCode>
    <#if columnOrCode?is_hash>
        <#-- this is a DisplayedGridColumn -->
        <#assign column=columnOrCode>
        <#assign ivifType=column.entityAttribute.element.name.localPart>
                        <td data-title="'${column.title}'"<#rt>
        <#if !column.entityAttribute.element.value.transient>
 sortable="'${column.refVariableName}'" <#rt>
            <#switch ivifType>
                <#case "string">
                <#case "integer">
            filter="{'${column.refVariableName}':'text'}"<#t>
                    <#break>
                <#case "enum">
            filter="{'${column.refVariableName}':'select'}" filter-data="${column.entityAttribute.element.value.ref}.deferOptionList()"<#t>
                    <#break>
            </#switch>
        </#if>
                            ><#lt>
<@fieldEditor model="line."+column.refVariableName ivifType=ivifType nbTabs=7 editable=(editable && column.editable) editSwitch=column.editSwitch entityAttribute=column.entityAttribute/>
                        </td>
    <#else>
        ${columnOrCode}<#lt>
    </#if>
</#list>
                    </tr>
                </table>
            </fieldset>
        </div>
    </div>
</div>
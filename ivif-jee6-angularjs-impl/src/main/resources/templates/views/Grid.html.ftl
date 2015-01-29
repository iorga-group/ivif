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
                        <button type="button" class="btn btn-default navbar-btn" ng-click="clickOnButton${toolbarButton_index}()">${toolbarButton.element.title}</button>
    </#list>
                    </div>
                </nav>
</#if>
                <table ng-table="${grid.variableName}TableParams" show-filter="true" class="table table-bordered table-condensed table-hover">
                    <tr ng-repeat="line in $data"<#if grid.element.onOpen?has_content || grid.singleSelection> ng-click="clickLine(line)"</#if><#if grid.singleSelection> ng-class="{'active': line.$selected}"</#if>>
<#list grid.displayedColumns as column>
                        <td data-title="'${column.title}'" sortable="'${column.refVariableName}'" filter="{'${column.refVariableName}':'text'}">
<@fieldEditor model="line."+column.refVariableName fieldClassName=column.entityAttribute.type nbTabs=6 editable=(editable && column.element.editable) editSwitch="$edit"/>
                        </td>
</#list>
                    </tr>
                </table>
            </fieldset>
        </div>
    </div>
</div>
<#include "../utils/fieldEditorMacro.html.ftl">
<#assign grid=model.grid>
<#assign editable=grid.element.editable>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <fieldset>
                <legend>${grid.element.title}</legend>
<#if editable>
                <nav class="navbar navbar-default">
                    <button type="button" class="btn btn-default navbar-btn" ng-click="edit()" ng-if="!$edit">Edit</button>
                    <button type="button" class="btn btn-default navbar-btn" ng-click="save()" ng-if="$edit">Save</button>
                    <button type="button" class="btn btn-default navbar-btn" ng-click="cancel()" ng-if="$edit">Cancel</button>
                </nav>
</#if>
                <table ng-table="${grid.variableName}TableParams" show-filter="true" class="table table-bordered table-condensed table-hover table-striped">
                    <tr ng-repeat="line in $data"<#if grid.element.onOpen?has_content> ng-click="openLine(line)"</#if>>
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
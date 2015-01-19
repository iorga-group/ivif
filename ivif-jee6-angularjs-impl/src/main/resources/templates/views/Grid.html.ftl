<#assign grid=model.grid>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <fieldset>
                <legend>${grid.element.title}</legend>
                <table ng-table="${grid.variableName}TableParams" show-filter="true" class="table table-bordered table-condensed table-hover table-striped">
                    <tr ng-repeat="line in $data"<#if grid.element.onOpen?has_content> ng-click="openLine(line)"</#if>>
<#list grid.displayedColumns as column>
                        <td data-title="'${column.title}'" sortable="'${column.refVariableName}'" filter="{'${column.refVariableName}':'text'}">
                            {{line.${column.refVariableName}}}
                        </td>
</#list>
                    </tr>
                </table>
            </fieldset>
        </div>
    </div>
</div>
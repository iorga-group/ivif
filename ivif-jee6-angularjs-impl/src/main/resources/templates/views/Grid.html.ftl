<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <fieldset>
                <legend>${grid.title}</legend>
                <table ng-table="${model.variableName}TableParams" class="table table-bordered table-condensed table-hover table-striped">
                    <tr ng-repeat="line in $data">
<#list model.columns as column>
                        <td data-title="'${column.title}'" sortable="'${column.refVariableName}'">
                            {{line.${column.refVariableName}}}
                        </td>
</#list>
                    </tr>
                </table>
            </fieldset>
        </div>
    </div>
</div>
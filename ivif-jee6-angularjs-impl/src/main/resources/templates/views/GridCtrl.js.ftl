<#assign grid=model.grid>
<#assign editable=grid.element.editable>
<#assign tableParamsVariableName=grid.variableName+"TableParams">
<#if grid.onOpen?exists>
    <#assign onOpenMethod=grid.onOpen.injections[0]>
    <#assign onOpenCode=grid.onOpen.expression>
</#if>
'use strict';

angular.module('${model.configuration.angularModuleName}')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/${grid.variableName}', {
                    templateUrl: 'templates/views/${grid.element.name}.html',
                    controller: '${grid.element.name}Ctrl'
                });
        }])
    .controller('${grid.element.name}Ctrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService'<#list model.injections as injection>, '${injection}'</#list><#if model.actionOpenViewDefined>, '$location', 'locationUtils'</#if>, function($scope, ngTableParams, $timeout, $http, locationService<#list model.injections as injection>, ${injection}</#list><#if model.actionOpenViewDefined>, $location, locationUtils</#if>) {
        // Utils
<#if editable || grid.singleSelection>
        function getIdForLine(line) {
            return <#list grid.idColumns as idColumn>line.${idColumn.refVariableName}<#if idColumn_has_next>+':'+</#if></#list>;
        }
</#if>
        // Declare actions
<#if onOpenCode?exists>
        $scope.clickLine = function(selectedLine) {
            ${onOpenCode};
        };
</#if>
<#if grid.singleSelection>
        $scope.clickLine = function(selectedLine) {
            // unselect previous selected line if any
            if ($scope.selectedLine) {
                delete $scope.selectedLine.$selected;
            }
            $scope.selectedLine = selectedLine;
            $scope.selectedLineId = getIdForLine(selectedLine);
            selectedLine.$selected = true;
    <#if grid.onSelect?exists>
            // on-select call
            ${grid.onSelect.expression};
    </#if>
        };
</#if>
<#list grid.toolbarButtons as toolbarButton>
        $scope.clickOnButton${toolbarButton_index} = function() {
            ${toolbarButton.jsExpression.expression};
        };
</#list>
<#if editable>
        $scope.edit = function() {
            $scope.$edit = true;
            $scope.editedLinesById = {};
            $scope.${tableParamsVariableName}.reload();
        };
        $scope.save = function() {
            // Send only modified lines to server, thanks to http://stackoverflow.com/a/26975765/535203
            var linesToSave = [];
            angular.forEach($scope.editedLinesById, function(editedLine) {
                if (!angular.equals(editedLine, editedLine.$original)) {
                    linesToSave.push({
    <#list grid.saveColumns as column>
                        ${column.refVariableName}: editedLine.${column.refVariableName}<#if column_has_next>,</#if>
    </#list>
                    });
                }
            });
            if (linesToSave.length > 0) {
                // call save function
                $http.post('api/${grid.variableName}/save', linesToSave).success(function() {
                    // All was OK, let's ask to refresh the data
                    $scope.cancel();
                });
            }
        };
        $scope.cancel = function() {
            $scope.$edit = false;
            $scope.editedLinesById = null;
            $scope.${tableParamsVariableName}.reload();
        };
</#if>

        // Init variables
        function getData($defer, params) {
            var sorting = {
                ref: null,
                type: null
            };
            var paramsSorting = params.sorting();
            for (var field in paramsSorting) {
                sorting.ref = field;
                sorting.type = paramsSorting[field];
            }
            $http.post('api/${grid.variableName}/search', {
                limit: params.count(),
                offset: (params.page() - 1) * params.count(),
                sorting: sorting,
                filter: params.filter()
            }).success(function(data) {
                params.total(data.total);
                var results = data.results;
<#if editable>
                if ($scope.$edit) {
                    results = [];
                    var editedLinesById = $scope.editedLinesById;
                    angular.forEach(data.results, function(result) {
                        var id = getIdForLine(result);
                        var editedLine = editedLinesById[id];
                        if (editedLine === undefined) {
                            editedLine = angular.copy(result);
                            editedLine.$original = result;
                            editedLinesById[id] = editedLine;
                        }
                        results.push(editedLine);
                    });
                }
</#if>
<#if grid.singleSelection>
                if ($scope.selectedLineId) {
                    // search if the selected line id is in current results and flag the result in that case
                    for (var i = 0; i < results.length; i++) {
                        var result = results[i],
                            id = getIdForLine(result);
                        if (id === $scope.selectedLineId) {
                            $scope.selectedLine = result;
                            result.$selected = true;
                            break;
                        }
                    }
                }
</#if>
                $defer.resolve(results);
            });
        }

        if (!locationService.initializeController($scope)) {
<#if editable>
            $scope.editedLinesById = {};
</#if>
            $scope.${tableParamsVariableName} = new ngTableParams({
                page: 1,
                count: 10<#if model.actionOpenViewDefined>,
                filter: locationUtils.fromSearchToObject($location.search())</#if>
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            // refresh getData function as the $scope is different from the original one
            $scope.${tableParamsVariableName}.settings({
                total: 0, // length of data
                getData: getData
            });
        }
        locationService.controllerInitialized('${grid.tabTitle}', $scope, ['${tableParamsVariableName}'<#if editable>, 'editedLinesById'</#if><#if grid.singleSelection>, 'selectedLineId'</#if>]);
    }])
;

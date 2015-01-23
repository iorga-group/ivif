<#assign grid=model.grid>
<#assign editable=grid.element.editable>
<#assign tableParamsVariableName=grid.variableName+"TableParams">
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
    .controller('${grid.element.name}Ctrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService'<#if model.onOpenMethod?has_content>, '${model.onOpenMethod}'</#if><#if model.actionOpenViewDefined>, '$location', 'locationUtils'</#if>, function($scope, ngTableParams, $timeout, $http, locationService<#if model.onOpenMethod?has_content>, ${model.onOpenMethod}</#if><#if model.actionOpenViewDefined>, $location, locationUtils</#if>) {
        // Declare actions
<#if model.onOpenCode?has_content>
        $scope.openLine = function(line) {
            ${model.onOpenCode};
        };
</#if>
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
                getData: function($defer, params) {
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
<#if editable>
                        var results = data.results;
                        if ($scope.$edit) {
                            results = [];
                            var editedLinesById = $scope.editedLinesById;
                            angular.forEach(data.results, function(result) {
                                var id = <#list grid.idColumns as idColumn>result.${idColumn.refVariableName}<#if idColumn_has_next>+':'+</#if></#list>;
                                var editedLine = editedLinesById[id];
                                if (editedLine === undefined) {
                                    editedLine = angular.copy(result);
                                    editedLine.$original = result;
                                    editedLinesById[id] = editedLine;
                                }
                                results.push(editedLine);
                            });
                        }
                        $defer.resolve(results);
<#else>
                        $defer.resolve(data.results);
</#if>
                    });
                }
            });
        }
        locationService.controllerInitialized('${grid.element.title}', $scope, ['${tableParamsVariableName}'<#if editable>, 'editedLinesById'</#if>]);
    }])
;

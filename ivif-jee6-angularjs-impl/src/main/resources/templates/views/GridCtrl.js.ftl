<#assign grid=model.grid>
<#assign editable=grid.editable>
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
    .controller('${grid.element.name}Ctrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService'<#list model.injections as injection>, '${injection}'</#list>, '$location', 'locationUtils', function($scope, ngTableParams, $timeout, $http, locationService<#list model.injections as injection>, ${injection}</#list>, $location, locationUtils) {
        // Utils
<#if editable || grid.singleSelection>
        $scope.getIdForLine = function(line) {
            return <#list grid.idColumns as idColumn>line.${idColumn.refVariableName}<#if idColumn_has_next>+':'+</#if></#list>;
        };
</#if>
        // Declare actions
<#if grid.singleSelection>
        $scope.clickLine = function(selectedLine) {
            // unselect previous selected line if any
            if ($scope.selectedLine) {
                delete $scope.selectedLine.$selected;
            }
            $scope.selectedLine = selectedLine;
            $scope.selectedLineId = $scope.getIdForLine(selectedLine);
            selectedLine.$selected = true;
        };
</#if>
<#if editable>
        $scope.edit = function() {
            $scope.$edit = true;
            $scope.editedLinesById = {};
            $scope.dirtyLinesById = {};
            $scope.validDirtyLinesById = {};
            $scope.invalidDirtyLinesById = {};
            $scope.$dirtyGrid = false;
            $scope.$validDirtyGrid = false;
            $scope.${tableParamsVariableName}.reload();
            $scope.dirtyCheckKey = locationService.addDirtyCheck(function() {
                return $scope.$dirtyGrid;
            });
        };
        function objectEmpty(obj) {
            for (var f in obj) {
                if (obj.hasOwnProperty(f)) {
                    return false;
                }
            }
            return true;
        }
        $scope.onLineChange = function(line, fieldName) {
            // check dirty
            var dirty = !angular.equals(line[fieldName], line.$original[fieldName]) || !angular.equals(line, line.$original),
                id = $scope.getIdForLine(line);
            line.$dirty = dirty;
            if (dirty) {
                $scope.dirtyLinesById[id] = line;
                // check validity
                if (line.$modelCtrls[fieldName].$valid) {
                    $scope.validDirtyLinesById[id] = line;
                    delete $scope.invalidDirtyLinesById[id];
                } else {
                    $scope.invalidDirtyLinesById[id] = line;
                    delete $scope.validDirtyLinesById[id];
                }
            } else {
                delete $scope.dirtyLinesById[id];
                delete $scope.validDirtyLinesById[id];
                delete $scope.invalidDirtyLinesById[id];
            }
            $scope.$dirtyGrid = !objectEmpty($scope.dirtyLinesById);
            $scope.$validDirtyGrid = !objectEmpty($scope.validDirtyLinesById) && objectEmpty($scope.invalidDirtyLinesById);
        };
        $scope.$isDirty = function(line) {
            return line.$dirty;
        };
        $scope.save = function() {
            // Send only modified lines to server, thanks to http://stackoverflow.com/a/26975765/535203
            var linesToSave = [];
            angular.forEach($scope.validDirtyLinesById, function(editedLine) {
                linesToSave.push({
    <#list grid.editableGridColumns as column>
                    ${column.refVariableName}: editedLine.${column.refVariableName}<#if column_has_next>,</#if>
    </#list>
                });
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
            delete $scope.editedLinesById;
            delete $scope.dirtyLinesById;
            delete $scope.validDirtyLinesById;
            delete $scope.invalidDirtyLinesById;
            delete $scope.$dirtyGrid;
            delete $scope.$validDirtyGrid;
            $scope.${tableParamsVariableName}.reload();
            locationService.removeDirtyCheck($scope.dirtyCheckKey);
        };
</#if>

        // Init variables
<#list model.injections as injection>
        $scope.${injection} = ${injection};
    <#if !injection_has_next>

    </#if>
</#list>
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
                        var id = $scope.getIdForLine(result);
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
                            id = $scope.getIdForLine(result);
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
                count: 10,
                filter: locationUtils.fromSearchToObject($location.search())
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
        locationService.controllerInitialized('${grid.tabTitle}', $scope, ['${tableParamsVariableName}'<#if editable>, 'editedLinesById', '$edit', 'validDirtyLinesById', 'invalidDirtyLinesById', 'dirtyLinesById', '$dirtyGrid', '$validDirtyGrid', 'dirtyCheckKey'</#if><#if grid.singleSelection>, 'selectedLineId'</#if>]);
    }])
;

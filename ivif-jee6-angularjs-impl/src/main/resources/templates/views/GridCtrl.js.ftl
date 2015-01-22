<#assign grid=model.grid>
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

        // Init variables
        if (!locationService.initializeController($scope)) {
            $scope.${grid.variableName}TableParams = new ngTableParams({
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
                        $defer.resolve(data.results);
                    });
                }
            });
        }
        locationService.controllerInitialized('${grid.element.title}', $scope, ['${grid.variableName}TableParams']);
    }])
;

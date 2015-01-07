'use strict';

angular.module('sara')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/${model.variableName}', {
                    templateUrl: 'templates/views/${grid.name}.html',
                    controller: '${grid.name}Ctlr'
                });
        }])
    .controller('${grid.name}Ctlr', ['$scope', 'ngTableParams', '$timeout', '$http', function($scope, ngTableParams, $timeout, $http) {
        $scope.${model.variableName}TableParams = new ngTableParams({
            page: 1,
            count: 10
        }, {
            total: 0, // length of data
            getData: function($defer, params) {
                var sorting = {
                    ref: null,
                    type: null
                };
                var paramsSorting = params.sorting();
                for (field in paramsSorting) {
                    sorting.ref = field;
                    sorting.type = paramsSorting[field];
                }
                $http.post('api/${model.variableName}/search', {
                    limit: params.count(),
                    offset: (params.page() - 1) * params.count(),
                    sorting: params.sorting(),
                    filter: params.filter()
                }).success(function(data) {
                    $defer.resolve(data.results);
                });
            }
        })
    }]);
;

'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/computerGrid', {
                    templateUrl: 'templates/views/ComputerGrid.html',
                    controller: 'ComputerGridCtrl'
                });
        }])
    .controller('ComputerGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'openUserGridFromComputerAction', '$location', 'locationUtils', function($scope, ngTableParams, $timeout, $http, locationService, openUserGridFromComputerAction, $location, locationUtils) {
        // Utils
        // Declare actions
        $scope.clickLine = function(selectedLine) {
            openUserGridFromComputerAction({userId:selectedLine.user_id});
        };

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
            $http.post('api/computerGrid/search', {
                limit: params.count(),
                offset: (params.page() - 1) * params.count(),
                sorting: sorting,
                filter: params.filter()
            }).success(function(data) {
                params.total(data.total);
                var results = data.results;
                $defer.resolve(results);
            });
        }

        if (!locationService.initializeController($scope)) {
            $scope.computerGridTableParams = new ngTableParams({
                page: 1,
                count: 10,
                filter: locationUtils.fromSearchToObject($location.search())
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            // refresh getData function as the $scope is different from the original one
            $scope.computerGridTableParams.settings({
                total: 0, // length of data
                getData: getData
            });
        }
        locationService.controllerInitialized('Computers', $scope, ['computerGridTableParams']);
    }])
;

'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/computerToCurrentUserDesktopSessionGrid', {
                    templateUrl: 'templates/views/ComputerToCurrentUserDesktopSessionGrid.html',
                    controller: 'ComputerToCurrentUserDesktopSessionGridCtrl'
                });
        }])
    .controller('ComputerToCurrentUserDesktopSessionGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'openCurrentUserDesktopSessionGridFromComputerAction', '$location', 'locationUtils', function($scope, ngTableParams, $timeout, $http, locationService, openCurrentUserDesktopSessionGridFromComputerAction, $location, locationUtils) {
        // Utils
        // Declare actions
        $scope.clickLine = function(selectedLine) {
            openCurrentUserDesktopSessionGridFromComputerAction({computerId:selectedLine.id});
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
            $http.post('api/computerToCurrentUserDesktopSessionGrid/search', {
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
            $scope.computerToCurrentUserDesktopSessionGridTableParams = new ngTableParams({
                page: 1,
                count: 10,
                filter: locationUtils.fromSearchToObject($location.search())
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            // refresh getData function as the $scope is different from the original one
            $scope.computerToCurrentUserDesktopSessionGridTableParams.settings({
                total: 0, // length of data
                getData: getData
            });
        }
        locationService.controllerInitialized('Computers', $scope, ['computerToCurrentUserDesktopSessionGridTableParams']);
    }])
;

'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/computerToDesktopSessionGrid', {
                    templateUrl: 'templates/views/ComputerToDesktopSessionGrid.html',
                    controller: 'ComputerToDesktopSessionGridCtrl'
                });
        }])
    .controller('ComputerToDesktopSessionGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'openDesktopSessionGridFromComputerAction', function($scope, ngTableParams, $timeout, $http, locationService, openDesktopSessionGridFromComputerAction) {
        // Utils
        // Declare actions
        $scope.clickLine = function(selectedLine) {
            openDesktopSessionGridFromComputerAction({userId:selectedLine.user_id,computerId:selectedLine.id});
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
            $http.post('api/computerToDesktopSessionGrid/search', {
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
            $scope.computerToDesktopSessionGridTableParams = new ngTableParams({
                page: 1,
                count: 10,
                filter: locationUtils.fromSearchToObject($location.search())
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            // refresh getData function as the $scope is different from the original one
            $scope.computerToDesktopSessionGridTableParams.settings({
                total: 0, // length of data
                getData: getData
            });
        }
        locationService.controllerInitialized('Computers', $scope, ['computerToDesktopSessionGridTableParams']);
    }])
;

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
    .controller('ComputerToCurrentUserDesktopSessionGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'openCurrentUserDesktopSessionGridFromComputerAction', function($scope, ngTableParams, $timeout, $http, locationService, openCurrentUserDesktopSessionGridFromComputerAction) {
        // Declare actions
        $scope.openLine = function($line) {
            openCurrentUserDesktopSessionGridFromComputerAction({computerId: $line.id});
        };

        // Init variables
        if (!locationService.initializeController($scope)) {
            $scope.computerToCurrentUserDesktopSessionGridTableParams = new ngTableParams({
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
                        $defer.resolve(data.results);
                    });
                }
            });
        }
        locationService.controllerInitialized('Computers', $scope, ['computerToCurrentUserDesktopSessionGridTableParams']);
    }])
;

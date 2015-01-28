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
        // Declare actions
        $scope.openLine = function($line) {
            openDesktopSessionGridFromComputerAction({userId: $line.user_id, computerId: $line.id});
        };

        // Init variables
        if (!locationService.initializeController($scope)) {
            $scope.computerToDesktopSessionGridTableParams = new ngTableParams({
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
                    $http.post('api/computerToDesktopSessionGrid/search', {
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
        locationService.controllerInitialized('Computers', $scope, ['computerToDesktopSessionGridTableParams']);
    }])
;

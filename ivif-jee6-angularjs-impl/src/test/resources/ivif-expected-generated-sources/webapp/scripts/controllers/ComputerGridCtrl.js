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
    .controller('ComputerGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'openUserGridFromComputerAction', function($scope, ngTableParams, $timeout, $http, locationService, openUserGridFromComputerAction) {
        // Declare actions
        $scope.openLine = function($line) {
            openUserGridFromComputerAction({userId: $line.user_id});
        };

        // Init variables
        if (!locationService.initializeController($scope)) {
            $scope.computerGridTableParams = new ngTableParams({
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
                    $http.post('api/computerGrid/search', {
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
        locationService.controllerInitialized('Computers', $scope, ['computerGridTableParams']);
    }])
;

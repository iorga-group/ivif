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
        $scope.getCurrentScope = function() { return $scope; };

        // Init variables
        $scope.openCurrentUserDesktopSessionGridFromComputerAction = openCurrentUserDesktopSessionGridFromComputerAction;

        function getData($defer, params) {
            var $scope = params.settings().$scope.getCurrentScope(), // get the current controller scope
                sorting = {
                    ref: null,
                    type: null
                };
            if ($scope.reinitPage) {
                params.page($scope.reinitPage);
                delete $scope.reinitPage;
            }
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
            $scope.reinitPage = $scope.computerToCurrentUserDesktopSessionGridTableParams.page(); // fix a bug occurring when backing to the grid from another screen: page is reinitialized to 1 by ng-tables
        }
        locationService.controllerInitialized('Computers', $scope, ['computerToCurrentUserDesktopSessionGridTableParams']);
    }])
;

'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/desktopSessionGrid', {
                    templateUrl: 'templates/views/DesktopSessionGrid.html',
                    controller: 'DesktopSessionGridCtrl'
                });
        }])
    .controller('DesktopSessionGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', '$location', 'locationUtils', function($scope, ngTableParams, $timeout, $http, locationService, $location, locationUtils) {
        // Declare actions

        // Init variables
        if (!locationService.initializeController($scope)) {
            $scope.desktopSessionGridTableParams = new ngTableParams({
                page: 1,
                count: 10,
                filter: locationUtils.fromSearchToObject($location.search())
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
                    $http.post('api/desktopSessionGrid/search', {
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
        locationService.controllerInitialized('Sessions', $scope, ['desktopSessionGridTableParams']);
    }])
;

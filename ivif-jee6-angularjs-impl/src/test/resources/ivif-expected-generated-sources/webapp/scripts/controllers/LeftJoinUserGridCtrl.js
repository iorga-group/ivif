'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/leftJoinUserGrid', {
                    templateUrl: 'templates/views/LeftJoinUserGrid.html',
                    controller: 'LeftJoinUserGridCtrl'
                });
        }])
    .controller('LeftJoinUserGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'testMethod', '$location', 'locationUtils', function($scope, ngTableParams, $timeout, $http, locationService, testMethod, $location, locationUtils) {
        // Utils
        // Declare actions
        $scope.getCurrentScope = function() { return $scope; };

        // Init variables
        $scope.testMethod = testMethod;

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
            $http.post('api/leftJoinUserGrid/search', {
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
            $scope.leftJoinUserGridTableParams = new ngTableParams({
                page: 1,
                count: 10,
                filter: locationUtils.fromSearchToObject($location.search())
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            $scope.reinitPage = $scope.leftJoinUserGridTableParams.page(); // fix a bug occurring when backing to the grid from another screen: page is reinitialized to 1 by ng-tables
        }
        locationService.controllerInitialized('Left Join User Grid', $scope, ['leftJoinUserGridTableParams']);
    }])
;

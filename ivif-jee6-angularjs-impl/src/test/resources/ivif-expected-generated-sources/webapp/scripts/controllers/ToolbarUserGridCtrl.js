'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/toolbarUserGrid', {
                    templateUrl: 'templates/views/ToolbarUserGrid.html',
                    controller: 'ToolbarUserGridCtrl'
                });
        }])
    .controller('ToolbarUserGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'openProfileGridFromUserAction', 'openComputerGridFromUserAction', function($scope, ngTableParams, $timeout, $http, locationService, openProfileGridFromUserAction, openComputerGridFromUserAction) {
        // Declare actions
        $scope.clickLine = function(selectedLine) {
            $scope.selectedLine = selectedLine;
        };
        $scope.clickOnButton0 = function() {
            openProfileGridFromUserAction({profileId:$scope.selectedLine.profile_id});
        }
        $scope.clickOnButton1 = function() {
            openComputerGridFromUserAction({userId:$scope.selectedLine.id});
        }

        // Init variables
        if (!locationService.initializeController($scope)) {
            $scope.toolbarUserGridTableParams = new ngTableParams({
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
                    $http.post('api/toolbarUserGrid/search', {
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
        locationService.controllerInitialized('Toolbar User Grid', $scope, ['toolbarUserGridTableParams', 'selectedLine']);
    }])
;

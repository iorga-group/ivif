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
        // Utils
        function getIdForLine(line) {
            return line.id;
        }
        // Declare actions
        $scope.clickLine = function(selectedLine) {
            // unselect previous selected line if any
            if ($scope.selectedLine) {
                delete $scope.selectedLine.$selected;
            }
            $scope.selectedLine = selectedLine;
            $scope.selectedLineId = getIdForLine(selectedLine);
            selectedLine.$selected = true;
        };
        $scope.clickOnButton0 = function() {
            openProfileGridFromUserAction({profileId:$scope.selectedLine.profile_id});
        };
        $scope.clickOnButton1 = function() {
            openComputerGridFromUserAction({userId:$scope.selectedLine.id});
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
            $http.post('api/toolbarUserGrid/search', {
                limit: params.count(),
                offset: (params.page() - 1) * params.count(),
                sorting: sorting,
                filter: params.filter()
            }).success(function(data) {
                params.total(data.total);
                var results = data.results;
                if ($scope.selectedLineId) {
                    // search if the selected line id is in current results and flag the result in that case
                    for (var i = 0; i < results.length; i++) {
                        var result = results[i],
                            id = getIdForLine(result);
                        if (id === $scope.selectedLineId) {
                            $scope.selectedLine = result;
                            result.$selected = true;
                            break;
                        }
                    }
                }
                $defer.resolve(results);
            });
        }

        if (!locationService.initializeController($scope)) {
            $scope.toolbarUserGridTableParams = new ngTableParams({
                page: 1,
                count: 10
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            // refresh getData function as the $scope is different from the original one
            $scope.toolbarUserGridTableParams.settings({
                total: 0, // length of data
                getData: getData
            });
        }
        locationService.controllerInitialized('Toolbar User Grid', $scope, ['toolbarUserGridTableParams', 'selectedLineId']);
    }])
;

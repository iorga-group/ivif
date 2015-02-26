'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/selectEditableAndButtonUserGrid', {
                    templateUrl: 'templates/views/SelectEditableAndButtonUserGrid.html',
                    controller: 'SelectEditableAndButtonUserGridCtrl'
                });
        }])
    .controller('SelectEditableAndButtonUserGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'aService', 'aService2', '$location', 'locationUtils', function($scope, ngTableParams, $timeout, $http, locationService, aService, aService2, $location, locationUtils) {
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
            // on-select call
            aService(selectedLine.id);
        };
        $scope.clickOnButton0 = function() {
            aService2($scope.selectedLine.id);
        };
        $scope.edit = function() {
            $scope.$edit = true;
            $scope.editedLinesById = {};
            $scope.selectEditableAndButtonUserGridTableParams.reload();
        };
        $scope.save = function() {
            // Send only modified lines to server, thanks to http://stackoverflow.com/a/26975765/535203
            var linesToSave = [];
            angular.forEach($scope.editedLinesById, function(editedLine) {
                if (!angular.equals(editedLine, editedLine.$original)) {
                    linesToSave.push({
                        id: editedLine.id,
                        version: editedLine.version
                    });
                }
            });
            if (linesToSave.length > 0) {
                // call save function
                $http.post('api/selectEditableAndButtonUserGrid/save', linesToSave).success(function() {
                    // All was OK, let's ask to refresh the data
                    $scope.cancel();
                });
            }
        };
        $scope.cancel = function() {
            $scope.$edit = false;
            $scope.editedLinesById = null;
            $scope.selectEditableAndButtonUserGridTableParams.reload();
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
            $http.post('api/selectEditableAndButtonUserGrid/search', {
                limit: params.count(),
                offset: (params.page() - 1) * params.count(),
                sorting: sorting,
                filter: params.filter()
            }).success(function(data) {
                params.total(data.total);
                var results = data.results;
                if ($scope.$edit) {
                    results = [];
                    var editedLinesById = $scope.editedLinesById;
                    angular.forEach(data.results, function(result) {
                        var id = getIdForLine(result);
                        var editedLine = editedLinesById[id];
                        if (editedLine === undefined) {
                            editedLine = angular.copy(result);
                            editedLine.$original = result;
                            editedLinesById[id] = editedLine;
                        }
                        results.push(editedLine);
                    });
                }
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
            $scope.editedLinesById = {};
            $scope.selectEditableAndButtonUserGridTableParams = new ngTableParams({
                page: 1,
                count: 10,
                filter: locationUtils.fromSearchToObject($location.search())
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            // refresh getData function as the $scope is different from the original one
            $scope.selectEditableAndButtonUserGridTableParams.settings({
                total: 0, // length of data
                getData: getData
            });
        }
        locationService.controllerInitialized('Select Editable And Button User Grid', $scope, ['selectEditableAndButtonUserGridTableParams', 'editedLinesById', 'selectedLineId']);
    }])
;

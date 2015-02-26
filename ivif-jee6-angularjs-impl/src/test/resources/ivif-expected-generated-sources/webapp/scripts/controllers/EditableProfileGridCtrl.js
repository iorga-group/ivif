'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/editableProfileGrid', {
                    templateUrl: 'templates/views/EditableProfileGrid.html',
                    controller: 'EditableProfileGridCtrl'
                });
        }])
    .controller('EditableProfileGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', function($scope, ngTableParams, $timeout, $http, locationService) {
        // Utils
        function getIdForLine(line) {
            return line.id;
        }
        // Declare actions
        $scope.edit = function() {
            $scope.$edit = true;
            $scope.editedLinesById = {};
            $scope.editableProfileGridTableParams.reload();
        };
        $scope.save = function() {
            // Send only modified lines to server, thanks to http://stackoverflow.com/a/26975765/535203
            var linesToSave = [];
            angular.forEach($scope.editedLinesById, function(editedLine) {
                if (!angular.equals(editedLine, editedLine.$original)) {
                    linesToSave.push({
                        name: editedLine.name,
                        id: editedLine.id
                    });
                }
            });
            if (linesToSave.length > 0) {
                // call save function
                $http.post('api/editableProfileGrid/save', linesToSave).success(function() {
                    // All was OK, let's ask to refresh the data
                    $scope.cancel();
                });
            }
        };
        $scope.cancel = function() {
            $scope.$edit = false;
            $scope.editedLinesById = null;
            $scope.editableProfileGridTableParams.reload();
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
            $http.post('api/editableProfileGrid/search', {
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
                $defer.resolve(results);
            });
        }

        if (!locationService.initializeController($scope)) {
            $scope.editedLinesById = {};
            $scope.editableProfileGridTableParams = new ngTableParams({
                page: 1,
                count: 10,
                filter: locationUtils.fromSearchToObject($location.search())
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            // refresh getData function as the $scope is different from the original one
            $scope.editableProfileGridTableParams.settings({
                total: 0, // length of data
                getData: getData
            });
        }
        locationService.controllerInitialized('Profiles', $scope, ['editableProfileGridTableParams', 'editedLinesById']);
    }])
;

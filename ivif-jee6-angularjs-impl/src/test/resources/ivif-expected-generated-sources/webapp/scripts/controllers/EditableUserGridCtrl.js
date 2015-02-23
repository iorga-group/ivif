'use strict';

angular.module('test')
    .config(['$routeProvider',
        function($routeProvider) {
            $routeProvider.
                when('/editableUserGrid', {
                    templateUrl: 'templates/views/EditableUserGrid.html',
                    controller: 'EditableUserGridCtrl'
                });
        }])
    .controller('EditableUserGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'openProfileGridFromUserAction', function($scope, ngTableParams, $timeout, $http, locationService, openProfileGridFromUserAction) {
        // Utils
        function getIdForLine(line) {
            return line.id;
        }
        // Declare actions
        $scope.clickLine = function(selectedLine) {
            if (!$scope.$edit) {
                openProfileGridFromUserAction({profileId:selectedLine.profile_id});
            }
        };
        $scope.edit = function() {
            $scope.$edit = true;
            $scope.editedLinesById = {};
            $scope.editableUserGridTableParams.reload();
        };
        $scope.save = function() {
            // Send only modified lines to server, thanks to http://stackoverflow.com/a/26975765/535203
            var linesToSave = [];
            angular.forEach($scope.editedLinesById, function(editedLine) {
                if (!angular.equals(editedLine, editedLine.$original)) {
                    linesToSave.push({
                        name: editedLine.name,
                        status: editedLine.status,
                        commentTemp: editedLine.commentTemp,
                        enabled: editedLine.enabled,
                        id: editedLine.id,
                        version: editedLine.version
                    });
                }
            });
            if (linesToSave.length > 0) {
                // call save function
                $http.post('api/editableUserGrid/save', linesToSave).success(function() {
                    // All was OK, let's ask to refresh the data
                    $scope.cancel();
                });
            }
        };
        $scope.cancel = function() {
            $scope.$edit = false;
            $scope.editedLinesById = null;
            $scope.editableUserGridTableParams.reload();
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
            $http.post('api/editableUserGrid/search', {
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
            $scope.editableUserGridTableParams = new ngTableParams({
                page: 1,
                count: 10
            }, {
                total: 0, // length of data
                getData: getData
            });
        } else {
            // refresh getData function as the $scope is different from the original one
            $scope.editableUserGridTableParams.settings({
                total: 0, // length of data
                getData: getData
            });
        }
        locationService.controllerInitialized('Users', $scope, ['editableUserGridTableParams', 'editedLinesById']);
    }])
;

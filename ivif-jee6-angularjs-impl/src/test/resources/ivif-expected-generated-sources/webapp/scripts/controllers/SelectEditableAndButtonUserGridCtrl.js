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
    .controller('SelectEditableAndButtonUserGridCtrl', ['$scope', 'ngTableParams', '$timeout', '$http', 'locationService', 'aService', 'aService2', 'anotherService', 'myOtherService', '$location', 'locationUtils', function($scope, ngTableParams, $timeout, $http, locationService, aService, aService2, anotherService, myOtherService, $location, locationUtils) {
        // Utils
        $scope.getIdForLine = function(line) {
            return line.id;
        };
        // Declare actions
        $scope.clickLine = function(selectedLine) {
            // unselect previous selected line if any
            if ($scope.selectedLine) {
                delete $scope.selectedLine.$selected;
            }
            $scope.selectedLine = selectedLine;
            $scope.selectedLineId = $scope.getIdForLine(selectedLine);
            selectedLine.$selected = true;
        };
        $scope.edit = function() {
            $scope.$edit = true;
            $scope.editedLinesById = {};
            $scope.dirtyLinesById = {};
            $scope.validDirtyLinesById = {};
            $scope.invalidDirtyLinesById = {};
            $scope.$dirtyGrid = false;
            $scope.$validDirtyGrid = false;
            $scope.selectEditableAndButtonUserGridTableParams.reload();
            $scope.dirtyCheckKey = locationService.addDirtyCheck(function() {
                return $scope.$dirtyGrid;
            });
        };
        function objectEmpty(obj) {
            for (var f in obj) {
                if (obj.hasOwnProperty(f)) {
                    return false;
                }
            }
            return true;
        }
        $scope.onLineValueChange = function(line, fieldName) {
            // check dirty
            var dirty = !angular.equals(line[fieldName], line.$original[fieldName]) || !angular.equals(line, line.$original),
                id = $scope.getIdForLine(line);
            line.$dirty = dirty;
            if (dirty) {
                $scope.dirtyLinesById[id] = line;
            } else {
                delete $scope.dirtyLinesById[id];
            }
            $scope.$dirtyGrid = !objectEmpty($scope.dirtyLinesById);
            // also check valid status in order to enable or disable the save button
            $scope.onLineValidStatusChange(line, fieldName);
        };
        $scope.onLineValidStatusChange = function(line, fieldName) {
            var id = $scope.getIdForLine(line);
            if (line.$dirty) {
                // check validity
                var invalidCtrls = line.$invalidCtrls,
                    modelCtrl = line.$modelCtrls[fieldName];
                if (!invalidCtrls) {
                    invalidCtrls = line.$invalidCtrls = {};
                }
                if (!modelCtrl.$valid) {
                    invalidCtrls[fieldName] = modelCtrl;
                } else {
                    delete invalidCtrls[fieldName];
                }
                if (objectEmpty(invalidCtrls)) {
                    $scope.validDirtyLinesById[id] = line;
                    delete $scope.invalidDirtyLinesById[id];
                } else {
                    $scope.invalidDirtyLinesById[id] = line;
                    delete $scope.validDirtyLinesById[id];
                }
            } else {
                delete $scope.validDirtyLinesById[id];
                delete $scope.invalidDirtyLinesById[id];
            }
            $scope.$validDirtyGrid = !objectEmpty($scope.validDirtyLinesById) && objectEmpty($scope.invalidDirtyLinesById);
        };
        $scope.$isDirty = function(line) {
            return line.$dirty;
        };
        $scope.save = function() {
            // Send only modified lines to server, thanks to http://stackoverflow.com/a/26975765/535203
            var linesToSave = [];
            angular.forEach($scope.validDirtyLinesById, function(editedLine) {
                linesToSave.push({
                    firstName: editedLine.firstName,
                    id: editedLine.id,
                    version: editedLine.version
                });
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
            delete $scope.editedLinesById;
            delete $scope.dirtyLinesById;
            delete $scope.validDirtyLinesById;
            delete $scope.invalidDirtyLinesById;
            delete $scope.$dirtyGrid;
            delete $scope.$validDirtyGrid;
            $scope.selectEditableAndButtonUserGridTableParams.reload();
            locationService.removeDirtyCheck($scope.dirtyCheckKey);
        };

        // Init variables
        $scope.aService = aService;
        $scope.aService2 = aService2;
        $scope.anotherService = anotherService;
        $scope.myOtherService = myOtherService;

        function getData($defer, params) {
            var $scope = params.settings().$scope.$parent.$parent,
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
                        var id = $scope.getIdForLine(result);
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
                            id = $scope.getIdForLine(result);
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
            $scope.reinitPage = $scope.selectEditableAndButtonUserGridTableParams.page(); // fix a bug occurring when backing to the grid from another screen: page is reinitialized to 1 by ng-tables
        }
        locationService.controllerInitialized('Select Editable And Button User Grid', $scope, ['selectEditableAndButtonUserGridTableParams', 'editedLinesById', '$edit', 'validDirtyLinesById', 'invalidDirtyLinesById', 'dirtyLinesById', '$dirtyGrid', '$validDirtyGrid', 'dirtyCheckKey', 'selectedLineId']);
    }])
;

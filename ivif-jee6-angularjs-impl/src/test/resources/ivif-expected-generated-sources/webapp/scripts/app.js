'use strict';

angular.module('test', [
        'ngRoute',
        'ngTable'
    ])
    .value('locationUtils', {
        fromObjectToSearch: function(object) {
            var search = {};
            angular.forEach(object, function(value, key) {
               search[key] = angular.toJson(value);
            });
            return search;
        },
        fromSearchToObject: function (search) {
            var object = {};
            angular.forEach(search, function(value, key) {
                object[key] = angular.fromJson(value);
            });
            return object;
        }
    })
    .factory('locationService', ['$location', function($location) {
        var newLocationToBePushed = false,
            locationContexts = [],
            locationLoading = false,
            locationService = {
                controllerInitialized: function(title, scope, scopeAttributesToSave) {
                    var locationContext = {
                        title: title,
                        scope: scope,
                        scopeAttributesToSave: scopeAttributesToSave,
                        path: $location.path(),
                        search: $location.search(),
                        savedScopeAttributes: null
                    };
                    if (!locationLoading) {
                        if (!newLocationToBePushed) {
                            locationContexts = []; // re-init location contexts because it's a new root
                        }
                        locationContexts.push(locationContext);
                    }
                    newLocationToBePushed = false;
                    locationLoading = false;
                },
                pushNewLocation: function(path, search) {
                    newLocationToBePushed = true;
                    // Save current attributes values
                    var locationContext = locationService.getCurrentLocationContext(),
                        savedScopeAttributes = {},
                        scope = locationContext.scope;
                    locationContext.savedScopeAttributes = savedScopeAttributes;
                    angular.forEach(locationContext.scopeAttributesToSave, function(scopeAttributeToSave) {
                        savedScopeAttributes[scopeAttributeToSave] = scope[scopeAttributeToSave];
                    });
                    // Go to asked new location
                    $location.path(path).search(search);
                },
                loadLocationContext: function(locationIndex) {
                    // Deleting previous location Context
                    for (var i = locationContexts.length - 1 ; i > locationIndex ; i--) {
                        locationContexts.pop();
                    }
                    var locationContext = locationService.getCurrentLocationContext();
                    locationLoading = true;
                    // Go to asked location
                    $location.path(locationContext.path).search(locationContext.search);
                },
                initializeController: function(scope) {
                    if (locationLoading) {
                        // we are loading a previous saved location context
                        // get the current location context in order to apply saved context values
                        var locationContext = locationService.getCurrentLocationContext(),
                            savedScopeAttributes = locationContext.savedScopeAttributes;
                        angular.forEach(locationContext.scopeAttributesToSave, function(scopeAttributeToSave) {
                            scope[scopeAttributeToSave] = savedScopeAttributes[scopeAttributeToSave];
                        });
                        return true; // controller initialized
                    } else {
                        return false; // controller must initialize
                    }
                },
                getCurrentLocationContext: function() {
                    return locationContexts.length > 0 ? locationContexts[locationContexts.length - 1] : null;
                },
                getLocationContexts: function() {
                    return locationContexts;
                }
            };
        return locationService;
    }])
    .controller('BreadcrumbsCtrl', ['$scope', 'locationService', function($scope, locationService) {
        $scope.locationService = locationService;
    }])
;
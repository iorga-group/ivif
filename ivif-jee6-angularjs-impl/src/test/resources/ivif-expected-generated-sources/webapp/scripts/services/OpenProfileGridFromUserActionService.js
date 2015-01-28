'use strict';

angular.module('test')
    .factory('openProfileGridFromUserAction', ['$location', 'locationUtils', 'locationService', function($location, locationUtils, locationService) {
        return function(parameters) {
            locationService.pushNewLocation('/profileGrid', locationUtils.fromObjectToSearch({openProfileGridFromUser: parameters}));
        }
    }]);
;


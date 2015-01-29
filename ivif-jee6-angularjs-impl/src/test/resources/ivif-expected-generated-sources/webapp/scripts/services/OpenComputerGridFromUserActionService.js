'use strict';

angular.module('test')
    .factory('openComputerGridFromUserAction', ['$location', 'locationUtils', 'locationService', function($location, locationUtils, locationService) {
        return function(parameters) {
            locationService.pushNewLocation('/computerGrid', locationUtils.fromObjectToSearch({openComputerGridFromUser: parameters}));
        }
    }]);
;


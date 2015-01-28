'use strict';

angular.module('test')
    .factory('openUserGridFromComputerAction', ['$location', 'locationUtils', 'locationService', function($location, locationUtils, locationService) {
        return function(parameters) {
            locationService.pushNewLocation('/userGrid', locationUtils.fromObjectToSearch({openUserGridFromComputer: parameters}));
        }
    }]);
;


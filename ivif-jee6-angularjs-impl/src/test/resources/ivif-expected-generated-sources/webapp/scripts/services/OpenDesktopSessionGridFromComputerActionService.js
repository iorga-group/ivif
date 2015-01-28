'use strict';

angular.module('test')
    .factory('openDesktopSessionGridFromComputerAction', ['$location', 'locationUtils', 'locationService', function($location, locationUtils, locationService) {
        return function(parameters) {
            locationService.pushNewLocation('/desktopSessionGrid', locationUtils.fromObjectToSearch({openDesktopSessionGridFromComputer: parameters}));
        }
    }]);
;


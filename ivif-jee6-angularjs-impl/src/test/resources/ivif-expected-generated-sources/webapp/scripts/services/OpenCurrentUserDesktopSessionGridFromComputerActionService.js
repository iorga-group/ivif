'use strict';

angular.module('test')
    .factory('openCurrentUserDesktopSessionGridFromComputerAction', ['$location', 'locationUtils', 'locationService', function($location, locationUtils, locationService) {
        return function(parameters) {
            locationService.pushNewLocation('/desktopSessionGrid', locationUtils.fromObjectToSearch({openCurrentUserDesktopSessionGridFromComputer: parameters}));
        }
    }])
;

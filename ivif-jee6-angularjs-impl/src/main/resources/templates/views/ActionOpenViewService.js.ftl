<#assign action=model.actionOpenView.element>
'use strict';

angular.module('sara')
    .factory('${action.name}Action', ['$location', 'locationUtils', 'locationService', function($location, locationUtils, locationService) {
        return function(parameters) {
            locationService.pushNewLocation('${model.gridPath}', locationUtils.fromObjectToSearch({${action.name}: parameters}));
        }
    }]);
;


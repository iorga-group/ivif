<#assign actionName=model.id>
'use strict';

angular.module('${model.configuration.angularModuleName}')
    .factory('${actionName}Action', ['$location', 'locationUtils', 'locationService', function($location, locationUtils, locationService) {
        return function(parameters) {
            locationService.pushNewLocation('${model.gridPath}', locationUtils.fromObjectToSearch({${actionName}: parameters}));
        }
    }]);
;


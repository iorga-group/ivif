<#assign action=model.actionOpenView.element>
'use strict';

angular.module('sara')
    .factory('${action.name}Action', ['$location', 'locationUtils', function($location, locationUtils) {
        return function(parameters) {
            $location.path('${model.gridPath}').search(locationUtils.fromObjectToSearch({${action.name}: parameters}));
        }
    }]);
;


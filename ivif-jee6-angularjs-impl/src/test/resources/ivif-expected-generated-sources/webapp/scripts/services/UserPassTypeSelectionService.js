'use strict';
angular.module('test')
    .factory('UserPassType', ['$q', function($q) {
        var selection = {
                NONE: {value: 1, title: 'NONE'},
                FULL: {value: 2, title: 'FULL'},
                LIMITED: {value: 3, title: 'LIMITED'}
            },
            optionsByName = {},
            optionList = [],
            titlesByValue = {};

        angular.forEach(selection, function(item, itemName) {
            var option = {id: item.value, title: item.title};
            optionsByName[itemName] = option;
            optionList.push(option);
            titlesByValue[item.value] = item.title;
        });

        return angular.extend({}, optionsByName, {
            optionsByName: optionsByName,
            optionList: optionList,
            deferOptionList: function() {
                var deferred = $q.defer();
                deferred.resolve(angular.copy(optionList));
                return deferred;
            },
            titlesByValue: titlesByValue
        });
    }])
    .run(['$rootScope', 'UserPassType', function ($rootScope, UserPassType) {
        $rootScope.UserPassType = UserPassType;
    }])
;

'use strict';
angular.module('test')
    .factory('UserStatusType', ['$q', function($q) {
        var selection = {
                ACTIVE: {value: 'ACTIVE', title: 'ACTIVE'},
                DISABLED: {value: 'DIS', title: 'DISABLED'},
                UNKNOWN: {value: '?', title: 'Not known'}
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
    .run(['$rootScope', 'UserStatusType', function ($rootScope, UserStatusType) {
        $rootScope.UserStatusType = UserStatusType;
    }])
;

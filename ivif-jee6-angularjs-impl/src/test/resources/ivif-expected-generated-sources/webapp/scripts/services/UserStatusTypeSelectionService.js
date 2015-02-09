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
            optionListDeferred = $q.defer(),
            titlesByValue = {};

        angular.forEach(selection, function(item, itemName) {
            var option = {id: item.value, title: item.title};
            optionsByName[itemName] = option;
            optionList.push(option);
            titlesByValue[item.value] = item.title;
        });

        optionListDeferred.resolve(optionList);
        return angular.extend({}, optionsByName, {
            optionsByName: optionsByName,
            optionList: optionList,
            optionListDeferred: optionListDeferred,
            titlesByValue: titlesByValue
        });
    }])
    .run(['$rootScope', 'UserStatusType', function ($rootScope, UserStatusType) {
        $rootScope.UserStatusType = UserStatusType;
    }])
;

'use strict';
<#assign selectionModel=model.selectionModel>
<#assign selection=selectionModel.element>
angular.module('${model.configuration.angularModuleName}')
    .factory('${selection.name}', ['$q', function($q) {
        var selection = {
<#list selectionModel.options as option>
                ${option.name}: {value: '${option.value}', title: '${option.title}'}<#if option_has_next>,</#if>
</#list>
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
    .run(['$rootScope', '${selection.name}', function ($rootScope, ${selection.name}) {
        $rootScope.${selection.name} = ${selection.name};
    }])
;

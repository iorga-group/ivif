<#include "utils.ftl">
<#macro fieldEditor model ivifType nbTabs editable editSwitch entityAttribute>
    <#switch ivifType>
        <#case "boolean">
<@tabulate nbTabs=nbTabs/><input type="checkbox" class="form-control" ng-model="${model}" ng-disabled="<#if editable>!(${editSwitch})<#else>true</#if>" />
            <#break>
        <#case "date">
<@tabulate nbTabs=nbTabs/>{{${model} | amDateFormat:'ll'}}
            <#break>
        <#case "datetime">
<@tabulate nbTabs=nbTabs/><span am-time-ago="${model}" bs-tooltip data-title="{{${model} | amDateFormat:'lll'}}"></span>
            <#break>
        <#case "enum">
            <#assign selectionName=entityAttribute.element.value.ref>
            <#if editable>
<@tabulate nbTabs=nbTabs/><span ng-if="!(${editSwitch})">{{${selectionName}.titlesByValue[${model}]}}</span>
<@tabulate nbTabs=nbTabs/><select ng-options="option.id as option.title for (name, option) in ${selectionName}.optionsByName" ng-model="${model}" ng-if="${editSwitch}" class="form-control"></select>
            <#else>
<@tabulate nbTabs=nbTabs/>{{${selectionName}.titlesByValue[${model}]}}
            </#if>
            <#break>
        <#default>
            <#if editable>
<@tabulate nbTabs=nbTabs/><span ng-if="!(${editSwitch})">{{${model}}}</span>
                <#if entityAttribute.element.value.defaultEditor?has_content && entityAttribute.element.value.defaultEditor.toString() == "TEXT_AREA">
<@tabulate nbTabs=nbTabs/><textarea ng-if="${editSwitch}" class="form-control" ng-model="${model}"/>
                <#else>
<@tabulate nbTabs=nbTabs/><input ng-if="${editSwitch}" type="text" class="form-control" ng-model="${model}"/>
                </#if>
            <#else>
<@tabulate nbTabs=nbTabs/>{{${model}}}
            </#if>
    </#switch>
</#macro>
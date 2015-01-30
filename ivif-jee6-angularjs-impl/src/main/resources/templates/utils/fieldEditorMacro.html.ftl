<#include "utils.ftl">
<#macro fieldEditor model ivifType nbTabs editable editSwitch>
    <#switch ivifType>
        <#case "boolean">
<@tabulate nbTabs=nbTabs/><input type="checkbox" class="form-control" ng-model="${model}" ng-disabled="<#if editable>!${editSwitch}<#else>true</#if>" />
            <#break>
        <#case "date">
<@tabulate nbTabs=nbTabs/>{{${model} | amDateFormat:'ll'}}
            <#break>
        <#case "datetime">
<@tabulate nbTabs=nbTabs/><span am-time-ago="${model}" tooltip="{{${model} | amDateFormat:'lll'}}"></span>
            <#break>
        <#default>
            <#if editable>
<@tabulate nbTabs=nbTabs/><span ng-if="!${editSwitch}">{{${model}}}</span>
<@tabulate nbTabs=nbTabs/><span ng-if="${editSwitch}"><input type="text" class="form-control" ng-model="${model}"/></span>
            <#else>
<@tabulate nbTabs=nbTabs/>{{${model}}}
            </#if>
    </#switch>
</#macro>
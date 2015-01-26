<#include "utils.ftl">
<#macro fieldEditor model fieldClassName nbTabs editable editSwitch>
    <#switch fieldClassName>
        <#case "java.lang.Boolean">
<@tabulate nbTabs=nbTabs/><input type="checkbox" class="form-control" ng-model="${model}" ng-disabled="<#if editable>!${editSwitch}<#else>true</#if>" />
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
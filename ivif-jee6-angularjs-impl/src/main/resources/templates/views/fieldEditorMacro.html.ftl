<#macro tabulate nbTabs>
    <#list 0..nbTabs as i>    </#list><#t>
</#macro>
<#macro fieldEditor model fieldClassName nbTabs editable editSwitch>
    <#switch fieldClassName>
        <#case "java.lang.Boolean">
<@tabulate nbTabs=nbTabs/><input type="checkbox" class="form-control" ng-model="${model}"<#if editable> ng-disabled="!${editSwitch}"</#if> />
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
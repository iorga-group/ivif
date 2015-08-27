<#include "utils.ftl">
<#macro fieldEditor fieldName ivifType nbTabs editable editSwitch entityAttribute requiredIf>
<#assign model="line."+fieldName/>
    <#switch ivifType>
        <#case "boolean">
<@tabulate nbTabs=nbTabs/><div class="checkbox"><label><input type="checkbox" ng-model="${model}" ng-disabled="<#if editable>!(${editSwitch})<#else>true</#if>"<#if editable> grid-line-field</#if> /></label></div>
            <#break>
        <#case "date">
<@tabulate nbTabs=nbTabs/>{{${model} | amDateFormat:'ll'}}
            <#break>
        <#case "datetime">
            <#-- TODO bring back this code using a "default-display" feature in entity datetime attributes
<@tabulate nbTabs=nbTabs/><span am-time-ago="${model}" bs-tooltip data-title="{{${model} | amDateFormat:'lll'}}"></span>
             -->
<@tabulate nbTabs=nbTabs/>{{${model} | amDateFormat:'lll'}}
            <#break>
        <#case "enum">
            <#assign selectionName=entityAttribute.element.value.ref>
            <#if editable>
<@tabulate nbTabs=nbTabs/><span ng-if="!(${editSwitch})">{{${selectionName}.titlesByValue[${model}]}}</span>
<@tabulate nbTabs=nbTabs/><select ng-options="option.id as option.title for (name, option) in ${selectionName}.optionsByName" ng-model="${model}" ng-if="${editSwitch}"<#if requiredIf?has_content> ng-required="${requiredIf}"</#if> grid-line-field class="form-control"></select>
            <#else>
<@tabulate nbTabs=nbTabs/>{{${selectionName}.titlesByValue[${model}]}}
            </#if>
            <#break>
        <#default>
            <#if editable>
<@tabulate nbTabs=nbTabs/><span ng-if="!(${editSwitch})">{{${model}}}</span>
                <#if entityAttribute.element.value.defaultEditor?has_content && entityAttribute.element.value.defaultEditor.toString() == "TEXT_AREA">
<@tabulate nbTabs=nbTabs/><textarea ng-if="${editSwitch}" class="form-control" ng-model="${model}"<#if requiredIf?has_content> ng-required="${requiredIf}"</#if> grid-line-field/>
                <#else>
<@tabulate nbTabs=nbTabs/><input ng-if="${editSwitch}" type="<#if ivifType == "long" || ivifType == "integer">number<#else>text</#if>" class="form-control" ng-model="${model}"<#if requiredIf?has_content> ng-required="${requiredIf}"</#if> grid-line-field/>
                </#if>
            <#else>
<@tabulate nbTabs=nbTabs/>{{${model}}}
            </#if>
    </#switch>
</#macro>
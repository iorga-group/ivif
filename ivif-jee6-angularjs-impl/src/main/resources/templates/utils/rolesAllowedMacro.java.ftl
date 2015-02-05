<#include "utils.ftl">
<#macro rolesAllowed rolesAllowed util nbTabs=0, inCurrentClass=true>
    <#if rolesAllowed?size &gt; 0>
        <#assign multipleRoles=rolesAllowed?size &gt; 1>
<@tabulate nbTabs=nbTabs/>@${util.useClass("com.iorga.ivif.ja.RolesAllowed", inCurrentClass)}(<#if multipleRoles>{</#if><#list rolesAllowed as roleAllowed>"${roleAllowed}"<#if roleAllowed_has_next>, </#if></#list><#if multipleRoles>}</#if>)
    </#if>
</#macro>
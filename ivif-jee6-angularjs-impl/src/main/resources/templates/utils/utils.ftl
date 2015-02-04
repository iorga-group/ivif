<#macro tabulate nbTabs>
    <#if nbTabs &gt; 0>
        <#list 1..nbTabs as i>    </#list><#t>
    </#if>
</#macro>
<#macro escapeJavaString str>
    ${str?replace("\"", "\\\"")}<#t>
</#macro>
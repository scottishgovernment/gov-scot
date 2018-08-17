<#-- Macro to take a document's file size in bytes and display it as bytes, kilobytes or megabytes -->

<#macro formatFileSize document>
    <#if document.size??>
        <#if document.size gte 1000000>
            <#assign size = (document.size/1000000)?string["0.0"] + ' MB'/>
        <#elseif document.size gte 1000>
            <#assign size = (document.size/1000)?string["0.0"] + ' kB'/>
        <#else>
            <#assign size = document.size?string + ' B'/>
        </#if>
    ${size}
    </#if>
</#macro>

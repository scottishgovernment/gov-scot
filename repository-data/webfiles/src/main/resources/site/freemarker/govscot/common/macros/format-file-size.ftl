<#-- Macro to take a document's file size in bytes and display it as bytes, kilobytes or megabytes -->

<#macro formatFileSize document>
    <#if document.document??>
        <#if document.document.length gte 1000000>
            <#assign size = (document.document.length/1000000)?string["0.0"] + ' MB'/>
        <#elseif document.document.length gte 1000>
            <#assign size = (document.document.length/1000)?string["0.0"] + ' kB'/>
        <#else>
            <#assign size = document.document.length?string + ' B'/>
        </#if>
    ${size}
    </#if>
</#macro>

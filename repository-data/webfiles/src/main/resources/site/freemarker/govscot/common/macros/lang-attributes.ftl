<#macro lang doc default="en">
    <#if doc.contentitemlanguage?? && doc.contentitemlanguage != default><#t>
        lang="${document.contentitemlanguage}"<#t>
    </#if><#t>
</#macro>

<#macro langcompare doc1 doc2 default="en"><#t>
    <#assign doc1lang = default /><#t>
    <#assign doc2lang = default /><#t>
    <#if doc1.contentitemlanguage??><#t>
        <#assign doc1lang = doc1.contentitemlanguage /><#t>
    </#if><#t>
    <#if doc2?? && doc2.contentitemlanguage??><#t>
        <#assign doc2lang = doc2.contentitemlanguage /><#t>
    </#if><#t>
<#t>
    <#if doc1lang != doc2lang><#t>
        lang="${doc1lang}"<#t>
    </#if><#t>
</#macro>

<#macro revertlang doc default="en">
    <#if doc.contentitemlanguage?? && doc.contentitemlanguage != default><#t>
        lang="${default}"<#t>
    </#if><#t>
</#macro>

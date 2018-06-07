<#if document??>
    <#assign documentUuid = document.getProperty('jcr:uuid')/>
<#else>
    <#assign documentUuid = "n/a"/>
</#if>
<input id="documentUuid" type="hidden" name="uuid" value="${documentUuid}"/>
<@hst.include ref="feedback"/>

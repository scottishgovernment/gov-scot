<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#if hstRequestContext.preview><div class="cms-display-grid"></#if>
<div class="ds_pb  ds_pb--divider">
    <#if fullwidth?has_content>
        <#if fullwidth>
    <hr />
        <#else>
    <div class="ds_wrapper">
        <hr />
    </div> 
        </#if>
    <#else>
    <div class="ds_wrapper">
        <hr />
    </div>
    </#if>
</div>
<#if hstRequestContext.preview></div></#if>

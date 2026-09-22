<#-- Macro to render an NPF indicator's status (improving/maintaining/worsening) as a design system status tag -->

<#macro indicatorStatusTag status>
    <#if status?has_content>
        <#assign statusTagClass = "ds_tag">
        <#assign statusTagLabel = status?cap_first>
        <#if status == "improving">
            <#assign statusTagClass = "ds_tag  ds_tag--green">
        <#elseif status == "worsening">
            <#assign statusTagClass = "ds_tag  ds_tag--pink">
        <#elseif status == "stable">
            <#assign statusTagClass = "ds_tag  ds_tag--grey">
            <#assign statusTagLabel = "Maintaining">
        </#if>
        <strong class="${statusTagClass}">${statusTagLabel}</strong>
    </#if>
</#macro>

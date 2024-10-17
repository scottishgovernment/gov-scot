<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Text" -->

<div class="ds_cb  ds_cb--text
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
    <#if neutrallinks>  ds_cb--neutral-links</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner <#if removebottompadding>  ds_!_padding-bottom--0</#if>">
            <#if document??>
                <div class="ds_cb__text  <#if position??>ds_cb__text--${position}</#if>">
                    <@hst.html hippohtml=document.content/>
                </div>

                <@hst.manageContent hippobean=document documentTemplateQuery="new-text-document" parameterName="document"/>
            <#elseif editMode>
                <div class="cms-blank  ds_cb__text  <#if position??>ds_cb__text--${position}</#if>">
                    <@placeholdertext lines=7/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document"/>
            </#if>
        </div>
    </div>
</div>

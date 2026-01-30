<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">

<div class="ds_cb  ds_cb--double-text
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
<#if neutrallinks>  ds_cb--neutral-links</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner <#if removebottompadding> ds_!_padding-bottom--0</#if>">

            <#if document1??>
                <div class="ds_cb__text">
                    <@hst.html hippohtml=document1.content/>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-text-document" parameterName="document1"/>
                </div>
            <#elseif editMode>
                <div class="ds_cb__text  cms-blank">
                    <@placeholdertext lines=7/>

                    <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1"/>
                </div>
            </#if>

            <#if document2??>
                <div class="ds_cb__text">
                    <@hst.html hippohtml=document2.content/>

                    <@hst.manageContent hippobean=document2 documentTemplateQuery="new-text-document" parameterName="document2"/>
                </div>
            <#elseif editMode>
                <div class="ds_cb__text  cms-blank">
                    <@placeholdertext lines=7/>

                    <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document2"/>
                </div>
            </#if>
        </div>
    </div>
</div>

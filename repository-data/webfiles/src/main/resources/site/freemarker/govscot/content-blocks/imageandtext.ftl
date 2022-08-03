<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Imageandtext" -->

<div class="ds_cb  ds_cb--image-text
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document??>
                <div class="ds_cb__text">
                    <@hst.html hippohtml=document.content/>
                </div>

                <div class="ds_cb__poster">
                    <img src="<@hst.link hippobean=document.image />" alt="${document.alt}"/>
                </div>

                <@hst.manageContent hippobean=document documentTemplateQuery="new-imageandtext-document" parameterName="document" rootPath="images"/>
            <#elseif editMode>
                <div class="ds_cb__text cms-blank">
                    <@placeholdertext lines=7/>
                </div>

                <div class="ds_cb__poster cms-blank">
                    <@placeholderimage/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-imageandtext-document" parameterName="document" rootPath="images"/>
            </#if>
        </div>
    </div>
</div>

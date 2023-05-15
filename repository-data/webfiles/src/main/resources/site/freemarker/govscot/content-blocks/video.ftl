<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Video" -->

<div class="ds_cb  ds_cb--video
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document??>
                <div class="ds_cb__poster">
                    <a target="_blank" class="ds_cb__poster-link" href="${document.url}">
                        <img class="ds_cb__poster-video" src="<@hst.link hippobean=document.image /> " alt="${document.alt}"/>
                    </a>
                </div>

                <div class="ds_cb__text">
                    <@hst.html hippohtml=document.content/>
                </div>

                <@hst.manageContent hippobean=document documentTemplateQuery="new-video-document" parameterName="document" rootPath="videos"/>
            <#elseif editMode>
                <div class="ds_cb__poster cms-blank">
                    <@placeholdervideo/>
                </div>

                <div class="ds_cb__text cms-blank">
                    <@placeholdertext lines=7/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-video-document" parameterName="document" rootPath="videos"/>
            </#if>
        </div>
    </div>
</div>

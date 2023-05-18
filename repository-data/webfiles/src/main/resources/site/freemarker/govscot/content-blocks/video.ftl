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
                    <a target="_blank" class="ds_cb__poster__link" href="${document.videoUrl}">
                        <#if document.image.xlargesixcolumns??>
                            <img alt="${document.alt}" src="<@hst.link hippobean=document.image.xlargesixcolumns />"
                                class="ds_cb__poster-video"
                                width="${document.image.xlargesixcolumns.width?c}"
                                height="${document.image.xlargesixcolumns.height?c}"
                                srcset="
                                <@hst.link hippobean=document.image.smallcolumns/> 360w,
                                <@hst.link hippobean=document.image.smallcolumnsdoubled/> 720w,
                                <@hst.link hippobean=document.image.mediumsixcolumns/> 352w,
                                <@hst.link hippobean=document.image.mediumsixcolumnsdoubled/> 704w,
                                <@hst.link hippobean=document.image.largesixcolumns/> 448w,
                                <@hst.link hippobean=document.image.largesixcolumnsdoubled/> 896w,
                                <@hst.link hippobean=document.image.xlargesixcolumns/> 544w,
                                <@hst.link hippobean=document.image.xlargesixcolumnsdoubled/> 1088w"
                                sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, 100vw"
                                >
                        <#else>
                            <img class="ds_cb__poster-video" src="<@hst.link hippobean=document.image />" alt="" width="${document.image.original.width?c}" height="${document.image.original.height?c}">
                        </#if>

                        <svg class="ds_cb__poster__overlay" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" viewBox="0 0 160 90">
                            <circle class="background" cx="82" cy="45.5" r="16" fill="#0065db"/>
                            <path class="foreground" fill="#fff" d="M76 45.3v-9.2l8 4.6 8 4.6-8 4.7-8 4.6z"/>
                        </svg>
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

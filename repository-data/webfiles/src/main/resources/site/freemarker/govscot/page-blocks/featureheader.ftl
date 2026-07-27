<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">

<#-- @ftlvariable name="document" type="scot.mygov.publishing.beans.Pageheading" -->
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#assign variables = hstRequestContext.getAttribute("variables")/>
<@hst.messagesReplace escapeMessageXml=false bundle=variables variablePrefix="[[" variableSuffix="]]">
<#if document?? || editMode>
<#assign constrain = true>
<#if fullwidth?has_content>
    <#if fullwidth>
        <#assign constrain = false>   
    </#if>
</#if>
<#if constrain>
<div class="ds_wrapper">
</#if>
    <header class="ds_pb  ds_pb--ds_feature-header  ds_feature-header
<#if backgroundcolor?has_content> 
<#switch backgroundcolor?lower_case> 
  <#case 'secondary'>
  ds_feature-header--background-secondary
  <#break>
  <#case 'tertiary'>
  ds_feature-header--background-tertiary
  <#break>
  <#case 'theme'>
  ds_pb__theme--background-secondary  ds_feature-header--background-secondary
  <#break>
  <#case 'theme reversed'>
  ds_feature-header--background
  <#break>
</#switch>
</#if>
<#if widetext?has_content><#if widetext>  ds_feature-header--wide</#if></#if>
<#if !constrain>  ds_feature-header--fullwidth</#if>
<#if verticalalign?has_content><#if verticalalign == 'top'>  ds_feature-header--top</#if></#if>">
        <#if !constrain>
        <div class="ds_wrapper">
        </#if>
        <#if document?has_content>
            <div class="ds_feature-header__primary">
                <h1 class="ds_feature-header__title">
                    ${document.title}
                </h1>
                <@hst.html hippohtml=document.description/>
                <#if document.cta?has_content>
                <#if document.link?has_content>
                <a href="<@hst.link hippobean=document.link/>" class="ds_button ds_button--has-icon">${document.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                    <use href="${iconspath}#chevron_right"></use>
                </svg></a>
                <#elseif document.externalLink?has_content>
                <a href="${document.externalLink}" class="ds_button ds_button--has-icon">${document.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                    <use href="${iconspath}#chevron_right"></use>
                </svg></a>
                </#if>
                </#if>
            </div>
            <#if document.image?has_content>
            <div class="ds_feature-header__secondary<#if imagenomargin>  ds_feature-header__secondary--no-padding</#if><#if imagecover?has_content><#if imagecover>  ds_feature-header__secondary--cover</#if></#if><#if imagealignmobile??>  ds_feature-header__secondary--${imagealignmobile}-mobile</#if>">
                <#if document.videoUrl?has_content>
                <a target="_blank" class="ds_feature-header__secondary-link" href="${document.videoUrl}">
                </#if>
                <picture>
                <#if imagealignmobile == "hidden">
                    <source media="(max-width: 767px)" sizes="1px" srcset="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 1w" />
                </#if>
                <#if document.image.xlargesixcolumns?has_content>
                    <img class="ds_feature-header__image" alt="${document.alt}" src="<@hst.link hippobean=document.image.xlargesixcolumns />"
                        <#if document.image.xlargesixcolumns.width gt 0>
                            width="${document.image.xlargesixcolumns.width?c}"
                        </#if>
                        <#if document.image.xlargesixcolumns.height gt 0>
                            height="${document.image.xlargesixcolumns.height?c}"
                        </#if>
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
                    <img class="ds_feature-header__image" src="<@hst.link hippobean=document.image />" alt="${document.alt}"
                        <#if document.image.original.width gt 0>
                            width="${document.image.original.width?c}"
                        </#if>
                        <#if document.image.original.height gt 0>
                            height="${document.image.original.height?c}"
                        </#if>
                    >
                </#if>
                </picture>
                <#if document.videoUrl?has_content>
                    <svg class="ds_feature-header__secondary-overlay" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" viewBox="0 0 160 90">
                        <circle class="ds_feature-header__secondary-overlay-background" cx="82" cy="45.5" r="16" fill="#0065db"/>
                        <path class="ds_feature-header__secondary-overlay-foreground" fill="#fff" d="M76 45.3v-9.2l8 4.6 8 4.6-8 4.7-8 4.6z"/>
                    </svg>
                </a>
                </#if>
            </div>
            </#if>
                
            <@hst.manageContent hippobean=document documentTemplateQuery="new-pageheading-document" parameterName="document" rootPath="pageheadings"/>

        <#elseif editMode>
            <div class="ds_feature-header__primary">
                <h1 class="ds_feature-header__title"><@placeholdertext lines=2/></h1>
                <@placeholdertext lines=4/>
            </div>

            <div class="ds_feature-header__secondary<#if imagenomargin>  ds_feature-header__secondary--no-padding</#if><#if imagecover?has_content><#if imagecover>  ds_feature-header__secondary--cover</#if></#if><#if imagealignmobile??>  ds_feature-header__secondary--${imagealignmobile}-mobile</#if>">
                <@placeholderimage/>
            </div>

            <@hst.manageContent documentTemplateQuery="new-pageheading-document" parameterName="document" rootPath="pageheadings"/>
        </#if>

        <#if !constrain>
        </div>
        </#if>
    </header>
<#if constrain>
</div>
</#if>

</#if>
</@hst.messagesReplace>

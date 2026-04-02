<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Text" -->
<div class="ds_pb  ds_pb--card-text
<#if removebottompadding>  ds_!_padding-bottom--0</#if>
<#if backgroundcolor?has_content>
<#switch backgroundcolor?lower_case>
  <#case 'secondary'>
  ds_pb--background-secondary
  <#break>
  <#case 'tertiary'>
  ds_pb--background-tertiary
  <#break>
  <#case 'theme'>
  ds_pb__theme--background-secondary
  <#break>
</#switch>
</#if>">
    <div class="ds_wrapper">
        <div class="ds_pb__inner">
            <#if document1??>
            <div class="ds_pb__text">
                <@hst.html hippohtml=document1.content/>
                <@hst.manageContent hippobean=document1 documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
            </div>
            <#elseif editMode>
            <div class="cms-blank  ds_pb__text">
                <@placeholdertext lines=7/>
                <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
            </div>
            </#if>
            <#if document2??>
            <div class="ds_pb__card">
                <div class="ds_card  <#if document2.link?? || document2.externalLink?has_content>ds_card--navigation  </#if> <#if !backgroundcolor?has_content>  ds_card--background-secondary</#if>">
                    <#if showimages>
                        <div class="ds_card__media">
                            <div class="ds_aspect-box">
                                <#if document2.image.xlargefourcolumns??>
                                    <img class="ds_aspect-box__inner" alt="${document2.alt}" src="<@hst.link hippobean=document2.image.xlargefourcolumns />"
                                            width="${document2.image.xlargefourcolumns.width?c}"
                                            height="${document2.image.xlargefourcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=document2.image.smallcolumns/> 360w,
                                            <@hst.link hippobean=document2.image.smallcolumnsdoubled/> 720w,
                                            <@hst.link hippobean=document2.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=document2.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=document2.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=document2.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=document2.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=document2.image.xlargefourcolumnsdoubled/> 704w"
                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, <#if smallvariant>360px<#else>100vw</#if>"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=document2.image />" alt="${document2.alt}"/>
                                </#if>
                            </div>
                        </div>
                    </#if>
                    <div class="ds_card__content">
                        <#if document2.title??>
                        <div class="ds_card__content-header">
                            <h2 class="ds_card__title">
                                <#if document2.link??>
                                    <a class="ds_card__link  ds_card__link--cover" href="<@hst.link hippobean=document2.link/>">${document2.title}</a>
                                <#elseif document2.externalLink?has_content>
                                    <a class="ds_card__link  ds_card__link--cover" href="${document2.externalLink}">${document2.title}</a>
                                <#else>
                                    ${document2.title}
                                </#if>
                            </h2>
                        </div>
                        </#if>
                        <#if document2.text??>
                        <div class="ds_card__content-main">
                            <p>${document2.text}</p>
                        </div>
                        </#if>
                    </div>

                    <@hst.manageContent hippobean=document2 documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="navigationcards"/>
                </div>
            </div>
            <#elseif editMode>
            <div class="ds_pb__card">
                <div class="ds_card  cms-blank  ds_card--background-secondary">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <@placeholderimage/>
                        </div>
                    </#if>
                    <div class="ds_card__content">
                        <div class="ds_card__content-header">
                            <h2 class="ds_card__title">
                                <a><@placeholdertext lines=2/></a>
                            </h2>
                        </div>
                        <div class="ds_card__content-main">
                            <p><@placeholdertext lines=4/></p>
                        </div>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="navigationcards"/>
                </div>
            </div>
            </#if>
        </div>
    </div>
</div>

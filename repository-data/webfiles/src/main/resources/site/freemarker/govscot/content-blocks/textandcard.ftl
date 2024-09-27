<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Text" -->
<div class="ds_cb  ds_cb--card-text
<#if neutrallinks>  ds_cb--neutral-links</#if>
<#if removebottompadding>  ds_!_padding-bottom--0</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document1??>
            <div class="ds_cb__text">
                <@hst.html hippohtml=document1.content/>
                <@hst.manageContent hippobean=document1 documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
            </div>
            <#elseif editMode>
            <div class="cms-blank  ds_cb__text">
                <@placeholdertext lines=7/>
                <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1" rootPath="text"/>
            </div>
            </#if>
            <#if document2??>
            <div class="ds_cb__card">
                <div class="ds_card  <#if document2.link?? || document2.externalLink?has_content>ds_card--hover  </#if>ds_card--grey">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
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
                        <h2 class="ds_card__title">
                            <#if document2.link??>
                                <a class="ds_card__link--cover" href="<@hst.link hippobean=document2.link/>">${document2.title}</a>
                            <#elseif document2.externalLink?has_content>
                                <a class="ds_card__link--cover" href="${document2.externalLink}">${document2.title}</a>
                            <#else>
                                ${document2.title}
                            </#if>
                        </h2>
                        </#if>
                        <#if document2.text??>
                        <p>${document2.text}</p>
                        </#if>
                    </div>

                    <@hst.manageContent hippobean=document2 documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="navigationcards"/>
                </div>
            </div>
            <#elseif editMode>
            <div class="ds_cb__card">
                <div class="ds_card  cms-blank  ds_card--grey">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <@placeholderimage/>
                        </div>
                    </#if>
                    <div class="ds_card__content">
                        <h2 class="ds_card__title">
                            <a><@placeholdertext lines=2/></a>
                        </h2>
                        <p><@placeholdertext lines=4/></p>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-navigationcard-document" parameterName="document2" rootPath="navigationcards"/>
                </div>
            </div>
            </#if>
        </div>
    </div>
</div>
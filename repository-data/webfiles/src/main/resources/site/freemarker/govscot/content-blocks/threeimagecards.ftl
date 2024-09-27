<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">

<#-- @ftlvariable name="card" type="scot.gov.www.beans.NavigationCard" -->
<#-- @ftlvariable name="document1" type="scot.gov.www.beans.NavigationCard" -->
<#-- @ftlvariable name="document2" type="scot.gov.www.beans.NavigationCard" -->
<#-- @ftlvariable name="document3" type="scot.gov.www.beans.NavigationCard" -->

<#assign cards = []>
<#if document1??>
    <#assign cards = cards + [document1]>
<#elseif editMode>
    <#assign cards = cards + ['']>
</#if>
<#if document2??>
    <#assign cards = cards + [document2]>
<#elseif editMode>
    <#assign cards = cards + ['']>
</#if>
<#if document3??>
    <#assign cards = cards + [document3]>
<#elseif editMode>
    <#assign cards = cards + ['']>
</#if>

<#if cards?size != 0>
<div class="ds_cb  ds_cb--cards  <#if !greycards>ds_cb--bg-grey</#if>  <#if fullwidth>ds_cb--fullwidth</#if>  <#if neutrallinks>ds_cb--neutral-links</#if>">
    <div class="ds_wrapper">
        <div class="ds_cb__inner <#if removebottompadding> ds_!_padding-bottom--0</#if>">

        <#list cards as card>

            <#if card != ''>
                <div class="ds_card  ds_card--hover  <#if greycards>ds_card--grey</#if>">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <div class="ds_aspect-box">
                                <#if card.image.xlargefourcolumns??>
                                    <img class="ds_aspect-box__inner" alt="${card.alt}" src="<@hst.link hippobean=card.image.xlargefourcolumns />"
                                            width="${card.image.xlargefourcolumns.width?c}"
                                            height="${card.image.xlargefourcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=card.image.smallcolumns/> 360w,
                                            <@hst.link hippobean=card.image.smallcolumnsdoubled/> 720w,
                                            <@hst.link hippobean=card.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=card.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=card.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=card.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=card.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=card.image.xlargefourcolumnsdoubled/> 704w"
                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, <#if smallvariant>360px<#else>100vw</#if>"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=card.image />" alt="${card.alt}"/>
                                </#if>
                            </div>
                        </div>
                    </#if>
                    <div class="ds_card__content">
                        <#if card.title??>
                        <h2 class="ds_card__title">
                            <#if card.link??>
                                <a class="ds_card__link--cover" href="<@hst.link hippobean=card.link/>">${card.title}</a>
                            <#elseif card.externalLink?has_content>
                                <a class="ds_card__link--cover" href="${card.externalLink}">${card.title}</a>
                            <#else>
                                ${card.title}
                            </#if>
                        </h2>
                        </#if>
                        <#if card.text??>
                        <p>${card.text}</p>
                        </#if>
                    </div>

                    <@hst.manageContent hippobean=card documentTemplateQuery="new-navigationcardcontentblock-document" parameterName="document${card?index+1}"/>
                </div>
            <#elseif editMode>
                <div class="ds_card  cms-blank">
                    <#if showimages>
                        <div class="ds_card__media  <#if smallvariant>ds_card__media--small-mobile</#if>">
                            <@placeholderimage/>
                        </div>
                    </#if>
                    <div class="ds_card__content  ds_category-item">
                        <h2 class="ds_category-item__title">
                            <a><@placeholdertext lines=2/></a>
                        </h2>

                        <div class="ds_category-item__summary">
                            <@placeholdertext lines=4/>
                        </div>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-navigationcardcontentblock-document" parameterName="document${card?index+1}"/>
                </div>
            </#if>

        </#list>
        </div>
    </div>
</div>
</#if>

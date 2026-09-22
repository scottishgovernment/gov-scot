<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<main id="main-content">
<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="category-upper">
        <header class="ds_feature-header  ds_feature-header--background  ds_feature-header--fullwidth  ds_!_margin-bottom--0">
            <div class="ds_wrapper">
                <div class="ds_feature-header__primary">
                    <h1 class="ds_feature-header__title">${document.title}</h1>
                    <#if document.summary?has_content>
                        <p class="ds_leader">${document.summary}</p>
                    </#if>
                    <#if document.dashboardUrl?has_content>
                        <a href="${document.dashboardUrl}" class="ds_button  ds_button--has-icon">Data dashboard<svg class="ds_icon" aria-hidden="true" role="img">
                            <use href="${iconspath}#chevron_right"></use>
                        </svg></a>
                    </#if>
                </div>
                <#if document.image??>
                <div class="ds_feature-header__secondary">
                    <#if document.image.xlargefourcolumns??>
                        <img class="ds_feature-header__image" alt="${document.alt!""}"
                            width="${document.image.xlargesixcolumns.width?c}"
                            height="${document.image.xlargesixcolumns.height?c}"
                            loading="lazy"
                            src="<@hst.link hippobean=document.image.xlargefourcolumns/>"
                            srcset="
                                <@hst.link hippobean=document.image.smallcolumns/> 360w,
                                <@hst.link hippobean=document.image.smallcolumnsdoubled/> 720w,
                                <@hst.link hippobean=document.image.mediumsixcolumns/> 352w,
                                <@hst.link hippobean=document.image.mediumsixcolumnsdoubled/> 704w,
                                <@hst.link hippobean=document.image.largesixcolumns/> 448w,
                                <@hst.link hippobean=document.image.largesixcolumnsdoubled/> 896w,
                                <@hst.link hippobean=document.image.xlargesixcolumns/> 544w,
                                <@hst.link hippobean=document.image.xlargesixcolumnsdoubled/> 1088w"
                                sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, 100vw">
                    <#else>
                        <img class="ds_feature-header__image" loading="lazy" alt="${document.alt!""}" src="<@hst.link hippobean=document.image/>"/>
                    </#if>
                </div>
                </#if>
            </div>
        </header>
    </div>
    <div class="category-lower  ds_pre-footer-background">
        <div class="ds_wrapper">
            <div class="ds_layout  ds_layout--category-list">
                <div class="ds_layout__grid">

                    <#if document.content??>
                    <div class="ds_pb  ds_pb--text">
                        <div class="ds_pb__inner  ds_!_padding-bottom--0">
                            <div class="ds_pb__text">
                                <@hst.html hippohtml=document.content/>
                            </div>
                        </div>
                    </div>
                    </#if>

                    <#if outcomes?has_content>
                        <!--noindex-->
                        <div class="ds_card-grid-container">
                            <div class="ds_card-grid  ds_card-grid--min-height  ds_card-grid--narrow  ds_card-grid--medium-2">
                                <#list outcomes as outcome>
                                    <div class="ds_card  ds_card--navigation">
                                        <#if outcome.image??>
                                            <div class="ds_card__media">
                                                <div class="ds_aspect-box">
                                                    <#if outcome.image.xlargefourcolumns??>
                                                        <img class="ds_aspect-box__inner" alt="${outcome.alt!""}" src="<@hst.link hippobean=outcome.image.xlargefourcolumns/>"
                                                            width="${outcome.image.xlargefourcolumns.width?c}"
                                                            height="${outcome.image.xlargefourcolumns.height?c}"
                                                            loading="lazy"
                                                            srcset="
                                                            <@hst.link hippobean=outcome.image.smallcolumns/> 360w,
                                                            <@hst.link hippobean=outcome.image.smallcolumnsdoubled/> 720w,
                                                            <@hst.link hippobean=outcome.image.mediumfourcolumns/> 224w,
                                                            <@hst.link hippobean=outcome.image.mediumfourcolumnsdoubled/> 448w,
                                                            <@hst.link hippobean=outcome.image.largefourcolumns/> 288w,
                                                            <@hst.link hippobean=outcome.image.largefourcolumnsdoubled/> 576w,
                                                            <@hst.link hippobean=outcome.image.xlargefourcolumns/> 352w,
                                                            <@hst.link hippobean=outcome.image.xlargefourcolumnsdoubled/> 704w"
                                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 100vw">
                                                    <#else>
                                                        <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=outcome.image/>" alt="${outcome.alt!""}"/>
                                                    </#if>
                                                </div>
                                            </div>
                                        </#if>
                                        <div class="ds_card__content">
                                            <div class="ds_card__content-header">
                                                <h2 class="ds_card__title">
                                                    <#assign outcomeHref><@hst.link path="/npf/${outcome.parentBean.name}/"/></#assign>
                                                    <a class="ds_card__link  ds_card__link--cover" href="${outcomeHref}">${outcome.title}</a>
                                                </h2>
                                            </div>
                                            <#if outcome.summary?has_content>
                                            <div class="ds_card__content-main">${outcome.summary}</div>
                                            </#if>
                                            <div class="ds_card__content-footer">
                                                <#assign statusCounts = (outcomeStatusCounts[outcome.parentBean.name])!{}>
                                                <#if statusCounts?has_content>
                                                    <div class="gov_number-status-tag__grid">
                                                        <#list ["improving", "stable", "worsening"] as status>
                                                            <#assign statusLabel = (status == "stable")?then("maintaining", status)>
                                                            <div class="gov_number-status-tag">
                                                                <span class="ds_tag  ds_tag--<#if status == "improving">green<#elseif status == "worsening">orange<#else>grey</#if>">${(statusCounts[status])!0}</span>
                                                                <span class="gov_number-status-tag__label">${statusLabel?cap_first}</span>
                                                            </div>
                                                        </#list>
                                                    </div>
                                                </#if>
                                            </div>
                                        </div>
                                    </div>
                                </#list>
                            </div>
                        </div>
                        <!--endnoindex-->
                    </#if>

                    <@hst.html hippohtml=document.epilogue var="epilogue"/>
                    <#if epilogue?has_content>
                    <div class="ds_pb  ds_pb--text" id="epilogue">
                        <div class="ds_pb__inner  ds_!_padding-bottom--0">
                            <div class="ds_pb__text">
                            ${epilogue?no_esc}
                            </div>
                        </div>
                    </div>
                    </#if>
                </div>

                <div class="ds_layout__feedback">
                    <#include '../common/feedback-wrapper.ftl'>
                </div>
            </div>
        </div>
    </div>
</#if>
</main>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="National Performance Framework"/>
    </@hst.headContribution>

    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=lastUpdated.time type="both" pattern="yyyy-MM-dd HH:mm"/>"/>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <#include "../common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "../common/canonical.ftl" />
    <#include "../common/gtm-datalayer.ftl"/>
</#if>

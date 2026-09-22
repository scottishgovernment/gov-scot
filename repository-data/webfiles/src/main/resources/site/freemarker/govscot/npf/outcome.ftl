<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/indicator-status-tag.ftl">
<main id="main-content">
<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="category-upper">
        <div class="ds_wrapper">
            <header class="ds_feature-header">
                <div class="ds_feature-header__primary">
                    <span class="ds_content-label">National Outcome</span>
                    <h1 class="ds_feature-header__title">${document.title}</h1>
                    <#if document.summary?has_content>
                        <p class="ds_leader">${document.summary}</p>
                    </#if>
                </div>

                <#if document.image??>
                <div class="ds_feature-header__secondary">
                    <#if document.image.xlargefourcolumns?has_content>
                        <img class="ds_feature-header__image" alt="${document.alt!""}"
                            <#if document.image.xlargefourcolumns.width gt 0>width="${document.image.xlargefourcolumns.width?c}"</#if>
                            <#if document.image.xlargefourcolumns.height gt 0>height="${document.image.xlargefourcolumns.height?c}"</#if>
                            loading="lazy"
                            src="<@hst.link hippobean=document.image.xlargefourcolumns/>"
                            srcset="
                                <@hst.link hippobean=document.image.smallcolumns/> 360w,
                                <@hst.link hippobean=document.image.smallcolumnsdoubled/> 720w,
                                <@hst.link hippobean=document.image.mediumfourcolumns/> 224w,
                                <@hst.link hippobean=document.image.mediumfourcolumnsdoubled/> 448w,
                                <@hst.link hippobean=document.image.largefourcolumns/> 288w,
                                <@hst.link hippobean=document.image.largefourcolumnsdoubled/> 576w,
                                <@hst.link hippobean=document.image.xlargefourcolumns/> 352w,
                                <@hst.link hippobean=document.image.xlargefourcolumnsdoubled/> 704w"
                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 100vw">
                    <#else>
                        <img class="ds_feature-header__image" loading="lazy" alt="${document.alt!""}" src="<@hst.link hippobean=document.image.original/>"
                            <#if document.image.original.width gt 0>width="${document.image.original.width?c}"</#if>
                            <#if document.image.original.height gt 0>height="${document.image.original.height?c}"</#if>
                        />
                    </#if>
                </div>
                </#if>
            </header>
        </div>
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

                    <#if indicators?has_content>
                    <!--noindex-->
                    <div class="ds_card-grid-container">
                        <div class="ds_card-grid  ds_card-grid--min-height  ds_card-grid--narrow  ds_card-grid--medium-2">
                            <#list indicators as indicator>
                                <div class="ds_card  ds_card--navigation">
                                    <div class="ds_card__content">
                                        <div class="ds_card__content-header">
                                            <@indicatorStatusTag status=indicator.status/>
                                            <h2 class="ds_card__title">
                                                <#assign indicatorHref><@hst.link path="/npf/${document.parentBean.name}/${indicator.name}/"/></#assign>
                                                <a class="ds_card__link  ds_card__link--cover" href="${indicatorHref}">${indicator.title}</a>
                                            </h2>
                                        </div>
                                        <#if indicator.summary?has_content>
                                        <div class="ds_card__content-main">${indicator.summary}</div>
                                        </#if>
                                    </div>
                                </div>
                            </#list>
                        </div>
                    </div>
                    <!--endnoindex-->
                    </#if>

                    <@hst.html hippohtml=document.epilogue var="epilogue"/>
                    <#if epilogue?has_content>
                    <div id="epilogue">
                        ${epilogue?no_esc}
                    </div>
                    </#if>
                </div>

                <div class="ds_layout__feedback">
                    <#include '../common/feedback-wrapper.ftl'>
                </div>
            </div>
        </div>
</#if>
</main>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title} - National Performance Framework - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>

    <#if parent??>
        <@hst.headContribution category="dcMeta">
        <meta name="dc.title.series" content="${parent.title}"/>
        </@hst.headContribution>
        <@hst.headContribution category="dcMeta">
        <meta name="dc.title.series.link" content="<@hst.link hippobean=parent/>"/>
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="National Outcome"/>
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

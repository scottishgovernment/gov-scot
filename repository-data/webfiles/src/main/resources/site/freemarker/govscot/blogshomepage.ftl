<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#if document??>
    <@hst.manageContent hippobean=document/>
</#if>

<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  ds_layout--full-width">
        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title"><#if document??>${document.title}<#else>Blogs</#if></h1>
            </header>
        </div>

        <div class="ds_layout__content">
            <#if document?? && document.summary?has_content>
                <div class="ds_leader-first-paragraph">
                    <p>${document.summary}</p>
                </div>
            </#if>

            <#if document??>
                <@hst.html hippohtml=document.content/>
            </#if>

            <#if featuredPosts?? && featuredPosts?has_content>
                <h2>Featured Posts</h2>
                <div class="ds_card-grid  ds_card-grid--min-height  ds_card-grid--narrow  ds_card-grid--medium-2">
                    <#list featuredPosts as post>
                        <div class="ds_card  ds_card--navigation">
                            <#if post.heroImage??>
                                <div class="ds_card__media">
                                    <div class="ds_aspect-box">
                                        <#if post.heroImage.xlargefourcolumns??>
                                            <img class="ds_aspect-box__inner" alt="${post.heroImageAlt!""}" src="<@hst.link hippobean=post.heroImage.xlargefourcolumns/>"
                                                width="${post.heroImage.xlargefourcolumns.width?c}"
                                                height="${post.heroImage.xlargefourcolumns.height?c}"
                                                loading="lazy"
                                                srcset="
                                                <@hst.link hippobean=post.heroImage.smallcolumns/> 360w,
                                                <@hst.link hippobean=post.heroImage.smallcolumnsdoubled/> 720w,
                                                <@hst.link hippobean=post.heroImage.mediumfourcolumns/> 224w,
                                                <@hst.link hippobean=post.heroImage.mediumfourcolumnsdoubled/> 448w,
                                                <@hst.link hippobean=post.heroImage.largefourcolumns/> 288w,
                                                <@hst.link hippobean=post.heroImage.largefourcolumnsdoubled/> 576w,
                                                <@hst.link hippobean=post.heroImage.xlargefourcolumns/> 352w,
                                                <@hst.link hippobean=post.heroImage.xlargefourcolumnsdoubled/> 704w"
                                                sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 100vw">
                                        <#else>
                                            <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=post.heroImage.largefourcolumns/>" alt="${post.heroImageAlt!""}"/>
                                        </#if>
                                    </div>
                                </div>
                            </#if>
                            <div class="ds_card__content">
                                <div class="ds_card__content-header">
                                    <h3 class="ds_card__title">
                                        <#assign postHref><@hst.link hippobean=post/></#assign>
                                        <a class="ds_card__link  ds_card__link--cover" href="${postHref}">${post.title}</a>
                                    </h3>
                                </div>
                                <#if post.summary?has_content>
                                    <div class="ds_card__content-main">${post.summary}</div>
                                </#if>
                                <div class="ds_card__content-footer">
                                    <dl class="ds_metadata">
                                        <#if post.author??>
                                            <div class="ds_metadata__item">
                                                <dt class="ds_metadata__key">Author</dt>
                                                <dd class="ds_metadata__value">${post.author.title}</dd>
                                            </div>
                                        </#if>
                                        <#if post.publicationDate??>
                                            <div class="ds_metadata__item">
                                                <dt class="ds_metadata__key">Published</dt>
                                                <dd class="ds_metadata__value"><@fmt.formatDate value=post.publicationDate.time type="both" pattern="dd MMMM yyyy HH:mm"/></dd>
                                            </div>
                                        </#if>
                                    </dl>
                                </div>
                            </div>
                        </div>
                    </#list>
                </div>
            </#if>

            <#include "common/a-z-list.ftl">
        </div>
    </main>
</div>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>

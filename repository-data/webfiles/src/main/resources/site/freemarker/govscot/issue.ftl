<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <input id="topicName" type="hidden" value="${document.title}"/>
        <main id="main-content" class="ds_layout  gov_layout--issue">
            <div class="ds_layout__banner">
                <header class="ds_feature-header  ds_feature-header--full-image  ds_feature-header--issue" id="page-content">
                    <#if document.image??>
                        <div class="ds_feature-header__secondary">
                            <img class="ds_feature-header__image" alt="" aria-hidden="true"
                                width="${document.image.xlargetwelvecolumnsdoubledfourone.width?c}"
                                height="${document.image.xlargetwelvecolumnsdoubledfourone.height?c}"
                                loading="lazy"
                                src="<@hst.link hippobean=document.image.xlargetwelvecolumnsfourone/>"
                                srcset="<@hst.link hippobean=document.image.smallfullfourone/> 448w,
                                    <@hst.link hippobean=document.image.smallfulldoubledfourone/> 896w,
                                    <@hst.link hippobean=document.image.mediumtwelvecolumnsfourone/> 736w,
                                    <@hst.link hippobean=document.image.mediumtwelvecolumnsdoubledfourone/> 1472w,
                                    <@hst.link hippobean=document.image.largetwelvecolumnsfourone/> 928w,
                                    <@hst.link hippobean=document.image.largetwelvecolumnsdoubledfourone/> 1856w,
                                    <@hst.link hippobean=document.image.xlargetwelvecolumnsfourone/> 1120w,
                                    <@hst.link hippobean=document.image.xlargetwelvecolumnsdoubledfourone/> 2240w"
                                sizes="(min-width:1200px) 1120px, (min-width:992px) 928px, (min-width:768px) 763px, 448px" />
                        </div>
                    </#if>

                    <div class="ds_feature-header__primary">
                        <h1 class="ds_feature-header__title">${document.title}</h1>
                    </div>
                </header>
            </div>

            <div class="ds_layout__content">
                <div id="introduction">
                    <@hst.html hippohtml=document.content/>
                </div>

                <div id="overview">
                    <@hst.html hippohtml=document.overview/>
                </div>
            </div>

            <div class="ds_layout__sidebar-B">
                <#if document.featureDateTitle?has_content ||
                    document.featureDate?has_content ||
                    document.featureDateSummary?has_content >
                    <aside class="gov_issue-callout">
                        <div class="gov_issue-callout__inner">
                            <#if document.featureDateTitle?has_content><h3 class="gov_issue-callout__beta">${document.featureDateTitle}</h3></#if>
                            <#if document.featureDate?has_content><div class="gov_issue-callout__alpha"><@fmt.formatDate value=document.featureDate.time type="both" pattern="d MMMM yyyy"/></div></#if>
                            <#if document.featureDateSummary?has_content><div class="gov_issue-callout__gamma">${document.featureDateSummary}</div></#if>
                        </div>
                    </aside>
                </#if>
            </div>

            <div class="ds_layout__sidebar">
                <#if document.featuredItems?has_content>
                    <section class="gov_content-block" id="featured-items">

                        <h2 class="gamma  visually-hidden"><#if document.featuredItemsTitle?has_content>${document.featuredItemsTitle}<#else>Featured</#if></h2>

                        <ul class="ds_no-bullets">
                            <#list document.featuredItems as item>
                                <li class="gov_featured-item  gov_featured-item--sidebar">
                                    <article>
                                        <dl class="ds_metadata  gov_featured-item__metadata">
                                            <#if item.label??>
                                                <div class="ds_metadata__item">
                                                    <dt class="ds_metadata__key  visually-hidden">Type</dt>
                                                    <dd class="ds_metadata__value  ds_content-label">${item.label}</dd>
                                                </div>
                                            </#if>

                                            <#if item.label == 'news'>
                                                <div class="ds_metadata__item">
                                                    <dt class="ds_metadata__key  visually-hidden">Date</dt>
                                                    <dd class="ds_metadata__value"><@fmt.formatDate value=item.publicationDate.time type="both" pattern="d MMMM yyyy HH:mm"/></dd>
                                                </div>
                                            <#elseif item.class == 'scot.gov.www.beans.ExternalLink'>
                                                <div class="ds_metadata__item">
                                                    <dt class="ds_metadata__key  visually-hidden">Link</dt>
                                                    <dd class="ds_metadata__value">${document.url}</dd>
                                                </div>
                                            <#elseif item.publicationDate??>
                                                <div class="ds_metadata__item">
                                                    <dt class="ds_metadata__key  visually-hidden">Publication date</dt>
                                                    <dd class="ds_metadata__value"><@fmt.formatDate value=item.displayDate.time type="both" pattern="d MMMM yyyy"/></dd>
                                                </div>
                                            </#if>
                                        </dl>

                                        <h3 class="gov_featured-item__title  ds_no-margin--bottom">
                                            <a class="gov_featured-item__link" data-navigation="featured-${item?index + 1}" href="<#if item.class == 'scot.gov.www.beans.ExternalLink'>${item.url}<#else><@hst.link hippobean=item/></#if>">
                                                ${item.title}
                                            </a>
                                        </h3>
                                    </article>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </#if>

                <#if news?has_content>
                    <section class="gov_content-block" id="related-news">
                        <h2 class="gov_content-block__title  gamma">News</h2>
                        <ul class="ds_no-bullets">
                            <#list news as newsItem>
                                <li><a data-navigation="news-${newsItem?index + 1}" href="<@hst.link hippobean=newsItem/>">${newsItem.title}</a></li>
                            </#list>
                        </ul>

                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/news/?topics=' + document.title/>" data-navigation="news-all">
                            <span class="gov_icon-link__text">See all news <span class="visually-hidden">about ${document.title}</span></span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </section>
                </#if>

                <#if policies?has_content>
                    <section class="gov_content-block" id="related-policies">
                        <h2 class="gov_content-block__title  gamma">Policies</h2>
                        <ul class="ds_no-bullets">
                            <#list policies as policy>
                                <li><a data-navigation="policy-${policy?index + 1}" href="<@hst.link hippobean=policy/>">${policy.title}</a></li>
                            </#list>
                        </ul>

                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/policies/?topics=' + document.title/>" data-navigation="policies-all">
                            <span class="gov_icon-link__text">See all policies <span class="visually-hidden">about ${document.title}</span></span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </section>
                </#if>

                <#if publications?has_content>
                    <section class="gov_content-block" id="related-publications">
                        <h2 class="gov_content-block__title  gamma">Publications</h2>
                        <ul class="ds_no-bullets">
                            <#list publications as publication>
                                <li><a data-navigation="publications-${publication?index + 1}" href="<@hst.link hippobean=publication/>">${publication.title}</a></li>
                            </#list>
                        </ul>

                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/?topics=' + document.title/>" data-navigation="publications-all">
                            <span class="gov_icon-link__text">See all publications <span class="visually-hidden">about ${document.title}</span></span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </section>
                </#if>
            </div>

            <div class="ds_layout__feedback">
                <#include 'common/feedback-wrapper.ftl'>
            </div>
        </main>
    </div>
</#if>

<#if document??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <#if document.tags??>
        <@hst.headContribution category="dcMeta">
            <meta name="dc.subject" content="<#list document.tags as tag>${tag}<#sep>, </#sep></#list>"/>
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Issue"/>
    </@hst.headContribution>

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

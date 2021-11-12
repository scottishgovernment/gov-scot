<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <input id="topicName" type="hidden" value="${document.title}"/>
        <main id="main-content" class="ds_layout  gov_layout--issue">
            <div class="ds_layout__banner">
                <header class="gov_feature-header  gov_feature-header--issue" id="page-content">
                    <#if document.image??>
                        <div class="gov_feature-header__media  gov_feature-header__media--full">
                            <img alt="" aria-hidden="true" src="<@hst.link hippobean=document.image.bannerdesktop/>" />
                        </div>
                    </#if>

                    <div class="gov_feature-header__content">
                        <h1 class="gov_feature-header__title">${document.title}</h1>
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
                            <#if document.featureDate?has_content><div class="gov_issue-callout__alpha"><@fmt.formatDate value=document.featureDate.time type="both" pattern="d MMM yyyy"/></div></#if>
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
                                                    <dd class="ds_metadata__value"><@fmt.formatDate value=item.publicationDate.time type="both" pattern="d MMM yyyy HH:mm"/></dd>
                                                </div>
                                            <#elseif item.class == 'scot.gov.www.beans.ExternalLink'>
                                                <div class="ds_metadata__item">
                                                    <dt class="ds_metadata__key  visually-hidden">Link</dt>
                                                    <dd class="ds_metadata__value">${document.url}</dd>
                                                </div>
                                            <#elseif item.publicationDate??>
                                                <div class="ds_metadata__item">
                                                    <dt class="ds_metadata__key  visually-hidden">Publication date</dt>
                                                    <dd class="ds_metadata__value"><@fmt.formatDate value=item.publicationDate.time type="both" pattern="d MMM yyyy"/></dd>
                                                </div>
                                            </#if>
                                        </dl>

                                        <h3 class="gov_featured-item__title  ds_no-margin--bottom">
                                            <a class="gov_featured-item__link" data-gtm="featured-item-${item?index + 1}" href="<#if item.class == 'scot.gov.www.beans.ExternalLink'>${item.url}<#else><@hst.link hippobean=item/></#if>">
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
                                <li><a data-gtm="news-${newsItem?index + 1}" href="<@hst.link hippobean=newsItem/>">${newsItem.title}</a></li>
                            </#list>
                        </ul>

                        <a href="<@hst.link path='/news/?topics=' + document.title/>" class="gov_icon-link">
                            <svg class="ds_icon  ds_icon--24" aria-hidden="true" role="img">
                                <use xlink:href="${iconspath}#list"></use>
                            </svg>
                            See all news <span class="visually-hidden">about ${document.title}</span>
                        </a>
                    </section>
                </#if>

                <#if policies?has_content>
                    <section class="gov_content-block" id="related-policies">
                        <h2 class="gov_content-block__title  gamma">Policies</h2>
                        <ul class="ds_no-bullets">
                            <#list policies as policy>
                                <li><a data-gtm="policy-${policy?index + 1}" href="<@hst.link hippobean=policy/>">${policy.title}</a></li>
                            </#list>
                        </ul>
                        <a href="<@hst.link path='/policies/?topics=' + document.title/>" class="gov_icon-link">
                            <svg class="ds_icon  ds_icon--24" aria-hidden="true" role="img">
                                <use xlink:href="${iconspath}#list"></use>
                            </svg>
                            See all policies <span class="visually-hidden">about ${document.title}</span>
                        </a>
                    </section>
                </#if>

                <#if publications?has_content>
                    <section class="gov_content-block" id="related-publications">
                        <h2 class="gov_content-block__title  gamma">Publications</h2>
                        <ul class="ds_no-bullets">
                            <#list publications as publication>
                                <li><a data-gtm="publications-${publication?index + 1}" href="<@hst.link hippobean=publication/>">${publication.title}</a></li>
                            </#list>
                        </ul>
                        <a href="<@hst.link path='/publications/?topics=' + document.title/>" class="gov_icon-link">
                            <svg class="ds_icon  ds_icon--24" aria-hidden="true" role="img">
                                <use xlink:href="${iconspath}#list"></use>
                            </svg>
                            See all publications <span class="visually-hidden">about ${document.title}</span>
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
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>

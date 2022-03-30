<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <input id="topicName" type="hidden" value="${document.title}"/>
        <main id="main-content" class="ds_layout  gov_layout--topic">
            <div class="ds_layout__banner">
                <header class="gov_feature-header  gov_feature-header--topic" id="page-content">
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
                <@hst.html hippohtml=document.content />
            </div>

            <#if policies?has_content>
                <div class="ds_layout__policies">
                    <section id="policies" class="gov_content-block">
                        <h2 class="gov_content-block__title">Policies</h2>

                        <ul class="gov_policy-list  gov_column-list  gov_column-list--not-small  gov_column-list--3">
                            <#list policies as policy>
                                <li class="gov_policy-list__item  gov_column-list__item">
                                    <a data-navigation="policy-${policy?index + 1}" class="gov_policy-list__link" href="<@hst.link hippobean=policy/>">${policy.title}</a>
                                </li>
                            </#list>
                        </ul>
                    </section>
                </div>
            </#if>

            <#if document.featuredItems?has_content>
                <div class="ds_layout__featured">
                    <section id="featured-items" class="gov_content-block">
                        <h2 class="gov_content-block__title">Featured</h2>

                        <div class="gov_feature-cards">
                            <#list document.featuredItems as item>
                                <div class="ds_card  ds_card--grey  ds_card--hover  gov_feature-card">
                                    <#assign imgLabel = 'news'/>
                                    <#if item.label == 'news'>
                                        <#assign imgLabel = 'news'/>
                                    <#elseif item.label?contains('Consultation')>
                                        <#assign imgLabel = 'cons'/>
                                    <#elseif item.publicationType??>
                                        <#assign imgLabel = 'pubs'/>
                                    </#if>
                                    <#if imgLabel??>
                                        <div class="ds_card__media">
                                            <img src="<@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop.jpg'/>"
                                            srcset="<@hst.link path='/assets/images/graphics/featured-${imgLabel}-tablet.jpg'/>  220w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-tablet_@2x.jpg'/> 440w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop.jpg'/> 293w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop_@2x.jpg'/> 586w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-hd.jpg'/> 360w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-hd_@2x.jpg'/> 720w"
                                            sizes="(min-width:1200px) 360px, (min-width:992px) 293px, (min-width:768px) 220px, 360px" alt="" class="ds_card__image">
                                        </div>
                                    </#if>

                                    <article class="ds_card__content">
                                        <h3 class="ds_card__title">
                                            <a data-navigation="featured-${item?index + 1}" href="<@hst.link hippobean=item/>" class="ds_card__link--cover">
                                                ${item.title}
                                            </a>
                                        </h3>

                                        <p>
                                            ${item.summary}
                                        </p>

                                        <#if item.label?has_content>
                                            <div class="ds_card__content-footer">
                                                <#assign date = (item.publicationDate.time)!item.properties['hippostdpubwf:lastModificationDate'].time />

                                                <dl class="ds_metadata  ds_metadata--inline  gov_featured-item__metadata">
                                                    <div class="ds_metadata__item">
                                                        <dt class="ds_metadata__key  visually-hidden">
                                                            Publication date</dt>
                                                        <dd class="ds_metadata__value">
                                                            <@fmt.formatDate value=date type="both" pattern="dd MMMM yyyy"/>
                                                        </dd>
                                                    </div>

                                                    <div class="ds_metadata__item">
                                                        <dt class="ds_metadata__key  visually-hidden">Type</dt>
                                                        <dd class="ds_metadata__value">${item.label}</dd>
                                                    </div>
                                                </dl>
                                            </div>
                                        </#if>
                                    </article>
                                </div>
                            </#list>
                        </div>

                    </section>
                </div>
            </#if>

            <div class="ds_layout__latest  gov_latest-feeds">
                <section id="latest-publications" class="gov_content-block  gov_latest-feed">
                    <div>
                        <h2 class="gov_content-block__title">
                            Publications
                        </h2>

                        <div id="publications-container" class="gov_latest-feed__items">
                            <#if publications?has_content>
                                <#list publications as publication>
                                    <article class="gov_latest-feed__item">
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=publication />" data-navigation="publications-${publication?index + 1}">${publication.title}</a>
                                        </h3>
                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=publication.displayDate.time type="both" pattern="dd MMMM yyyy"/></p>
                                    </article>
                                </#list>
                            <#else>
                                <p>There are no publications relating to this topic.</p>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/?topics=${document.title}'/>" data-navigation="publications-all">
                            <span class="gov_icon-link__text">See all publications <span class="visually-hidden">about ${document.title}</span></span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>

                <section id="latest-consultations" class="gov_content-block  gov_latest-feed">
                    <div>
                        <h2 class="gov_content-block__title">
                            Consultations
                        </h2>

                        <div id="consultations-container" class="gov_latest-feed__items">
                            <#if consultations?has_content>
                                <#list consultations as consultation>
                                    <article class="gov_latest-feed__item">
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=consultation />" data-navigation="consultations-${consultation?index + 1}">${consultation.title}</a>
                                        </h3>
                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=consultation.publicationDate.time type="both" pattern="dd MMMM yyyy"/></p>
                                    </article>
                                </#list>
                            <#else>
                                <p>There are no consultations relating to this topic.</p>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/?topics=${document.title}&publicationTypes=consultation-analysis;consultation-paper'/>" data-navigation="consultations-all">
                            <span class="gov_icon-link__text">See all consultations <span class="visually-hidden">about ${document.title}</span></span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>

                </section>

                <section id="latest-stats-research" class="gov_content-block  gov_latest-feed">
                    <div>
                        <h2 class="gov_content-block__title">
                            Statistics and research
                        </h2>

                        <div id="statistics-container" class="gov_latest-feed__items">
                            <#if statsAndResearch?has_content>
                                <#list statsAndResearch as publication>
                                    <article class="gov_latest-feed__item">
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=publication />" data-navigation="statistics-${publication?index + 1}">${publication.title}</a>
                                        </h3>
                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=publication.displayDate.time type="both" pattern="dd MMMM yyyy"/></p>
                                    </article>
                                </#list>
                            <#else>
                                <p>There are no statistics and research relating to this topic.</p>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/statistics-and-research/?topics=${document.title}'/>" data-navigation="statistics-all">
                            <span class="gov_icon-link__text">See all Statistics and research <span class="visually-hidden">about ${document.title}</span></span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>

                <section id="latest-news" class="gov_content-block  gov_latest-feed">
                    <div>
                        <h2 class="gov_content-block__title">
                            News
                        </h2>

                        <div class="gov_latest-feed__items">
                            <#if news?has_content>
                                <#list news as newsItem>
                                    <article class="gov_latest-feed__item">
                                        <p class="gov_latest-feed__item__date  gov_latest-feed__item__date--bar"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMMM yyyy HH:mm"/></p>
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=newsItem />" data-navigation="news-${newsItem?index + 1}">${newsItem.title}</a>
                                        </h3>
                                        <p class="gov_latest-feed__item__summary">${newsItem.summary}</p>
                                    </article>
                                </#list>
                            <#else>
                                <p>There are no news items relating to this topic.</p>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/news/?topics=${document.title}'/>" data-navigation="news-all">
                            <span class="gov_icon-link__text">See all news <span class="visually-hidden">about ${document.title}</span></span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>
            </div>

            <div class="ds_layout__directorates">
                <section id="people-and-directorates" class="gov_content-block">
                    <h2 class="gov_content-block__title">People and directorates</h2>

                    <div class="ds_accordion" data-module="ds-accordion">
                        <button data-accordion="accordion-open-all" type="button" class="ds_link  ds_accordion__open-all  js-open-all">Open all <span class="visually-hidden">sections</span></button>

                        <div class="ds_accordion-item">
                            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-people" aria-labelledby="panel-people-heading" />
                            <div class="ds_accordion-item__header">
                                <h3 id="panel-people-heading" class="ds_accordion-item__title">
                                    ${document.peopleSectionTitle!"Cabinet Secretary and Ministers"}
                                </h3>
                                <span class="ds_accordion-item__indicator"></span>
                                <label class="ds_accordion-item__label" for="panel-people"><span class="visually-hidden">Show this section</span></label>
                            </div>
                            <div class="ds_accordion-item__body">
                                <ul class="person-list  gov_column-list  gov_column-list--not-small  gov_column-list--2">
                                    <#list document.responsibleRoles as role>
                                        <li class="gov_person  gov_person--small  gov_column-list__item">
                                            <#if role.incumbent??>
                                                <h4 class="gov_person__name">${role.incumbent.title}</h4>
                                            </#if>
                                            <p class="gov_person__roles"><a href="<@hst.link hippobean=role/>" data-navigation="person-${role?index + 1}">${role.title}</a></p>
                                        </li>
                                    </#list>
                                </ul>
                            </div>
                        </div>

                        <div class="ds_accordion-item">
                            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-directorates" aria-labelledby="panel-directorates-heading" />
                            <div class="ds_accordion-item__header">
                                <h3 id="panel-directorates-heading" class="ds_accordion-item__title">
                                    ${document.directoratesSectionTitle!"Directorates"}
                                </h3>
                                <span class="ds_accordion-item__indicator"></span>
                                <label class="ds_accordion-item__label" for="panel-directorates"><span class="visually-hidden">Show this section</span></label>
                            </div>
                            <div class="ds_accordion-item__body">
                                <ul class="directorate-list  gov_column-list  gov_column-list--not-small  gov_column-list--2">
                                    <#list directorates as directorate>
                                        <li class="directorate-list__item  gov_column-list__item">
                                            <a class="directorate-list__link" data-navigation="directorate-${directorate?index + 1}" href="<@hst.link hippobean=directorate/>">${directorate.title}</a>
                                        </li>
                                    </#list>
                                </ul>
                            </div>
                        </div>
                    </div>
                </section>
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
        <meta name="dc.format" content="Topic"/>
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

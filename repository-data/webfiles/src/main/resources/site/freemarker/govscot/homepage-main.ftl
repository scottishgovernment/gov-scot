<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>


<div class="ds_wrapper">

    <main id="main-content" class="ds_layout  gov_layout--home">
        <#if document??>
            <div class="ds_layout__header">
                <div class="ds_page-header">
                    <@hst.html hippohtml=document.content />
                </div>
            </div>
        </#if>

        <div class="ds_layout__search">
            <#include "common/search.ftl" />
        </div>

        <div class="ds_layout__content">
            <#if document?? && document.featuredItems??>
                <div class="gov_homepage-hero  gov_homepage-hero--${document.featuredItems?size}">
                    <div class="gov_homepage-hero__main">
                        <#assign featuredItem = document.featuredItems?first>
                        <div class="gov_hero-item">
                            <div class="gov_hero-item__media">
                                <a data-gtm="gov_hero-item-1" href="${featuredItem.link.url}">
                                    <div class="gov_hero-item__figure">
                                        <img class="gov_hero-item__image" alt="${featuredItem.title}" src="<@hst.link hippobean=featuredItem.image.featuredlarge/>" />
                                    </div>
                                </a>
                            </div>

                            <div class="gov_hero-item__content">
                                <h2 class="gov_hero-item__title">
                                    <a data-gtm="gov_hero-item-1" href="${featuredItem.link.url}">${featuredItem.title}</a>
                                </h2>

                                <@hst.html hippohtml=featuredItem.teaserText/>
                            </div>
                        </div>
                    </div>

                    <div class="gov_homepage-hero__sub">
                        <#list document.featuredItems[1..] as featuredItem>
                            <div class="gov_hero-item">
                                <div class="gov_hero-item__media">
                                    <a data-gtm="gov_hero-item-${featuredItem?index + 2}" href="${featuredItem.link.url}">
                                        <div class="gov_hero-item__figure">
                                            <img class="gov_hero-item__image" alt="${featuredItem.title}" src="<@hst.link hippobean=featuredItem.image.featuredlarge/>" />
                                        </div>
                                    </a>
                                </div>

                                <div class="gov_hero-item__content">
                                    <h2 class="gov_hero-item__title">
                                        <a data-gtm="gov_hero-item-${featuredItem?index + 2}" href="${featuredItem.link.url}">${featuredItem.title}</a>
                                    </h2>

                                    <@hst.html hippohtml=featuredItem.teaserText/>
                                </div>
                            </div>
                        </#list>
                    </div>
                </div>
            </#if>

            <div class="gov_latest-feeds">
                <section id="latest-publications" class="gov_latest-feed  gov_content-block">
                    <div>
                        <h2 class="gov_content-block__title  gov_content-block__title--publications">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/publications/'/>"
                                data-gtm="panel-pubs">
                                Publications
                            </a>
                        </h2>

                        <div id="publications-container" class="gov_latest-feed__items">
                            <#if publications?has_content>
                                <#list publications as publication>
                                    <article class="gov_latest-feed__item">
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=publication />" data-gtm="publications-${publication?index + 1}" title="${publication.title}">${publication.title}</a>
                                        </h3>

                                        <ul class="gov_latest-feed__item__topics">
                                            <#list publication.topics as topic>
                                                <li>${topic.title}</li>
                                            </#list>
                                        </ul>

                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=publication.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                                    </article>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_latest-feed__see-all" href="<@hst.link path='/publications/'/>"
                        data-gtm="all-pubs">
                            <svg class="ds_icon  ds_icon--28" aria-hidden="true" role="img">
                                <use xlink:href="${iconspath}#list"></use>
                            </svg>
                            See all publications
                        </a>
                    </div>
                </section>

                <section id="latest-consultations" class="gov_latest-feed  gov_content-block">
                    <div>
                        <h2 class="gov_content-block__title  gov_content-block__title--consultations">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/publications/?publicationTypes=consultation-analysis;consultation-paper'/>"
                                data-gtm="panel-cons">
                                Consultations
                            </a>
                        </h2>

                        <div id="publications-container" class="gov_latest-feed__items">
                            <#if consultations?has_content>
                                <#list consultations as consultation>
                                    <article class="gov_latest-feed__item">
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=consultation />" data-gtm="consultations-${consultation?index + 1}" title="${consultation.title}">${consultation.title}</a>
                                        </h3>

                                        <ul class="gov_latest-feed__item__topics">
                                            <#list consultation.topics as topic>
                                                <li>${topic.title}</li>
                                            </#list>
                                        </ul>

                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=consultation.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                                    </article>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_latest-feed__see-all" href="<@hst.link path='/consultations/'/>"
                        data-gtm="all-cons">
                            <svg class="ds_icon  ds_icon--28" aria-hidden="true" role="img">
                                <use xlink:href="${iconspath}#list"></use>
                            </svg>
                            See all consultations
                        </a>
                    </div>
                </section>

                <section id="topics" class="gov_content-block  gov_latest-feed">
                    <div class="gov_content-block__highlight">
                        <h2 class="gov_content-block__title">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/topics/'/>"
                                data-gtm="panel-pols">
                                Topics
                            </a>
                        </h2>

                        <p class="gov_content-block__intro">Look for a policy using keywords or filtering by available topic areas.</p>

                        <form class="scrollable  gov_filters">

                            <fieldset id="filter-search">
                                <legend class="visually-hidden">Keyword search</legend>
                                <label class="ds_label" for="filters-search-term">Keyword</label>

                                <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                                    <input type="text" title="Filter by keyword" name="filters-search-term" id="filters-search-term" placeholder="Keyword" maxlength="160" class="ds_input" />
                                    <a href="<@hst.link path='/policies/' />" title="Submit" class="ds_button  js-policy-form-submit" title="Submit" id="filters-search-submit" >
                                        <span class="visually-hidden">Search</span>
                                        <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                                    </a>
                                </div>
                            </fieldset>

                            <h3 class="gov_content-block__subtitle">Filter by topic</h3>

                            <div class="scrollable__content  ds_field-group--checkboxes  scrollable__content--homepage-policies">
                                <fieldset>
                                    <legend class="hidden">Topics</legend>
                                    <#list topics as item>
                                        <#assign slugifyTitle=item.title?lower_case?replace(' ', '-')?replace(',', '')>

                                        <div class="ds_checkbox  ds_checkbox--small">
                                            <input
                                                id="${slugifyTitle}" name="topics[]" class="ds_checkbox__input" type="checkbox" value="${item.title}">
                                            <label for="${slugifyTitle}" class="ds_checkbox__label">${item.title}</label>
                                        </div>

                                        <#if itemsTrigger>
                                            <#assign noItems = false />
                                        </#if>
                                    </#list>
                                </fieldset>
                            </div>

                            <button href="<@hst.link path='/policies/' />" class="js-policy-form-submit  ds_button">Go</button>
                        </form>
                    </div>

                    <div>
                        <a href="<@hst.link path='/topics/'/>" class="gov_latest-feed__see-all">
                            <svg class="ds_icon  ds_icon--28" aria-hidden="true" role="img"><use xlink:href="${iconspath}#list"></use></svg>
                            See all topics
                        </a>
                    </div>
                </section>

                <section id="latest-news" class="gov_latest-feed  gov_content-block">
                    <div>
                        <h2 class="gov_content-block__title">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/news/'/>"
                                data-gtm="panel-news">
                                News
                            </a>
                        </h2>

                        <div class="homepage-subscribe">
                            <span class="hidden-xsmall">Get all the latest news from gov.scot&hellip;</span>

                            <a data-gtm="news-subscribe" href="http://register.scotland.gov.uk/Subscribe/Step1">
                                Subscribe
                                <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#chevron_right"></use></svg>
                            </a>
                        </div>

                        <div id="news-container" class="gov_latest-feed__items">
                            <#if news?has_content>
                                <#list news as newsItem>
                                    <article class="gov_latest-feed__item">
                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=newsItem />" data-gtm="news-${newsItem?index + 1}" title="${newsItem.title}">${newsItem.title}</a>
                                        </h3>
                                        <p class="gov_latest-feed__item__summary">${newsItem.summary}</p>
                                    </article>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_latest-feed__see-all" href="<@hst.link path='/news/'/>"
                        data-gtm="all-news">
                            <svg class="ds_icon  ds_icon--28" aria-hidden="true" role="img">
                                <use xlink:href="${iconspath}#list"></use>
                            </svg>
                            See all news
                        </a>
                    </div>
                </section>

                <section id="latest-stats-research" class="gov_latest-feed  gov_content-block">
                    <div>
                        <h2 class="gov_content-block__title">
                            Statistics and research
                        </h2>

                        <div id="statistics-container" class="gov_latest-feed__items">
                            <#if statsAndResearch?has_content>
                                <#list statsAndResearch as publication>
                                    <article class="gov_latest-feed__item">
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=publication />" data-gtm="statistics-${publication?index + 1}" title="${publication.title}">${publication.title}</a>
                                        </h3>
                                        <ul class="gov_latest-feed__item__topics">
                                                <li>${publication.label}</li>
                                        </ul>
                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=publication.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                                    </article>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_latest-feed__see-all" href="<@hst.link path='/statistics-and-research/'/>"
                        data-gtm="all-stats">
                            <svg class="ds_icon  ds_icon--28" aria-hidden="true" role="img">
                                <use xlink:href="${iconspath}#list"></use>
                            </svg>
                            See all Statistics and research
                        </a>
                    </div>
                </section>
            </div>

            <section id="about" class="gov_content-block">
                <h2 class="gov_content-block__title">
                    <a class="gov_content-block__title-link" href="<@hst.link path='/about/'/>" data-gtm="panel-govt">
                        About government
                    </a>
                </h2>

                <div class="gov_sublayout  gov_sublayout--twocols">
                    <#if firstMinister??>
                        <div class="gov_homepage-fm">

                            <h3 class="gov_homepage-fm__title">${firstMinister.title}</h3>

                            <picture class="gov_homepage-fm__image">
                                <source srcset="<@hst.link path='/assets/images/people/first_minister_home_mob.jpg'/> 1x, <@hst.link path='/assets/images/people/first_minister_home_mob_@2x.jpg'/> 2x" media="(max-width: 767px)">
                                <source srcset="<@hst.link path='/assets/images/people/first_minister_home_768.jpg'/> 1x, <@hst.link path='/assets/images/people/first_minister_home_768_@2x.jpg'/> 2x" media="(max-width: 991px)">
                                <source srcset="<@hst.link path='/assets/images/people/first_minister_home_1024.jpg'/> 1x, <@hst.link path='/assets/images/people/first_minister_home_1024_@2x.jpg'/> 2x" media="(max-width: 1199px)">

                                <img src="<@hst.link path='/assets/images/people/first_minister_home_hd.jpg'/>" srcset="<@hst.link path='/assets/images/people/first_minister_home_hd.jpg'/> 1x, <@hst.link path='/assets/images/people/first_minister_home_hd_@2x.jpg'/> 2x">
                            </picture>


                            <div class="gov_homepage-fm__content">
                                <@hst.html var="firstMinisterContent" hippohtml=firstMinister.content />
                                ${firstMinisterContent?trim?keep_before("\n")}

                                <p><a class="homepage-about__read-more" data-gtm="read-more" href="<@hst.link hippobean=firstMinister/>">Read more</a></p>
                            </div>
                        </div>
                    </#if>

                    <#if document??>
                        <div>
                            <h3>How government works</h3>

                            <div class="ds_leader  homepage-about__leader">
                                <@hst.html hippohtml=document.howGovernmentWorks />
                            </div>
                        </div>
                    </#if>
                </div>
            </section>
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/home.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/home.es5.js"/>"></script>
</@hst.headContribution>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" path="/" canonical=true/>
    <#include "common/canonical.ftl" />
</#if>

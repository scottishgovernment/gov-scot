<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>


<div class="ds_wrapper">

    <main id="main-content" class="ds_layout  gov_layout--home">
        <div class="ds_layout__header">
            <div class="ds_page-header">
                <#if document??>
                    <@hst.html hippohtml=document.content />
                </#if>
            </div>
        </div>

        <div class="ds_layout__content">
            <#if document?? && document.featuredItems??>
                <div class="gov_homepage-hero  gov_homepage-hero--${document.featuredItems?size}">
                    <div class="gov_homepage-hero__main">
                        <#assign featuredItem = document.featuredItems?first>

                        <div class="gov_hero-item">
                            <div class="gov_hero-item__media">
                                <a tabindex="-1" data-navigation="hero-1-image" href="${featuredItem.link.url}">
                                    <div class="gov_hero-item__figure">
                                        <img class="gov_hero-item__image" alt="${featuredItem.title}" src="<@hst.link hippobean=featuredItem.image.xlargeeightcolumnsdoubled/>"
                                            width="${featuredItem.image.xlargesevencolumns.width?c}"
                                            height="${featuredItem.image.xlargesevencolumns.height?c}"
                                        <#if document.featuredItems?size == 1>
                                            srcset="
                                            <@hst.link hippobean=featuredItem.image.mediumtwelvecolumns/> 736w,
                                            <@hst.link hippobean=featuredItem.image.largeeightcolumns/> 608w,
                                            <@hst.link hippobean=featuredItem.image.mediumtwelvecolumnsdoubled/> 1472w,
                                            <@hst.link hippobean=featuredItem.image.largeeightcolumnsdoubled/> 1216w"
                                            sizes="(min-width:1200px) 736px, (min-width:992px) 608px, 736px"
                                        <#elseif document.featuredItems?size == 2>
                                            srcset="
                                            <@hst.link hippobean=featuredItem.image.mediumtwelvecolumns/> 736w,
                                            <@hst.link hippobean=featuredItem.image.largeeightcolumns/> 608w,
                                            <@hst.link hippobean=featuredItem.image.xlargesevencolumns/> 640w,
                                            <@hst.link hippobean=featuredItem.image.mediumtwelvecolumnsdoubled/> 1472w,
                                            <@hst.link hippobean=featuredItem.image.largeeightcolumnsdoubled/> 1216w,
                                            <@hst.link hippobean=featuredItem.image.xlargesevencolumnsdoubled/> 1280w"
                                            sizes="(min-width:1200px) 640px, (min-width:992px) 608px, 736px"
                                        <#elseif document.featuredItems?size == 3>
                                            srcset="
                                            <@hst.link hippobean=featuredItem.image.mediumtwelvecolumns/> 736w,
                                            <@hst.link hippobean=featuredItem.image.largeeightcolumns/> 608w,
                                            <@hst.link hippobean=featuredItem.image.xlargesevencolumns/> 640w,
                                            <@hst.link hippobean=featuredItem.image.mediumtwelvecolumnsdoubled/> 1472w,
                                            <@hst.link hippobean=featuredItem.image.largeeightcolumnsdoubled/> 1216w,
                                            <@hst.link hippobean=featuredItem.image.xlargesevencolumnsdoubled/> 1280w"
                                            sizes="(min-width:1200px) 640px, (min-width:992px) 608px, 736px"
                                        <#else>
                                            srcset="
                                            <@hst.link hippobean=featuredItem.image.mediumtwelvecolumns/> 736w,
                                            <@hst.link hippobean=featuredItem.image.largesevencolumns/> 528w,
                                            <@hst.link hippobean=featuredItem.image.xlargesixcolumns/> 544w,
                                            <@hst.link hippobean=featuredItem.image.mediumtwelvecolumnsdoubled/> 736w,
                                            <@hst.link hippobean=featuredItem.image.largesevencolumnsdoubled/> 1054w,
                                            <@hst.link hippobean=featuredItem.image.xlargesixcolumnsdoubled/> 1088w"
                                            sizes="(min-width:1200px) 544px, (min-width:992px) 528px, 736px"
                                        </#if>
                                        >
                                    </div>
                                </a>
                            </div>

                            <div class="gov_hero-item__content">
                                <h2 class="gov_hero-item__title">
                                    <a data-navigation="hero-1-title" href="${featuredItem.link.url}">${featuredItem.title}</a>
                                </h2>

                                <@hst.html hippohtml=featuredItem.teaserText/>
                            </div>
                        </div>
                    </div>

                    <#if document.featuredItems?size gt 1>
                        <div class="gov_homepage-hero__sub">
                            <#list document.featuredItems[1..] as featuredItem>
                                <div class="gov_hero-item">
                                    <div class="gov_hero-item__media">
                                        <a tabindex="-1" data-navigation="hero-${featuredItem?index + 2}-image" href="${featuredItem.link.url}">
                                            <div class="gov_hero-item__figure">
                                                <img class="gov_hero-item__image" alt="${featuredItem.title}" src="<@hst.link hippobean=featuredItem.image.xlargetwocolumns/>"
                                                    width="${featuredItem.image.xlargethreecolumns.width?c}"
                                                    height="${featuredItem.image.xlargethreecolumns.height?c}"
                                                    <#if document.featuredItems?size == 2>
                                                        srcset="
                                                        <@hst.link hippobean=featuredItem.image.mediumfourcolumns/> 224w,
                                                        <@hst.link hippobean=featuredItem.image.largefourcolumns/> 288w,
                                                        <@hst.link hippobean=featuredItem.image.xlargethreecolumns/> 256w,
                                                        <@hst.link hippobean=featuredItem.image.mediumfourcolumnsdoubled/> 448w,
                                                        <@hst.link hippobean=featuredItem.image.largefourcolumnsdoubled/> 576w,
                                                        <@hst.link hippobean=featuredItem.image.xlargethreecolumnsdoubled/> 512w"
                                                        sizes="(min-width:1200px) 256px, (min-width:992px) 288px, 224px"
                                                    <#elseif document.featuredItems?size == 3>
                                                        srcset="
                                                        <@hst.link hippobean=featuredItem.image.mediumfourcolumns/> 224w,
                                                        <@hst.link hippobean=featuredItem.image.largethreecolumns/> 208w,
                                                        <@hst.link hippobean=featuredItem.image.xlargethreecolumns/> 256w,
                                                        <@hst.link hippobean=featuredItem.image.mediumfourcolumnsdoubled/> 448w,
                                                        <@hst.link hippobean=featuredItem.image.largethreecolumnsdoubled/> 416w,
                                                        <@hst.link hippobean=featuredItem.image.xlargethreecolumnsdoubled/> 512w"
                                                        sizes="(min-width:1200px) 256px, (min-width:992px) 208px, 224px"
                                                    <#else>
                                                        srcset="
                                                        <@hst.link hippobean=featuredItem.image.mediumfourcolumns/> 224w,
                                                        <@hst.link hippobean=featuredItem.image.largetwocolumns/> 128w,
                                                        <@hst.link hippobean=featuredItem.image.xlargetwocolumns/> 160w,
                                                        <@hst.link hippobean=featuredItem.image.mediumfourcolumnsdoubled/> 448w,
                                                        <@hst.link hippobean=featuredItem.image.largetwocolumnsdoubled/> 256w,
                                                        <@hst.link hippobean=featuredItem.image.xlargetwocolumnsdoubled/> 320w"
                                                        sizes="(min-width:1200px) 160px, (min-width:992px) 128px, 224px"
                                                    </#if>
                                                >
                                            </div>
                                        </a>
                                    </div>

                                    <div class="gov_hero-item__content">
                                        <h2 class="gov_hero-item__title">
                                            <a data-navigation="hero-${featuredItem?index + 2}-title" href="${featuredItem.link.url}">${featuredItem.title}</a>
                                        </h2>

                                        <@hst.html hippohtml=featuredItem.teaserText/>
                                    </div>
                                </div>
                            </#list>
                        </div>
                    </#if>
                </div>
            </#if>

            <div class="gov_latest-feeds">
                <section id="latest-publications" class="gov_latest-feed  gov_content-block">
                    <div>
                        <h2 class="gov_content-block__title  gov_content-block__title--publications">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/publications/'/>"
                                data-navigation="publications-title">
                                Publications
                            </a>
                        </h2>

                        <div class="gov_latest-feed__items">
                            <#if publications?has_content>
                                <#list publications as publication>
                                    <article class="gov_latest-feed__item">
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=publication />" data-navigation="publications-${publication?index + 1}">${publication.title}</a>
                                        </h3>

                                        <ul class="gov_latest-feed__item__topics">
                                            <#list publication.topics as topic>
                                                <li>${topic.title}</li>
                                            </#list>
                                        </ul>

                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=publication.displayDate.time type="both" pattern="dd MMMM yyyy"/></p>
                                    </article>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/'/>" data-navigation="publications-all">
                            <span class="gov_icon-link__text">See all publications</span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>

                <section id="latest-consultations" class="gov_latest-feed  gov_content-block">
                    <div>
                        <h2 class="gov_content-block__title  gov_content-block__title--consultations">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/publications/?publicationTypes=consultation-analysis;consultation-paper'/>"
                                data-navigation="consultations-title">
                                Consultations
                            </a>
                        </h2>

                        <div class="gov_latest-feed__items">
                            <#if consultations?has_content>
                                <#list consultations as consultation>
                                    <article class="gov_latest-feed__item">
                                        <h3 class="gov_latest-feed__item__title">
                                            <a href="<@hst.link hippobean=consultation />" data-navigation="consultations-${consultation?index + 1}">${consultation.title}</a>
                                        </h3>

                                        <ul class="gov_latest-feed__item__topics">
                                            <#list consultation.topics as topic>
                                                <li>${topic.title}</li>
                                            </#list>
                                        </ul>

                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=consultation.displayDate.time type="both" pattern="dd MMMM yyyy"/></p>
                                    </article>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/?publicationTypes=consultation-analysis;consultation-paper'/>" data-navigation="consultations-all">
                            <span class="gov_icon-link__text">See all consultations</span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>

                <section id="topics" class="gov_content-block  gov_latest-feed">
                    <div class="gov_content-block__highlight">
                        <h2 class="gov_content-block__title">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/topics/'/>"
                                data-navigation="topics-title">
                                Topics
                            </a>
                        </h2>

                        <p class="gov_content-block__intro">Look for a policy using keywords or filtering by available topic areas.</p>

                        <form class="gov_filters  ds_no-margin--bottom ds_form">
                            <div class="ds_form__content">
                                <div class="ds_question">
                                <fieldset id="filter-search">
                                    <legend class="visually-hidden">Keyword search</legend>
                                    <label class="ds_label" for="filters-search-term">Keyword</label>

                                    <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                                        <input type="text" title="Filter by keyword" name="term" id="filters-search-term" placeholder="Keyword" maxlength="160" class="ds_input" />
                                        <button data-href="<@hst.link path='/policies/' />" class="ds_button  js-policy-form-submit" title="Submit" id="filters-search-submit" >
                                            <span class="visually-hidden">Search</span>
                                            <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#search"></use></svg>
                                        </button>
                                    </div>
                                </fieldset>
                                </div>
                                <div class="ds_question">
                                <fieldset>
                                    <legend class="gov_content-block__subtitle">Filter by topic</legend>

                                    <div class="gov_filters__scrollable  gov_filters__scrollable--fixed">
                                        <div class="ds_field-group--checkboxes">
                                            <#list topics as item>
                                                <#assign slugifyTitle=item.title?lower_case?replace(' ', '-')?replace(',', '')>

                                                <div class="ds_checkbox  ds_checkbox--small">
                                                    <input
                                                        id="${slugifyTitle}" name="topics[]" class="ds_checkbox__input" type="checkbox" value="${item.title}">
                                                    <label for="${slugifyTitle}" class="ds_checkbox__label">${item.title}</label>
                                                </div>
                                            </#list>
                                        </div>
                                    </div>
                                </fieldset>
                                </div>
                            </div>
                            <div class="ds_form__actions">
                                <button type="button" data-href="<@hst.link path='/policies/' />" class="js-policy-form-submit  ds_button  ds_button--fixed  ds_button--has-icon  ds_no-margin--bottom">
                                    Search policies
                                    <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#chevron_right"></use></svg>
                                </button>
                            </div>
                        </form>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/topics/'/>" data-navigation="topics-all">
                            <span class="gov_icon-link__text">See all topics</span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>

                <section id="latest-news" class="gov_latest-feed  gov_latest-feed--horizontal  gov_content-block">
                    <div>
                        <h2 class="gov_content-block__title">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/news/'/>"
                                data-navigation="news-title">
                                News
                            </a>
                        </h2>

                        <div class="gov_homepage-subscribe">
                            <span class="gov_homepage-subscribe__text">Get all the latest news from gov.scot</span>

                            <a data-navigation="news-subscribe" class="gov_homepage-subscribe__link  gov_icon-link" href="https://eepurl.com/gEp6KP">
                                <span class="gov_icon-link__text">Subscribe</span>
                                <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                            </a>
                        </div>

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
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/news/'/>" data-navigation="news-all">
                            <span class="gov_icon-link__text">See all news</span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>

                <section id="latest-stats-research" class="gov_latest-feed  gov_latest-feed--horizontal  gov_content-block">
                    <div>
                        <h2 class="gov_content-block__title">
                            <a class="gov_content-block__title-link"
                                href="<@hst.link path='/statistics-and-research/'/>"
                                data-navigation="statistics-title">
                                Statistics and research
                            </a>
                        </h2>

                        <div id="statistics-container" class="gov_latest-feed__items">
                            <#list statisticsAndResearch as publication>
                                <article class="gov_latest-feed__item">
                                    <h3 class="gov_latest-feed__item__title">
                                        <a href="<@hst.link hippobean=publication />" data-navigation="statistics-${publication?index + 1}">${publication.title}</a>
                                    </h3>
                                    <ul class="gov_latest-feed__item__topics">
                                            <li>${publication.label}</li>
                                    </ul>
                                    <p class="gov_latest-feed__item__date"><@fmt.formatDate value=publication.displayDate.time type="both" pattern="dd MMMM yyyy"/></p>
                                </article>
                            </#list>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/statistics-and-research/'/>" data-navigation="statistics-all">
                            <span class="gov_icon-link__text">See all Statistics and research</span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>
            </div>

            <section id="about" class="gov_content-block">
                <h2 class="gov_content-block__title">
                    <a class="gov_content-block__title-link" href="<@hst.link path='/about/'/>" data-navigation="about-title">
                        About government
                    </a>
                </h2>

                <div class="gov_sublayout  gov_sublayout--twocols">
                    <#if firstMinister??>
                        <div class="gov_homepage-fm">

                            <h3 class="gov_homepage-fm__title">${firstMinister.title}</h3>

                            <picture class="gov_homepage-fm__image">
                                <source width="${document.fmImageLandscape.smallcolumns.width?c}" height="${document.fmImageLandscape.smallcolumns.height?c}" srcset="<@hst.link hippobean=document.fmImageLandscape.smallcolumns/> 1x, <@hst.link hippobean=document.fmImageLandscape.smallcolumnsdoubled/> 2x" media="(max-width: 767px)">
                                <source width="${document.fmImageLandscape.mediumsixcolumns.width?c}" height="${document.fmImageLandscape.mediumsixcolumns.height?c}" srcset="<@hst.link hippobean=document.fmImageLandscape.mediumsixcolumns/> 1x, <@hst.link hippobean=document.fmImageLandscape.mediumsixcolumnsdoubled/> 2x" media="(max-width: 991px)">
                                <source width="${document.fmImageLandscape.largetwocolumns.width?c}" height="${document.fmImageLandscape.largetwocolumns.height?c}" srcset="<@hst.link hippobean=document.fmImagePortrait.largetwocolumns/> 1x, <@hst.link hippobean=document.fmImagePortrait.largetwocolumnsdoubled/> 2x" media="(max-width: 1199px)">

                                <img alt="${document.fmImageAlt}" loading="lazy" width="${document.fmImagePortrait.xlargetwocolumns.width?c}" height="${document.fmImagePortrait.xlargetwocolumns.height?c}" src="<@hst.link hippobean=document.fmImagePortrait.xlargetwocolumns/>" srcset="<@hst.link hippobean=document.fmImagePortrait.xlargetwocolumns/> 1x, <@hst.link hippobean=document.fmImagePortrait.xlargetwocolumnsdoubled/> 2x">
                            </picture>

                            <div class="gov_homepage-fm__content">
                                <@hst.html var="firstMinisterContent" hippohtml=firstMinister.content />
                                ${firstMinisterContent?trim?keep_before("\n")?no_esc}

                                <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link hippobean=firstMinister/>">
                                    <span class="gov_icon-link__text">Read more <span class="visually-hidden">about the First Minister</span></span>
                                    <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                                </a>
                            </div>
                        </div>
                    </#if>

                    <#if document??>
                        <div>
                            <h3>How government works</h3>

                            <@hst.html hippohtml=document.howGovernmentWorks />
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
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <#include "common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" path="/" canonical=true/>
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>

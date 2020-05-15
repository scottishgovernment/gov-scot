<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<@hst.manageContent hippobean=document/>

<div class="layout--topic">

    <header class="topic-header  <#if document.image??>topic-header--has-image</#if>" id="page-content">
        <#if document.image??>
            <img alt="" src="<@hst.link hippobean=document.image.bannerdesktop/>" class="topic-header__image">
        </#if>

        <h1 class="article-header  topic-header__title">${document.title}</h1>
    </header>

    <input id="topicName" type="hidden" value="${document.title}"/>

    <div class="body-content  leader--first-para">
        <@hst.html hippohtml=document.content />
    </div>

    <#if policies?has_content>
        <h2 class="emphasis  topic-block__title">Policies</h2>

        <ul class="policy-list column-list  column-list--not-small  column-list--3">
            <#list policies as policy>
                <li class="policy-list__item  column-list__item">
                    <a data-gtm="policy-${policy?index + 1}" class="policy-list__link" href="<@hst.link hippobean=policy/>">${policy.title}</a>
                </li>
            </#list>
        </ul>
    </#if>

    <#if document.featuredItems?has_content>

        <section id="featured-items" class="topic-block">
            <h2 class="emphasis  topic-block__title">Featured</h2>

            <ul class="grid"><!--
                <#list document.featuredItems as item>
                --><li class="grid__item  medium--four-twelfths  listed-content-item  listed-content-item--dark  listed-content-item--small">
                        <article class="listed-content-item__article">
                            <#-- use news as default image -->
                            <#assign imgLabel = 'news'/>
                            <#if item.label == 'news'>
                                <#assign imgLabel = 'news'/>
                            <#elseif item.label?contains('Consultation')>
                                <#assign imgLabel = 'cons'/>
                            <#elseif item.publicationType??>
                                <#assign imgLabel = 'pubs'/>
                            </#if>
                            <#if imgLabel??>
                                <img src="<@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop.jpg'/>"
                                    srcset="<@hst.link path='/assets/images/graphics/featured-${imgLabel}-tablet.jpg'/>  220w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-tablet_@2x.jpg'/> 440w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop.jpg'/> 293w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop_@2x.jpg'/> 586w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-hd.jpg'/> 360w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-hd_@2x.jpg'/> 720w"
                                    sizes="(min-width:1200px) 360px, (min-width:992px) 293px, (min-width:768px) 220px, 360px" alt="" class="listed-content-item__feature-image">
                            </#if>

                            <header class="listed-content-item__heading">
                                <#if item.label?has_content>
                                    <#assign date = (item.publicationDate.time)!item.properties['hippostdpubwf:lastModificationDate'].time />
                                    <div class="listed-content-item__meta">
                                        <div class="listed-content-item__meta-right">
                                            <p class="listed-content-item__date"><@fmt.formatDate value=date type="both" pattern="dd MMM yyyy"/></p>
                                        </div>
                                        <div class="listed-content-item__meta-left">
                                            <p class="listed-content-item__label  js-truncate" data-lines="1">${item.label}</p>
                                        </div>
                                    </div>
                                </#if>

                                <h3 class="listed-content-item__title  js-truncate" title="${item.title}">
                                    <a href="<@hst.link hippobean=item/>" title="${item.title}" class="listed-content-item__link" data-gtm="featured-item-${item?index + 1}">
                                        ${item.title}
                                    </a>
                                </h3>
                            </header>

                            <p class="listed-content-item__summary  hidden-small  hidden-xsmall  js-truncate" title="${item.summary}">
                                ${item.summary}
                            </p>
                        </article>
                    </li><!--
                </#list>
            --></ul>
        </section>
    </#if>

    <div class="grid"><!--

        --><#if topicsStatsPanelEnabled == false><div class="grid__item  medium--four-twelfths">
        <section id="latest-news" class="topic-block">
            <h2 class="emphasis  topic-block__title">
                News
            </h2>

            <div id="news-container">
                <#if news?has_content>
                    <#list news as newsItem>
                        <article class="homepage-publication">
                        <h3 class="js-truncate  homepage-publication__title">
                            <a href="<@hst.link hippobean=newsItem />" data-gtm="news-${newsItem?index + 1}" title="${newsItem.title}">${newsItem.title}</a>
                        </h3>
                        <p class="homepage-publication__date"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                        </article>
                    </#list>
                <#else>
                    <p>There are no news items relating to this topic.</p>
                </#if>
            </div>

            <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
            <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/news/?topics=${document.title}'/>"
            data-gtm="all-news">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all news
            </a>
        </section>
    </div></#if><!--


        --><div class="grid__item  medium--four-twelfths">
        <section id="latest-publications" class="topic-block">
            <h2 class="emphasis  topic-block__title">
                Publications
            </h2>

            <div id="publications-container">
                <#if publications?has_content>
                    <#list publications as publication>
                        <article class="homepage-publication">
                            <h3 class="js-truncate  homepage-publication__title">
                                <a href="<@hst.link hippobean=publication />" data-gtm="publications-${publication?index + 1}" title="${publication.title}">${publication.title}</a>
                            </h3>
                            <p class="homepage-publication__date"><@fmt.formatDate value=publication.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                        </article>
                    </#list>
                <#else>
                    <p>There are no publications relating to this topic.</p>
                </#if>
            </div>

            <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
            <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/publications/?topics=${document.title}'/>"
            data-gtm="all-pubs">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all publications
            </a>

        </section>
        </div><!--


        --><div class="grid__item  medium--four-twelfths">
        <section id="latest-consultations" class="topic-block">
            <h2 class="emphasis  topic-block__title">
                Consultations
            </h2>

            <div id="consultations-container">
                <#if consultations?has_content>
                    <#list consultations as consultation>
                        <article class="homepage-publication">
                            <h3 class="js-truncate  homepage-publication__title">
                                <a href="<@hst.link hippobean=consultation />" data-gtm="consultations-${consultation?index + 1}" title="${consultation.title}">${consultation.title}</a>
                            </h3>
                            <p class="homepage-publication__date"><@fmt.formatDate value=consultation.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                        </article>
                    </#list>
                <#else>
                    <p>There are no consultations relating to this topic.</p>
                </#if>
            </div>

            <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
            <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/publications/?topics=${document.title}&publicationTypes=consultation-analysis;consultation-paper'/>"
            data-gtm="all-cons">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all consultations
            </a>

        </section>
        </div><!--

        --><#if topicsStatsPanelEnabled == true><div class="grid__item  medium--four-twelfths">
        <section id="latest-stats-research" class="topic-block">
            <h2 class="emphasis  topic-block__title">
                Statistics and research
            </h2>

            <div id="publications-container">
                <#if statsAndResearch?has_content>
                    <#list statsAndResearch as publication>
                        <article class="homepage-publication">
                            <h3 class="js-truncate  homepage-publication__title">
                                <a href="<@hst.link hippobean=publication />" data-gtm="publications-${publication?index}" title="${publication.title}">${publication.title}</a>
                            </h3>
                             <ul class="homepage-publication__topics">
                                    <li>${publication.label}</li>
                            </ul>
                            <p class="homepage-publication__date"><@fmt.formatDate value=publication.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                        </article>
                    </#list>
                <#else>
                    <p>There are no statistics and research relating to this topic.</p>
                </#if>
            </div>

            <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
            <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/statistics-and-research/?topics=${document.title}'/>"
            data-gtm="all-stats">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all Statistics and research
            </a>

        </section>
        </div></#if><!--



   --> </div>
    <!-- /end .grid -->

    <div class="topic-block  topic-block--negate-top-margin  hidden-xsmall">
        <div class="grid">
            <#if topicsStatsPanelEnabled == false><div class="grid__item medium--four-twelfths">
                <!-- if you're changing this link remember to also change the mobile equivalent above -->
                 <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/news/?topics=${document.title}'/>"
                data-gtm="all-news">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all news
            </a>
            </div></#if><div class="grid__item medium--four-twelfths">
                <!-- if you're changing this link remember to also change the mobile equivalent above -->
                <a class="button  button--tertiary  tst-all-pubs" href="<@hst.link path='/publications/?topics=${document.title}'/>"
                data-gtm="all-pubs">
                    <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                        <use xlink:href="${iconspath}#3x3grid"></use>
                    </svg>
                    See all publications
                </a>
            </div><div class="grid__item medium--four-twelfths">
                <!-- if you're changing this link remember to also change the mobile equivalent above -->
                <a class="button  button--tertiary  tst-all-cons"
                href="<@hst.link path='/publications/?topics=${document.title}&publicationTypes=consultation-analysis;consultation-paper'/>"
                data-gtm="all-cons">
                    <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                        <use xlink:href="${iconspath}#3x3grid"></use>
                    </svg>
                    See all consultations
                </a>
            </div><#if topicsStatsPanelEnabled == true><div class="grid__item medium--four-twelfths">
                <!-- if you're changing this link remember to also change the mobile equivalent above -->
                <a class="button  button--tertiary  tst-all-stats" href="<@hst.link path='/statistics-and-research/?topics=${document.title}'/>"
                data-gtm="all-stats">
                    <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                        <use xlink:href="${iconspath}#3x3grid"></use>
                    </svg>
                    See all Statistics and research
                </a>
            </div></#if>
        </div>
    </div>


<!-- start news -->

<#if topicsStatsPanelEnabled == true>
<section id="latest-news" class="topic-block">
    <h2 class="emphasis  topic-block__title">
        News
    </h2>

        <div class="grid"><!--

            <#list news as newsItem>
             --><div class="grid__item medium--four-twelfths homepage-news__item">
                    <article class="narrow">
                        <h3 class="js-truncate  homepage-publication__title">
                            <a href="<@hst.link hippobean=newsItem />" data-gtm="news-${newsItem?index}" title="${newsItem.title}">${newsItem.title}</a>
                        </h3>
                        <p class="homepage-publication__date"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                    </article>
                </div><!--
            </#list>

        --></div>

        <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/news/?topics=${document.title}'/>"
           data-gtm="all-news">
            <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                <use xlink:href="${iconspath}#3x3grid"></use>
            </svg>
            See all news
        </a>
    </section><!--

--><div class="topic-block  topic-block--negate-top-margin  hidden-xsmall">
        <div class="grid">
            <div class="grid__item  medium--four-twelfths">
                <!-- if you're changing this link remember to also change the mobile equivalent above -->
                <a class="button  button--tertiary  tst-all-news" href="<@hst.link path='/news/?topics=${document.title}'/>"
                data-gtm="all-news">
                    <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                        <use xlink:href="${iconspath}#3x3grid"></use>
                    </svg>
                    See all news
                </a>
            </div>
        </div>    
</div>
</#if>

<!-- close news -->

    <section class="topic-block" id="people-and-directorates">
        <h2 class="emphasis  topic-block__title">People and directorates</h2>

        <div class="expandable  topic-expandable">
            <div class="expandable-item  expandable-item--open  expandable-item--init-open" id="people-expandable">
                <button type="button" class="expandable-item__header  js-toggle-expand" tabindex="0">
                    <h3 class="expandable-item__title">${document.peopleSectionTitle!"Cabinet Secretary and Ministers"}</h3>
                    <span class="expandable-item__icon">
                        <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more">
                            <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                        </svg>
                        <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less">
                            <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                        </svg>
                    </span>
                </button>

                <div class="expandable-item__body">

                    <ul class="person-list  column-list  column-list--not-small  column-list--2">
                        <#list document.responsibleRoles as role>
                            <li class="person  person--small">
                                <#if role.incumbent??>
                                    <h4 class="person__name">${role.incumbent.title}</h4>
                                </#if>
                                <p class="person__roles"><a href="<@hst.link hippobean=role/>" data-gtm="person-${role?index + 1}">${role.title}</a></p>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>
            <!-- /end .expandable-item -->

            <div class="expandable-item" id="directorate-expandable">
                <button type="button" class="expandable-item__header  js-toggle-expand" tabindex="0">
                    <h3 class="expandable-item__title">${document.directoratesSectionTitle!"Directorates"}</h3>
                    <span class="expandable-item__icon">
                        <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more">
                            <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                        </svg>
                        <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less">
                            <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                        </svg>
                    </span>
                </button>

                <div class="expandable-item__body">

                    <ul class="directorate-list  column-list  column-list--not-small  column-list--2">
                        <#list directorates as directorate>
                            <li class="directorate-list__item  column-list__item">
                                <a class="directorate-list__link" data-gtm="directorate-${directorate?index + 1}" href="<@hst.link hippobean=directorate/>">${directorate.title}</a>
                            </li>
                        </#list>
                    </ul>
                </div>
            </div>
            <!-- /end .expandable-item -->

        </div>
        <!-- /end .expandable -->

    </section>

</div>

<div class="grid"><!--
    --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
        <#include 'common/feedback-wrapper.ftl'>
    </div><!--
--></div>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/topic.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/topic.es5.js"/>"></script>
</@hst.headContribution>

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

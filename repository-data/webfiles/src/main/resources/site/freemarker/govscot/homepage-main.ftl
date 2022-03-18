<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="layout--home">

<div class="wrapper" id="page-content">
    <h1 class="hidden"><#if document??>${document.title}<#else>The Scottish Government</#if></h1>

    <!-- WELCOME SECTION -->
    <div class="welcome">
        <div class="grid"><!--
        <#if document??>
            --><div class="grid__item medium--seven-twelfths">
                <div class="welcome__intro">
                    <@hst.html hippohtml=document.content />
                </div>
            </div><!--
        </#if>

        --><div class="grid__item medium--four-twelfths push--medium--one-twelfth hidden-xsmall">
                <div class="search-box welcome__search-box ">
                    <form class="search-box__form" method="GET" action="<@hst.link path='/search/'/>">
                        <label class="search-box__label hidden" for="search-box">Search</label>
                        <input name="q" required="" id="search-box" class="search-box__input" type="text" placeholder="Search site">
                        <input name="cat" value="sitesearch" hidden>
                        <button type="submit" title="search" class="search-box__button button button--primary">
                            <span class="icon icon--search-white"></span>
                            <span class="hidden">Search</span>
                        </button>
                    </form>
                </div>
            </div><!--
        --></div>
    </div>
</div>


<#if document??>
<div class="wrapper">
    <div class="homepage-hero  homepage-hero--${document.featuredItems?size}">
        <div class="homepage-hero__main">

            <#if document.featuredItems?has_content>
                <#assign featuredItem = document.featuredItems?first>
                <div class="hero-item">
                    <div class="hero-item__media">
                        <a data-gtm="hero-item-1" href="${featuredItem.link.url}">
                            <div class="hero-item__figure">
                                <img class="hero-item__image" alt="${featuredItem.title}" src="<@hst.link hippobean=featuredItem.image.featuredlarge/>" />
                            </div>
                        </a>
                    </div>

                    <div class="hero-item__content">
                        <h2 class="hero-item__title">
                            <a data-gtm="hero-item-1" href="${featuredItem.link.url}">${featuredItem.title}</a>
                        </h2>

                        <@hst.html hippohtml=featuredItem.teaserText/>
                    </div>
                </div>
            </#if>
        </div>

        <div class="homepage-hero__sub">
            <#list document.featuredItems[1..] as featuredItem>
                <div class="hero-item">
                    <div class="hero-item__media">
                        <a data-gtm="hero-item-${featuredItem?index + 2}" href="${featuredItem.link.url}">
                            <div class="hero-item__figure">
                                <img class="hero-item__image" alt="${featuredItem.title}" src="<@hst.link hippobean=featuredItem.image.featuredlarge/>" />
                            </div>
                        </a>
                    </div>

                    <div class="hero-item__content">
                        <h2 class="hero-item__title">
                            <a data-gtm="hero-item-${featuredItem?index + 2}" href="${featuredItem.link.url}">${featuredItem.title}</a>
                        </h2>

                        <@hst.html hippohtml=featuredItem.teaserText/>
                    </div>
                </div>
            </#list>
        </div>
    </div>
</div>
</#if>

<div class="wrapper">
    <div class="grid"><!--
        --><div class="grid__item medium--four-twelfths push--medium--eight-twelfths">
            <!-- TOPICS -->
            <section id="topics" class="js-topics homepage-block homepage-block--highlight narrow">
                <h2 class="emphasis homepage-block__title"><a class="homepage-block__title-link"
                                                            href="<@hst.link path='/topics/'/>"
                                                            data-gtm="panel-pols">Topics</a></h2>

                <p class="homepage-block__intro">Look for a policy using keywords or filtering by available topic areas.</p>

                <form class="scrollable">

                    <fieldset id="filter-search" class="filters__fieldset filter-search">
                        <legend class="filters__legend">Keyword search</legend>
                        <label class="filters__label" for="filters-search-term">Filter by keyword</label>
                        <div class="filters-input__wrapper">
                            <input type="text" title="Filter by keyword" name="filters-search-term" id="filters-search-term" placeholder="Keyword" maxlength="160" class="filters__input--search-term-home filters__input--search-term" />
                            <a href="<@hst.link path='/policies/' />" title="Submit" class="filter-search__button filter-search__button--home filter-search__button--submit  js-policy-form-submit button button--clear"></a>
                        </div>
                    </fieldset>

                    <h3 class="filter-search__subtitle homepage-block__subtitle">Filter by topic</h3>

                    <div class="scrollable__content checkbox-group scrollable__content--homepage-policies">
                        <fieldset>
                            <legend class="hidden">Topics</legend>
                            <#list topics as topic>
                                <#assign slugifyTitle=topic.title?lower_case?replace(' ', '-')?replace(',', '')>
                                <input id="${slugifyTitle}" name="topics[]" class="fancy-checkbox checkbox-group__input" type="checkbox" value="${topic.title}"/><label for="${slugifyTitle}" class="checkbox-group__label fancy-checkbox">${topic.title}</label>
                            </#list>
                        </fieldset>
                    </div>

                    <button href="<@hst.link path='/policies/' />" class="js-policy-form-submit button button--primary homepage-block__button">Go</button>

                </form>
            </section>

            <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
            <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/topics/'/>"
            data-gtm="all-topics">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all topics
            </a>
        </div><!--

        --><div class="grid__item medium--four-twelfths pull--medium--four-twelfths">
            <!-- PUBLICATIONS -->
            <section id="publications" class="homepage-block">
                <h2 class="emphasis homepage-block__title homepage-block__title--icon
                    homepage-block__title--icon--publication"><a class="homepage-block__title-link"
                                                                href="<@hst.link path='/publications/'/>"
                                                                data-gtm="panel-pubs">Publications</a></h2>
                <#list publications as publication>
                    <article class="homepage-publication">
                        <h3 class="js-truncate homepage-publication__title"><a href="<@hst.link hippobean=publication />"
                                                                            data-gtm="pubs-${publication?index + 1}" title="${publication.title}">${publication.title}</a></h3>
                        <ul class="homepage-publication__topics">
                            <#list publication.topics as topic>
                            <li>${topic.title}</li>
                            </#list>
                        </ul>
                        <p class="homepage-publication__date"><@fmt.formatDate value=publication.displayDate.time type="both" pattern="dd MMM yyyy"/></p>
                    </article>
                </#list>

            </section>

            <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
            <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/publications/'/>"
            data-gtm="all-pubs">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all publications
            </a>
        </div><!--

        --><div class="grid__item medium--four-twelfths pull--medium--four-twelfths">
            <!-- CONSULTATIONS -->
            <section id="consultations" class="homepage-block">
                <h2 class="emphasis homepage-block__title homepage-block__title--icon
                    homepage-block__title--icon--consultation"><a class="homepage-block__title-link"
                                                                href="<@hst.link path='/publications/?publicationTypes=consultation-analysis;consultation-paper'/>"
                                                                data-gtm="panel-cons">Consultations</a></h2>

                <#list consultations as consultation>
                    <article class="homepage-publication">
                        <h3 class="js-truncate homepage-publication__title">
                            <a href="<@hst.link hippobean=consultation />" data-gtm="cons-${consultation?index + 1}" title="${consultation.title}">${consultation.title}</a></h3>
                        <ul class="homepage-publication__topics">
                            <#list consultation.topics as topic>
                            <li>${topic.title}</li>
                            </#list>
                        </ul>
                        <p class="homepage-publication__date"><@fmt.formatDate value=consultation.displayDate.time type="both" pattern="dd MMM yyyy"/></p>
                    </article>
                </#list>

            </section>

            <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
            <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/publications/?publicationTypes=consultation-analysis;consultation-paper'/>"
            data-gtm="all-cons">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all consultations
            </a>
        </div><!--
    --></div>

    <div class="grid hidden-xsmall"><!--
        --><div class="grid__item medium--four-twelfths push--medium--eight-twelfths">
            <!-- if you're changing this link remember to also change the mobile equivalent above -->
            <a class="button  button--tertiary  tst-all-topics" href="<@hst.link path='/topics/'/>"
            data-gtm="all-topics"><svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                <use xlink:href="${iconspath}#3x3grid"></use>
            </svg>
                See all topics
            </a>
        </div><!--

        --><div class="grid__item medium--four-twelfths pull--medium--four-twelfths">
            <!-- if you're changing this link remember to also change the mobile equivalent above -->
            <a class="button  button--tertiary  tst-all-pubs" href="<@hst.link path='/publications/'/>"
            data-gtm="all-pubs">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all publications
            </a>
        </div><!--

        --><div class="grid__item medium--four-twelfths pull--medium--four-twelfths">
            <!-- if you're changing this link remember to also change the mobile equivalent above -->
            <a class="button  button--tertiary  tst-all-cons"
            href="<@hst.link path='/publications/?publicationTypes=consultation-analysis;consultation-paper'/>"
            data-gtm="all-cons">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all consultations
            </a>
        </div><!--
    --></div>

    <!-- NEWS -->
    <section id="news" class="homepage-block">
        <h2 class="emphasis homepage-block__title"><a class="homepage-block__title-link"
                                                      href="<@hst.link path='/news/'/>"
                                                      data-gtm="panel-news">News</a></h2>

        <div class="homepage-subscribe">
            <span class="hidden-xsmall">Get all the latest news from gov.scot&hellip;</span>

            <a data-gtm="link-note-sub" class="homepage-subscribe__link button  button--tertiary" href="https://eepurl.com/gEp6KP" data-gtm="news-subscribe">
                <svg class="svg-icon  mg-icon  mg-icon--absolute  mg-icon--medium--material  mg-icon--right">
                    <use xlink:href="${iconspath}#sharp-chevron_right-24px"></use>
                </svg>
                Subscribe
            </a>
        </div>

        <div class="grid"><!--

            <#list news as newsItem>
             --><div class="grid__item medium--four-twelfths homepage-news__item">
                    <article class="narrow">
                        <p class="homepage-news__date"><@fmt.formatDate value=newsItem.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                        <h3 class="js-truncate homepage-news__title">
                            <a href="<@hst.link hippobean=newsItem/>" data-gtm="news-${newsItem?index + 1}" title="${newsItem.title}">${newsItem.title}</a>
                        </h3>
                        <p class="homepage-news__summary">${newsItem.summary}</p>
                    </article>
                </div><!--
            </#list>

        --></div>

        <a class="button  button--tertiary  tst-all-news" href="<@hst.link path='/news/'/>"
           data-gtm="all-news">
            <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                <use xlink:href="${iconspath}#3x3grid"></use>
            </svg>
            See all news
        </a>
    </section>

    <!-- STATISTICS AND RESEARCH -->

    <section id="stats" class="homepage-block">
        <h2 class="emphasis homepage-block__title">
            <a class="homepage-block__title-link" href="<@hst.link path='/statistics-and-research/'/>" data-gtm="panel-stats">Statistics and research</a>
        </h2>

        <h3 class="homepage-about__title">Latest</h3>
        <div class="grid"><!--

            <#list statisticsAndResearch as statsItem>
            --><div class="grid__item medium--four-twelfths homepage-news__item">
                    <article class="narrow">
                        <h3 class="js-truncate homepage-news__title">
                            <a href="<@hst.link hippobean=statsItem/>" data-gtm="stats-${statsItem?index + 1}" title="${statsItem.title}">${statsItem.title}</a>
                        </h3>
                            <ul class="homepage-publication__topics">
                                    <li>${statsItem.label}</li>
                            </ul>

                            <p class="homepage-publication__date"><@fmt.formatDate value=statsItem.displayDate.time type="both" pattern="dd MMM yyyy"/></p>
                    </article>
                </div><!--
            </#list>

        --></div>

        <a class="button  button--tertiary tst-all-stats" href="<@hst.link path='/statistics-and-research/'/>"
        data-gtm="all-statistics">
            <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                <use xlink:href="${iconspath}#3x3grid"></use>
            </svg>
            See all Statistics and research
        </a>
    </section>

    <!-- END STATISTICS AND REASEARCH  -->







    <!-- ABOUT -->
    <section id="about" class="homepage-block homepage-about">
        <h2 class="emphasis homepage-block__title"><a class="homepage-block__title-link"
                                                      href="<@hst.link path='/about/'/>" data-gtm="panel-govt">About
            government</a></h2>

        <div class="grid"><!--
            <#if firstMinister??>
                --><div class="grid__item medium--six-twelfths homepage-about__grid-item" id="first-minister">

                    <h3 class="homepage-about__title">${firstMinister.title}</h3>

                    <div class="grid"><!--
                        --><div class="grid__item large--four-twelfths">

                            <div class="homepage-about__image-container">
                                <img alt="The First Minister" class="homepage-about__image visible-xsmall" src="<@hst.link path='/assets/images/people/first_minister_home_mob.jpg'/>"
                                    srcset="<@hst.link path='/assets/images/people/first_minister_home_mob.jpg'/> 1x, <@hst.link path='/assets/images/people/first_minister_home_mob_@2x.jpg'/> 2x"/>
                                <img alt="The First Minister" class="homepage-about__image visible-medium" src="<@hst.link path='/assets/images/people/first_minister_home_768.jpg'/>"
                                    srcset="<@hst.link path='/assets/images/people/first_minister_home_768.jpg'/> 1x, <@hst.link path='/assets/images/people/first_minister_home_768_@2x.jpg'/> 2x"/>
                                <img alt="The First Minister" class="homepage-about__image visible-large" src="<@hst.link path='/assets/images/people/first_minister_home_1024.jpg'/>"
                                    srcset="<@hst.link path='/assets/images/people/first_minister_home_1024.jpg'/> 1x, <@hst.link path='/assets/images/people/first_minister_home_1024_@2x.jpg'/> 2x"/>
                                <img alt="The First Minister" class="homepage-about__image visible-xlarge" src="<@hst.link path='/assets/images/people/first_minister_home_hd.jpg'/>"
                                    srcset="<@hst.link path='/assets/images/people/first_minister_home_hd.jpg'/> 1x, <@hst.link path='/assets/images/people/first_minister_home_hd_@2x.jpg'/> 2x"/>

                                <#--  <img alt="The First Minister" class="homepage-about__image visible-xsmall" src="<@hst.link hippobean=document.fmImageLandscape.smallcolumns/>"
                                    srcset="<@hst.link hippobean=document.fmImageLandscape.smallcolumns/> 1x, <@hst.link hippobean=document.fmImageLandscape.smallcolumnsdoubled/> 2x"/>
                                <img alt="The First Minister" class="homepage-about__image visible-medium" src="<@hst.link hippobean=document.fmImageLandscape.mediumsixcolumns/>"
                                    srcset="<@hst.link hippobean=document.fmImageLandscape.mediumsixcolumns/> 1x, <@hst.link hippobean=document.fmImageLandscape.mediumsixcolumnsdoubled/> 2x"/>
                                <img alt="The First Minister" class="homepage-about__image visible-large" src="<@hst.link hippobean=document.fmImagePortrait.largetwocolumns/>"
                                    srcset="<@hst.link hippobean=document.fmImagePortrait.largetwocolumns/> 1x, <@hst.link hippobean=document.fmImagePortrait.largetwocolumnsdoubled/> 2x"/>
                                <img alt="The First Minister" class="homepage-about__image visible-xlarge" src="<@hst.link hippobean=document.fmImagePortrait.xlargetwocolumns/>"
                                    srcset="<@hst.link hippobean=document.fmImagePortrait.xlargetwocolumns/> 1x, <@hst.link hippobean=document.fmImagePortrait.xlargetwocolumnsdoubled/> 2x"/>  -->

                            </div>
                        </div><!--

                        --><div class="grid__item large--eight-twelfths">
                            <div class="narrow">
                                <@hst.html var="firstMinisterContent" hippohtml=firstMinister.content />
                                ${firstMinisterContent?trim?keep_before("\n")}

                                <p><a class="homepage-about__read-more" data-gtm="read-more" href="<@hst.link hippobean=firstMinister/>">Read more</a></p>
                            </div>
                        </div><!--
                    --></div>
                </div><!--
            </#if>

            <#if document??>
                --><div class="grid__item medium--six-twelfths homepage-about__grid-item" id="how-gov-works">
                    <h3 class="homepage-about__title">How government works</h3>

                    <div class="leader homepage-about__leader">
                        <@hst.html hippohtml=document.howGovernmentWorks />
                    </div>
                </div><!--
            </#if>
        --></div>
    </section>

    <div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
            <#include 'common/feedback-wrapper.ftl'>
        </div><!--
    --></div>
</div>
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

<#include "../include/imports.ftl">

<header class="topic-header  {{#if contentItem.featureImage}}topic-header--has-image{{/if}}" id="page-content">
    <!--{{#if contentItem.featureImage}}-->
    <!--{{banner-image contentItem.featureImage}}-->
    <!--{{/if}}-->
    <h1 class="article-header  topic-header__title">${document.title}</h1>
</header>

<input id="topicName" type="hidden" value="{{#contentItem._embedded.topics}}{{name}}{{/contentItem._embedded.topics}}"/>

<div class="body-content  leader--first-para">
    <@hst.html hippohtml=document.content />
</div>

<ul class="policy-list column-list  column-list--not-small  column-list--3">
    <#list policies as policy>
        <li class="policy-list__item  column-list__item">
            <a class="policy-list__link" href="<@hst.link hippobean=policy/>">${policy.title}</a>
        </li>
    </#list>
</ul>

<#if document.featuredItems?has_content>
    <section id="featured-items" class="topic-block">
        <h2 class="emphasis  topic-block__title">Featured</h2>

        <ul class="grid"><!--
            <#list document.featuredItems as item>
             --><li class="grid__item  medium--four-twelfths  listed-content-item  listed-content-item--dark  listed-content-item--small">

                    <a href="{{url}}" title="{{{title}}}" class="listed-content-item__link">
                        <article class="listed-content-item__article">
                            {{feature-image featureImage}}

                            <header class="listed-content-item__heading">
                                {{#if label}}
                                <div class="listed-content-item__meta">
                                    <div class="listed-content-item__meta-right">
                                        <span class="listed-content-item__date">{{dateFormat date}}</span>
                                    </div>
                                    <div class="listed-content-item__meta-left">
                                        <p class="listed-content-item__label  js-truncate" data-lines="1">{{label}}</p>
                                    </div>
                                </div>
                                {{/if}}

                                <h3 class="listed-content-item__title  js-truncate" title="{{title}}">{{{title}}}</h3>
                            </header>

                            <p class="listed-content-item__summary  hidden-small  hidden-xsmall  js-truncate" title="{{summary}}">
                                {{{summary}}}
                            </p>
                        </article>
                    </a>

                </li><!--
            </#list>
        --></ul>
    </section>
</#if>

<div class="grid"><!--

    --><div class="grid__item  medium--four-twelfths">
    <section id="latest-news" class="topic-block">
        <h2 class="emphasis  topic-block__title">
            News
        </h2>

        <div id="news-container">
            <#if news?has_content>
                <#list news as newsItem>
                    <article class="homepage-publication">
                      <h3 class="js-truncate  homepage-publication__title">
                          <a href="<@hst.link hippobean=newsItem />" data-gtm="news-${newsItem?index}" title="${newsItem.title}">${newsItem.title}</a>
                      </h3>
                      <p class="homepage-publication__date"><@fmt.formatDate value=newsItem.publishedDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
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
                <use xlink:href="<@hst.link path='/assets/images/icons/svg/sprite.stack.svg#3x3grid'/>"></use>
            </svg>
            See all news
        </a>

    </section>
</div><!--


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
                            <a href="<@hst.link hippobean=publication />" data-gtm="news-${publication?index}" title="${publication.title}">${publication.title}</a>
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
                <use xlink:href="<@hst.link path='/assets/images/icons/svg/sprite.stack.svg#3x3grid'/>"></use>
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
                            <a href="<@hst.link hippobean=consultation />" data-gtm="news-${consultation?index}" title="${consultation.title}">${consultation.title}</a>
                        </h3>
                        <p class="homepage-publication__date"><@fmt.formatDate value=consultation.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                    </article>
                </#list>
            <#else>
                <p>There are no consultations relating to this topic.</p>
            </#if>
        </div>

        <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
        <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/publications/?topics=${document.title}&publicationTypes=consultation_paper|consultation_response'/>"
           data-gtm="all-cons">
            <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                <use xlink:href="<@hst.link path='/assets/images/icons/svg/sprite.stack.svg#3x3grid'/>"></use>
            </svg>
            See all consultations
        </a>

    </section>
</div>
</div>
<!-- /end .grid -->

<div class="topic-block  topic-block--negate-top-margin  hidden-xsmall">
    <div class="grid">
        <div class="grid__item  medium--four-twelfths">
            <!-- if you're changing this link remember to also change the mobile equivalent above -->
            <a class="button  button--tertiary  tst-all-news" href="<@hst.link path='/news/?topics=${document.title}'/>"
               data-gtm="all-news">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="<@hst.link path='/assets/images/icons/svg/sprite.stack.svg#3x3grid'/>"></use>
                </svg>
                See all news
            </a>
        </div><div class="grid__item medium--four-twelfths">
        <!-- if you're changing this link remember to also change the mobile equivalent above -->
        <a class="button  button--tertiary  tst-all-pubs" href="<@hst.link path='/publications/?topics=${document.title}'/>"
           data-gtm="all-pubs">
            <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                <use xlink:href="<@hst.link path='/assets/images/icons/svg/sprite.stack.svg#3x3grid'/>"></use>
            </svg>
            See all publications
        </a>
    </div><div class="grid__item medium--four-twelfths">
        <!-- if you're changing this link remember to also change the mobile equivalent above -->
        <a class="button  button--tertiary  tst-all-cons"
           href="<@hst.link path='/publications/?topics=${document.title}&publicationTypes=consultation_paper|consultation_response'/>"
           data-gtm="all-cons">
            <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                <use xlink:href="<@hst.link path='/assets/images/icons/svg/sprite.stack.svg#3x3grid'/>"></use>
            </svg>
            See all consultations
        </a>
    </div>
    </div>
</div>


<section class="topic-block" id="people-and-directorates">
    <h2 class="emphasis  topic-block__title">People and directorates</h2>

    <div class="expandable  topic-expandable">
        <div class="expandable-item  expandable-item--open  expandable-item--init-open" id="people-expandable">
            <button type="button" class="expandable-item__header  js-toggle-expand" tabindex="0">
                <h3 class="expandable-item__title">${document.peopleSectionTitle!"Cabinet Secretary and Ministers"}</h3>
                <span class="expandable-item__icon"></span>
            </button>

            <div class="expandable-item__body">

                <ul class="person-list grid"><!--
                    <#list roles as role>
                    --><li class="grid__item  medium--six-twelfths  person  person--small">
                            <h4 class="person__name">${role.incumbent.title}</h4>
                            <p class="person__roles"><a href="<@hst.link hippobean=role/>">${role.title}</a></p>
                        </li><!--
                    </#list>
             --></ul>
            </div>
        </div>
        <!-- /end .expandable-item -->

        <div class="expandable-item" id="directorate-expandable">
            <button type="button" class="expandable-item__header  js-toggle-expand" tabindex="0">
                <h3 class="expandable-item__title">${document.directoratesSectionTitle!"Directorates"}</h3>
                <span class="expandable-item__icon"></span>
            </button>

            <div class="expandable-item__body">

                <ul class="directorate-list  column-list  column-list--not-small  column-list--2">
                    <#list directorates as directorate>
                        <li class="directorate-list__item  column-list__item">
                            <a class="directorate-list__link" href="<@hst.link hippobean=directorate/>">${directorate.title}</a>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
        <!-- /end .expandable-item -->

    </div>
    <!-- /end .expandable -->

</section>

<@hst.headContribution category="footerScripts">
    <script src="<@hst.webfile path="/assets/scripts/topic.js"/>" type="text/javascript"></script>
</@hst.headContribution>

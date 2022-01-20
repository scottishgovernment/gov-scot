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

                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=publication.displayDate.time type="both" pattern="dd MMM yyyy"/></p>
                                    </article>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/'/>" data-gtm="all-pubs">
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

                                        <p class="gov_latest-feed__item__date"><@fmt.formatDate value=consultation.displayDate.time type="both" pattern="dd MMM yyyy"/></p>
                                    </article>
                                </#list>
                            </#if>
                        </div>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/consultations/'/>" data-gtm="all-cons">
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
                                data-gtm="panel-pols">
                                Topics
                            </a>
                        </h2>

                        <p class="gov_content-block__intro">Look for a policy using keywords or filtering by available topic areas.</p>

                        <form class="gov_filters  ds_no-margin--bottom">

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

                            <fieldset>
                                <legend class="gov_content-block__subtitle">Filter by topic</legend>

                                <div class="gov_filters__scrollable  gov_filters__scrollable--fixed">
                                    <div class="ds_field-group--checkboxes">
    <div class="ds_checkbox  ds_checkbox--small">
        <input id="arts-culture-and-sport" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Arts, culture and sport" data-form="checkbox-arts-culture-and-sport">
        <label for="arts-culture-and-sport" class="ds_checkbox__label">Arts, culture and sport</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="building-planning-and-design" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Building, planning and design" data-form="checkbox-building-planning-and-design">
        <label for="building-planning-and-design" class="ds_checkbox__label">Building, planning and design</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="business-industry-and-innovation" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Business, industry and innovation" data-form="checkbox-business-industry-and-innovation">
        <label for="business-industry-and-innovation" class="ds_checkbox__label">Business, industry and innovation</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="children-and-families" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Children and families" data-form="checkbox-children-and-families">
        <label for="children-and-families" class="ds_checkbox__label">Children and families</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="communities-and-third-sector" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Communities and third sector" data-form="checkbox-communities-and-third-sector">
        <label for="communities-and-third-sector" class="ds_checkbox__label">Communities and third sector</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="constitution-and-democracy" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Constitution and democracy" data-form="checkbox-constitution-and-democracy">
        <label for="constitution-and-democracy" class="ds_checkbox__label">Constitution and democracy</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="economy" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Economy" data-form="checkbox-economy">
        <label for="economy" class="ds_checkbox__label">Economy</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="education" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Education" data-form="checkbox-education">
        <label for="education" class="ds_checkbox__label">Education</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="energy" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Energy" data-form="checkbox-energy">
        <label for="energy" class="ds_checkbox__label">Energy</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="environment-and-climate-change" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Environment and climate change" data-form="checkbox-environment-and-climate-change">
        <label for="environment-and-climate-change" class="ds_checkbox__label">Environment and climate change</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="equality-and-rights" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Equality and rights" data-form="checkbox-equality-and-rights">
        <label for="equality-and-rights" class="ds_checkbox__label">Equality and rights</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="farming-and-rural" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Farming and rural" data-form="checkbox-farming-and-rural">
        <label for="farming-and-rural" class="ds_checkbox__label">Farming and rural</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="health-and-social-care" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Health and social care" data-form="checkbox-health-and-social-care">
        <label for="health-and-social-care" class="ds_checkbox__label">Health and social care</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="housing" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Housing" data-form="checkbox-housing">
        <label for="housing" class="ds_checkbox__label">Housing</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="international" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="International" data-form="checkbox-international">
        <label for="international" class="ds_checkbox__label">International</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="law-and-order" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Law and order" data-form="checkbox-law-and-order">
        <label for="law-and-order" class="ds_checkbox__label">Law and order</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="marine-and-fisheries" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Marine and fisheries" data-form="checkbox-marine-and-fisheries">
        <label for="marine-and-fisheries" class="ds_checkbox__label">Marine and fisheries</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="money-and-tax" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Money and tax" data-form="checkbox-money-and-tax">
        <label for="money-and-tax" class="ds_checkbox__label">Money and tax</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="public-safety-and-emergencies" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Public safety and emergencies" data-form="checkbox-public-safety-and-emergencies">
        <label for="public-safety-and-emergencies" class="ds_checkbox__label">Public safety and emergencies</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="public-sector" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Public sector" data-form="checkbox-public-sector">
        <label for="public-sector" class="ds_checkbox__label">Public sector</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="transport" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Transport" data-form="checkbox-transport">
        <label for="transport" class="ds_checkbox__label">Transport</label>
    </div>


    <div class="ds_checkbox  ds_checkbox--small">
        <input id="work-and-skills" name="topics[]" class="ds_checkbox__input js-has-tracking-event" type="checkbox" value="Work and skills" data-form="checkbox-work-and-skills">
        <label for="work-and-skills" class="ds_checkbox__label">Work and skills</label>
    </div>


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
                                    </div>
                                </div>
                            </fieldset>

                            <button data-href="<@hst.link path='/policies/' />" class="js-policy-form-submit  ds_button  ds_button--fixed  ds_button--has-icon  ds_no-margin--bottom">
                                Search policies
                                <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#chevron_right"></use></svg>
                            </button>
                        </form>
                    </div>

                    <div>
                        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/topics/'/>" data-gtm="all-topics">
                            <span class="gov_icon-link__text">See all topics</span>
                            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                        </a>
                    </div>
                </section>

                <div>
                    <section id="latest-news" class="gov_latest-feed  gov_content-block">
                        <div>
                            <h2 class="gov_content-block__title">
                                <a class="gov_content-block__title-link"
                                    href="<@hst.link path='/news/'/>"
                                    data-gtm="panel-news">
                                    News
                                </a>
                            </h2>

                            <div class="gov_homepage-subscribe">
                                <span class="gov_homepage-subscribe__text">Get all the latest news from gov.scot</span>

                                <a class="gov_homepage-subscribe__link  gov_icon-link" href="http://register.scotland.gov.uk/Subscribe/Step1">
                                    <span class="gov_icon-link__text">Subscribe</span>
                                    <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
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
                            <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/news/'/>" data-gtm="all-news">
                                <span class="gov_icon-link__text">See all news</span>
                                <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
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
                                            <p class="gov_latest-feed__item__date"><@fmt.formatDate value=publication.displayDate.time type="both" pattern="dd MMM yyyy"/></p>
                                        </article>
                                    </#list>
                                </#if>
                            </div>
                        </div>

                        <div>
                            <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/statistics-and-research/'/>" data-gtm="all-stats">
                                <span class="gov_icon-link__text">See all Statistics and research</span>
                                <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                            </a>
                        </div>
                    </section>
                </div>
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
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" path="/" canonical=true/>
    <#include "common/canonical.ftl" />
</#if>

<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="body-content  ds_leader-first-paragraph">
    <#if document.summary?has_content>
        <p>${document.summary}</p>
    </#if>
</div>

<#if document.image??>
    <header class="gov_topic-header  <#if document.image??>gov_topic-header--has-image</#if>" id="page-content">
        <img alt="" src="<@hst.link hippobean=document.image.xlargetwelvecolumnsdoubledfourone/>" class="gov_topic-header__image">
    </header>
</#if>

<div>
    <#if document.content?has_content>
        <@hst.html hippohtml=document.content />
    </#if>

    <#if document.additionalContent?has_content>
        <section id="additional-content" class="gov_content-block">
            <#list document.additionalContent as additionalContent>
                <h2 class="gov_content-block__title">${additionalContent.title}</h2>
                <@hst.html hippohtml=additionalContent.body />
            </#list>
        </section>
    </#if>

    <section id="latest-stats-research" class="gov_content-block">
        <h2 class="gov_content-block__title">
            Statistics and research publications
        </h2>

        <div class="gov_sublayout  gov_sublayout--twocols">
            <section id="latest-publications" class="gov_latest-feed">
                <div>
                    <#if statisticsAndResearch?has_content>
                        <h3>Latest</h3>

                        <div class="gov_latest-feed__items">
                            <#list statisticsAndResearch as statsItem>
                                <article class="gov_latest-feed__item">
                                    <h3 class="gov_latest-feed__item__title">
                                        <a href="<@hst.link hippobean=statsItem />" data-navigation="statistics-${statsItem?index + 1}">${statsItem.title}</a>
                                    </h3>

                                    <ul class="gov_latest-feed__item__topics">
                                        <li>${statsItem.label}</li>
                                    </ul>

                                    <p class="gov_latest-feed__item__date"><@fmt.formatDate value=statsItem.displayDate.time type="both" pattern="dd MMMM yyyy"/></p>
                                </article>
                            </#list>
                        </div>

                    <#else>
                        <p>There are no statistics and research relating to this topic.</p>
                    </#if>
                </div>

                <div>
                    <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/'/>" data-navigation="statistics-all">
                        <span class="gov_icon-link__text">See all Statistics and research</span>
                        <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                    </a>
                </div>

            </section>

            <div class="gov_content-block  gov_latest-feed">
                <div>
                    <h3>Search by type</h3>
                    <p>Look for either statistics or research that have been published by the Scottish Government.</p>

                    <form class="gov_filters  ds_no-margin--bottom">
                        <div class="ds_field-group--checkboxes">
                            <fieldset>
                                <legend class="visually-hidden">Publication type</legend>
                                <div class="ds_checkbox  ds_checkbox--small">
                                    <input id="research" name="pubtype[]" class="ds_checkbox__input" type="checkbox" value="research-and-analysis"/>
                                    <label for="research" class="ds_checkbox__label">Research and analysis</label>
                                </div>
                                <div class="ds_checkbox  ds_checkbox--small">
                                    <input id="statistics" name="pubtype[]" class="ds_checkbox__input" type="checkbox" value="statistics"/>
                                    <label for="statistics" class="ds_checkbox__label">Statistics</label>
                                </div>
                            </fieldset>
                        </div>

                        <button class="js-stats-form-submit  ds_button  ds_button--fixed  ds_button--has-icon  ds_no-margin--bottom" data-button="button-search-stats">
                            Search
                            <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#chevron_right"></use></svg>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </section>
</div>

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
                                <div class="ds_aspect-box">
                                    <img alt="" class="ds_aspect-box__inner" src="/assets/images/graphics/featured-${imgLabel}-hd.jpg" width="1600" height="900"
                                        srcset="<@hst.link path='/assets/images/graphics/featured-${imgLabel}-tablet.jpg'/>  220w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-tablet_@2x.jpg'/> 440w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop.jpg'/> 293w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop_@2x.jpg'/> 586w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-hd.jpg'/> 360w,
                                            <@hst.link path='/assets/images/graphics/featured-${imgLabel}-hd_@2x.jpg'/> 720w"
                                        sizes="(min-width:1200px) 360px, (min-width:992px) 293px, (min-width:768px) 220px, 360px"
                                    />
                                </div>
                            </div>
                        </#if>

                        <article class="ds_card__content">

                            <#if item.label?has_content>
                                <#assign date = (item.publicationDate.time)!item.properties['hippostdpubwf:lastModificationDate'].time />

                                <dl class="ds_metadata  gov_featured-item__metadata">
                                    <div class="ds_metadata__item">
                                        <dt class="ds_metadata__key  visually-hidden">Type</dt>
                                        <dd class="ds_metadata__value  ds_content-label">${item.label}</dd>
                                    </div>

                                    <div class="ds_metadata__item">
                                        <dt class="ds_metadata__key  visually-hidden">Publication date</dt>
                                        <dd class="ds_metadata__value"><@fmt.formatDate value=date type="both" pattern="dd MMMM yyyy"/></dd>
                                    </div>
                                </dl>
                            </#if>

                            <h3 class="ds_card__title">
                                <a data-navigation="featured-${item?index + 1}" href="<@hst.link hippobean=item/>" class="ds_card__link--cover">
                                    ${item.title}
                                </a>
                            </h3>

                            <p>
                                ${item.summary}
                            </p>
                        </article>
                    </div>
                </#list>
            </div>

        </section>
    </div>
</#if>


 <#if document.trailingContent?has_content>
    <section id="trailing-content" class="topic-block">
        <#list document.trailingContent as trailingContent>
            <h2 class="gov_content-block__title">${trailingContent.title}</h2>
            <@hst.html hippohtml=trailingContent.body />
        </#list>
    </section>
</#if>

<@hst.headContribution category="footerScripts">
<script type="module" src="<@hst.webfile path="/assets/scripts/aboutstats.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
<script nomodule="true" src="<@hst.webfile path="/assets/scripts/aboutstats.es5.js"/>"></script>
</@hst.headContribution>

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

    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=lastUpdated.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
    </@hst.headContribution>
</#if>

<#ftl output_format="HTML">
<#include "../../include/imports.ftl">


<#if showNews || showPolicies || showPublications>

<div class="ds_cb  ds_cb--link-list">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document??>
                <#if showNews && news?has_content>
                    <section class="gov_latest-feed  gov_latest-feed--horizontal  gov_content-block">
                        <div>
                            <h2>
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
                                </#if>
                            </div>
                        </div>

                        <div>
                            <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/news/'/>?cat=filter&topics=${document.title}" data-navigation="news-all">
                                <span class="gov_icon-link__text">See all news</span>
                                <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                            </a>
                        </div>
                    </section>
                </#if>

                <#if showPublications && publications?has_content>
                    <section class="gov_latest-feed  gov_latest-feed--horizontal  gov_content-block">
                        <div>
                            <h2>
                                Publications
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
                            <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/'/>?cat=filter&topics=${document.title}" data-navigation="publications-all">
                                <span class="gov_icon-link__text">See all publications</span>
                                <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                            </a>
                        </div>
                    </section>
                </#if>

                <#if showPolicies && policies?has_content>
                    <section class="gov_latest-feed  gov_latest-feed--horizontal  gov_content-block">
                        <div>
                            <h2>
                                Policies
                            </h2>

                            <div class="gov_latest-feed__items">
                                <#if policies?has_content>
                                    <#list policies as policy>
                                        <article class="gov_latest-feed__item">
                                            <h3 class="gov_latest-feed__item__title">
                                                <a href="<@hst.link hippobean=policy />" data-navigation="pollicies-${policy?index + 1}">${policy.title}</a>
                                            </h3>

                                            <ul class="gov_latest-feed__item__topics">
                                                <#list policy.topics as topic>
                                                    <li>${topic.title}</li>
                                                </#list>
                                            </ul>
                                        </article>
                                    </#list>
                                </#if>
                            </div>
                        </div>

                        <div>
                            <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/policies/'/>?cat=filter&topics=${document.title}" data-navigation="policies-all">
                                <span class="gov_icon-link__text">See all policies</span>
                                <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
                            </a>
                        </div>
                    </section>
                </#if>

                <@hst.manageContent hippobean=document documentTemplateQuery="new-text-document" parameterName="document" rootPath="content-blocks/text"/>
            <#elseif editMode>
                <@hst.manageContent documentTemplateQuery="new-dynamic-issue-document" parameterName="issue" rootPath="content-blocks/text"/>
            </#if>
        </div>
    </div>
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

    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=lastUpdated.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
    </@hst.headContribution>

    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <#include "../common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "../common/canonical.ftl" />

    <#include "../common/gtm-datalayer.ftl"/>
</#if>

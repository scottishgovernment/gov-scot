<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>

<div class="layout--issue">

    <@hst.manageContent hippobean=document />

    <header class="topic-header  <#if document.image??>topic-header--has-image</#if>" id="page-content">
        <#if document.image??>
            <img alt="" src="<@hst.link hippobean=document.image.bannerdesktop/>" class="topic-header__image">
        </#if>

        <h1 class="article-header  topic-header__title">${document.title}</h1>

        <#if document.featureDateTitle?has_content ||
        document.featureDate?has_content ||
        document.featureDateSummary?has_content >
        <aside class="issue-callout">
            <div class="issue-callout__inner">
                <#if document.featureDateTitle?has_content><h3 class="issue-callout__beta">${document.featureDateTitle}</h3></#if>
                <#if document.featureDate?has_content><div class="issue-callout__alpha"><@fmt.formatDate value=document.featureDate.time type="both" pattern="d MMM yyyy"/></div></#if>
                <#if document.featureDateSummary?has_content><div class="issue-callout__gamma">${document.featureDateSummary}</div></#if>
            </div>
        </aside>
        </#if>

    </header>

    <div class="body-content">
        <div class="grid"><!--
         --><div class="grid__item  medium--eight-twelfths">

                <div id="introduction">
                    <@hst.html hippohtml=document.content/>
                </div>

                <div id="overview">
                    <@hst.html hippohtml=document.overview/>
                </div>

            </div><!--

         --><div class="grid__item  medium--four-twelfths">
                <div <#if document.featureDateTitle?has_content ||
                    document.featureDate?has_content ||
                    document.featureDateSummary?has_content >class="displace-by-issue-callout"</#if>>
                    <#if document.featuredItems?has_content>
                        <section id="featured-items">

                            <h2 class="crossbar-header"><span class="crossbar-header__text"><#if document.featuredItemsTitle?has_content>${document.featuredItemsTitle}<#else>Featured</#if></span></h2>

                            <ul class="no-bullets">
                                <#list document.featuredItems as item>
                                    <li class="listed-content-item  listed-content-item--compact  listed-content-item--highlight">
                                        <article class="listed-content-item__article ">
                                            <header class="listed-content-item__header">
                                                <div class="listed-content-item__meta">
                                                    <#if item.label??><p class="listed-content-item__label">${item.label}</p></#if>

                                                    <#if item.label == 'news'>
                                                        <p class="listed-content-item__date"><@fmt.formatDate value=item.publicationDate.time type="both" pattern="d MMM yyyy HH:mm"/></p>
                                                    <#elseif item.class == 'scot.gov.www.beans.ExternalLink'>
                                                        <p class="listed-content-item__date">${document.url}</p>
                                                    <#elseif item.publicationDate??>
                                                        <p class="listed-content-item__date"><@fmt.formatDate value=item.displayDate.time type="both" pattern="d MMM yyyy"/></p>
                                                    </#if>
                                                </div>

                                                <h3 class="gamma  listed-content-item__title" title="${item.title}">
                                                    <a data-gtm="featured-item-${item?index + 1}" href="<#if item.class == 'scot.gov.www.beans.ExternalLink'>${item.url}<#else><@hst.link hippobean=item/></#if>" class="listed-content-item__link <#if item.class == 'scot.gov.www.beans.ExternalLink'>external  listed-content-item__link--external</#if>" title="${item.title}">
                                                        ${item.title}
                                                    </a>
                                                </h3>
                                            </header>
                                        </article>
                                    </li>
                                </#list>
                            </ul>
                        </section>
                    </#if>

                    <div>
                    <#if news?has_content>
                        <section class="sidebar-block" id="related-news">
                            <h2 class="gamma  emphasis  sidebar-block__title">News</h2>
                            <ul class="no-bullets">
                                <#list news as newsItem>
                                    <li><a data-gtm="news-${newsItem?index + 1}" class="sidebar-block__link" href="<@hst.link hippobean=newsItem/>">${newsItem.title}</a></li>
                                </#list>
                            </ul>
                            <a href="<@hst.link path='/news/?topics=' + document.title/>" class="see-all-button  see-all-button--icon  see-all-button--icon-grid"><span></span> See all news</a>
                        </section>
                    </#if>

                    <#if policies?has_content>
                        <section class="sidebar-block" id="related-policies">
                            <h2 class="gamma  emphasis  sidebar-block__title">Policies</h2>
                            <ul class="no-bullets">
                                <#list policies as policy>
                                    <li><a data-gtm="policy-${policy?index + 1}" class="sidebar-block__link" href="<@hst.link hippobean=policy/>">${policy.title}</a></li>
                                </#list>
                            </ul>
                            <a href="<@hst.link path='/policies/?topics=' + document.title/>" class="see-all-button  see-all-button--icon  see-all-button--icon-grid"><span></span> See all policies</a>
                        </section>
                    </#if>

                    <#if publications?has_content>
                        <section class="sidebar-block" id="related-publications">
                            <h2 class="gamma  emphasis  sidebar-block__title">Publications</h2>
                            <ul class="no-bullets">
                                <#list publications as publication>
                                    <li><a data-gtm="publications-${publication?index + 1}" class="sidebar-block__link" href="<@hst.link hippobean=publication/>">${publication.title}</a></li>
                                </#list>
                            </ul>
                            <a href="<@hst.link path='/publications/?topics=' + document.title/>" class="see-all-button  see-all-button--icon  see-all-button--icon-grid"><span></span> See all publications</a>
                        </section>
                    </#if>
                    </div>
                </div>
            </div><!--
     --></div>
    </div>


    <div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
            <#include 'common/feedback-wrapper.ftl'>
        </div><!--
    --></div>
</div>

</div>

</#if>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/issue-hub.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/issue-hub.es5.js"/>"></script>
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
    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Issue"/>
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

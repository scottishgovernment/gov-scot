<#include "../include/imports.ftl">
<!--
h h h h h h h h h h h h
m m m S S S S S S S S S (S = summary, m = metadata)
s s s c c c c c c c c c
. . . f f f f f f f f f

? replace sidebar with contents nav
-->
<#if document??>
todo: make a layout for collections instead of just using ds_layout--article. it might be possible to do a publications layout that includes collections.
<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  gov_layout--collection">
        <div class="ds_layout__header">
            <header class="ds_page-header  gov_sublayout  gov_sublayout--publication-header">
                <div class="gov_sublayout__title">
                    <span class="ds_page-header__label  ds_content-label">Collection</span>
                    <h1 class="ds_page-header__title">${document.title?html}</h1>
                </div>

                <div class="gov_sublayout__metadata">
                    <#assign index=document/>
                    <#include 'common/content-data.ftl'/>
                </div>

                <div class="gov_sublayout__content">
                    <#if document.summary??>
                        <#list document.summary?split("\n") as summaryParagraph>
                            <p class="ds_leader">${summaryParagraph}</p>
                        </#list>
                    </#if>
                </div>
            </header>
        </div>

        <div class="ds_layout__sidebar">
            <nav role="navigation" class="ds_contents-nav" aria-label="Sections">
                <h2 class="gamma">On this page</h2>

                <ul class="ds_contents-nav__list">
                    <#list document.groups as group>
                        <li class="ds_contents-nav__item">
                            <a href="#${group.groupTitle?lower_case?replace(" ","")}" class="ds_contents-nav__link" data-navigation-index="${group?index}">${group.groupTitle}</a>
                        </li>
                    </#list>
                </ul>
            </nav>
        </div>

        <div class="ds_layout__content">
            <@hst.html hippohtml=document.content var="content"/>
            <#if content?has_content>
                <h2 class="gamma">Introduction</h2>
                ${content}
            </#if>

            <#list document.groups as group>
                <h2 class="gamma" id="${group.groupTitle?lower_case?replace(" ","")}">${group.groupTitle}</h2>

                <@hst.html hippohtml=group.description var="description"/>
                <#if description?has_content>
                    ${description}
                </#if>

                <ul class="collections-list">
                    <#list group.collectionItems as item>
                        <#if group.highlight == true && item?index == 0>
                            <li class="listed-content-item  listed-content-item--highlight  listed-content-item--compact">
                                <article class="listed-content-item__article ">
                                    <header class="listed-content-item__header">
                                        <div class="listed-content-item__meta">
                                            <span class="listed-content-item__label">${item.label}</span>

                                            <#if item.publicationDate??>
                                                <#assign dateFormat = "dd MMM yyyy">
                                                <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                                                    <#assign dateFormat = "dd MMM yyyy HH:mm">
                                                </#if>
                                                <span class="listed-content-item__date">| <@fmt.formatDate value=item.publicationDate.time type="both" pattern=dateFormat /></span>
                                            </#if>
                                        </div>

                                        <h3 class="gamma  listed-content-item__title" title="${item.title}">
                                            <a href="<@hst.link hippobean=item/>" class="listed-content-item__link" title="${item.title}">${item.title}</a>
                                        </h3>
                                    </header>
                                    <p class="listed-content-item__summary">${item.summary}</p>
                                </article>
                            </li>
                        <#else>
                            <#if item.title??>
                                <li><a href="<@hst.link hippobean=item/>">${item.title}</a></li>
                            </#if>
                        </#if>
                    </#list>
                </ul>
            </#list>

            <@hst.html hippohtml=document.contact var="contact"/>
            <#if contact?has_content>
                <div class="publication-info__contact">
                    <h3 class="emphasis">Contact</h3>
                ${contact}
                </div>
            </#if>
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>



    <@hst.headContribution category="pageTitle">
    <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>

    <#if document.metaDescription??>
        <meta name="description" content="${document.metaDescription?html}"/>
    </#if>

    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>

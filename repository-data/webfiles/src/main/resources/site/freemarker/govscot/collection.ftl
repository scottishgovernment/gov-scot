<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content">
        <@hst.manageContent hippobean=document/>
        <header class="article-header">
            <div class="grid"><!--
             --><div class="grid__item">
                    <p class="article-header__label">Collection</p>
                    <h1 class="article-header__title">${document.title}</h1>
                </div><!--

             --><div class="grid__item medium--three-twelfths">
                    <section class="content-data">
                        <#assign index=document/>
                        <#include 'common/content-data.ftl'/>
                    </section>
                </div><!--

             --><div class="grid__item medium--nine-twelfths large--seven-twelfths">
                    <#if document.summary??>
                        <#list document.summary?split("\n") as summaryParagraph>
                            <p class="leader">${summaryParagraph}</p>
                        </#list>
                    </#if>
                </div><!--
         --></div>
        </header>
        <hr>

        <div class="grid"><!--
         --><div class="grid__item medium--three-twelfths">
                <nav>
                    <h2 class="gamma">On this page:</h2>
                    <ul class="no-left-margin">
                        <#list document.groups as group>
                            <li><a href="#${group.groupTitle?lower_case?replace(" ","")}" data-navigation-index="${group?index}">${group.groupTitle}</a></li>
                        </#list>
                    </ul>
                </nav>
                <hr class="visible-xsmall visible-small">
            </div><!--

         --><div class="grid__item medium--nine-twelfths large--seven-twelfths">

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

                    <#if group.order>
                        <#assign collectionItems = group.collectionItems?sort_by('publicationDate')?reverse/>
                    <#else>
                        <#assign collectionItems = group.collectionItems/>
                    </#if>

                    <ul class="collections-list">
                        <#list collectionItems as item>
                            <#if group.highlight == true && item?index == 0>
                                <li class="listed-content-item listed-content-item--highlight listed-content-item--compact">
                                    <a href="<@hst.link hippobean=item/>" class="listed-content-item__link" title="${item.title}">
                                        <article class="listed-content-item__article ">
                                            <header class="listed-content-item__header">
                                                <div class="listed-content-item__meta">
                                                    <span class="listed-content-item__label">${item.label}</span>

                                                    <span class="listed-content-item__date">| <@fmt.formatDate value=item.publicationDate.time type="both" pattern="d MMM yyyy HH:mm"/></span>
                                                </div>

                                                <h3 class="gamma  listed-content-item__title" title="${item.title}">${item.title}</h3>
                                            </header>
                                            <p class="listed-content-item__summary">${item.summary}</p>
                                        </article>
                                    </a>
                                </li>
                            <#else>
                                <li><a href="<@hst.link hippobean=item/>">${item.title}</a></li>
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
            </div><!--
     --></div>

    </article>

    <div class="grid"><!--
     --><div class="grid__item  push--medium--three-twelfths  medium--eight-twelfths  large--seven-twelfths">
            <#include 'common/feedback-wrapper.ftl'>
        </div><!--
 --></div>

    <@hst.headContribution category="pageTitle">
    <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
    <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/collection.js"/>"></script>
    </@hst.headContribution>
    <@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/collection.es5.js"/>"></script>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
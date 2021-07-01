<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">

<#if document??>
</div>

    <@hst.manageContent hippobean=document/>
    <@hst.link hippobean=document var="baseurl" canonical=true/>

<article id="page-content" class="layout--publication">

<#--------------------- HEADER SECTION --------------------->
    <div class="top-matter">
        <div class="wrapper">
            <header class="article-header no-bottom-margin">
                    <div class="grid"><!--
                     --><div class="grid__item large--ten-twelfths">
                        <p class="article-header__label">Publication - ${document.label}</p>
                        <h1 class="article-header__title">${document.title}</h1>
                    </div><!--
                 --></div>
                    <div class="grid"><!--
                     --><div class="grid__item  large--three-twelfths">
                        <#include 'metadata.ftl'/>
                    </div><!--

                     --><div class="grid__item  large--seven-twelfths">
                        <div class="leader  leader--first-para">
                            <#if document.summary??>
                                <#list document.summary?split("\n") as summaryParagraph>
                                    <p>${summaryParagraph}</p>
                                </#list>
                            </#if>

                            <#include '../common/collections-list.ftl'/>
                        </div>
                    </div><!--
                 --></div>

            </header>
        </div>
    </div>

    <#--------------------- BODY SECTION --------------------->
    <div class="wrapper js-content-wrapper">
        <div class="body-content publication-body">

            <div class="grid"><!--

                --><div class="grid__item large--eight-twelfths">

                    <h2>Supporting documents</h2>

                    <#list documents as attachedDocument>
                        <#assign isTargetedItem = doc == attachedDocument/>
                        <#assign isHighlightedItem = attachedDocument?is_first/>
                        <#include 'body-document-info.ftl'/>
                    </#list>


                    <div class="grid  page-nav"><!--
                         --><div class="grid__item  medium--six-twelfths  page-nav__item">
                                <a href="${baseurl}" class="page-nav__button  page-nav__button--left">
                                    <span data-label="Return" class="page-nav__text">Main publication</span>
                                </a>
                            </div><!--
                     --></div>
                </div><!--

            --></div>
        </div>
    </div>
</article>

<div class="wrapper">
    <div class="grid"><!--
        --><div class="grid__item  large--eight-twelfths">
        <#include '../common/feedback-wrapper.ftl'>
    </div><!--
    --></div>
</div>

</#if>

<#include "../common/schema.article.ftl"/>

<@hst.headContribution category="footerScripts">
<script type="module" src="<@hst.webfile path="/assets/scripts/publication.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
<script nomodule="true" src="<@hst.webfile path="/assets/scripts/publication.es5.js"/>"></script>
</@hst.headContribution>

<#if document??>
    <@hst.headContribution category="pageTitle">
    <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>

        <#if document.metaDescription??>
        <meta name="description" content="${document.metaDescription?html}"/>
        </#if>
    </@hst.headContribution>

    <#if isMultiPagePublication && (currentPage != pages[0])>
        <@hst.link var="canonicalitem" hippobean=currentPage canonical=true/>
        <#assign uuid = currentPage.getProperty('jcr:uuid')/>
        <#assign lastUpdated = currentPage.getProperty('hippostdpubwf:lastModificationDate')/>
        <#assign dateCreated = currentPage.getProperty('hippostdpubwf:creationDate')/>
        <#include "../common/gtm-datalayer.ftl"/>
    <#else>
        <@hst.link var="canonicalitem" hippobean=document canonical=true/>
        <#include "../common/gtm-datalayer.ftl"/>
    </#if>

    <#include "../common/canonical.ftl" />
</#if>

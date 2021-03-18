<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">

<#if document??>
</div>

    <@hst.manageContent hippobean=document/>

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
    <div class="inner-shadow-top  js-sticky-header-position">

        <div class="wrapper js-content-wrapper">
            <div class="body-content publication-body">

                <div class="grid"><!--

             --><div class="grid__item  medium--four-twelfths  large--three-twelfths hidden-xsmall  hidden-small  hidden-medium">
                </div><!--

                 --><div class="grid__item large--seven-twelfths">

                    <#list documents as attachedDocument>
                        <#if doc = attachedDocument>
                            <h2>THIS ONE</h2>
                        </#if>
                        <#include 'body-document-info.ftl'/>
                    </#list>
                    <#--<#assign attachedDocument=doc/>-->
                    <#--<#include 'body-document-info.ftl'/>-->

                </div><!--

         --></div>
            </div>
        </div>
    </div>
</article>

<div class="wrapper">
    <div class="grid"><!--
        --><div class="grid__item  large--seven-twelfths  push--large--three-twelfths">
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

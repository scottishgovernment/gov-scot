<#include "../include/imports.ftl">

<#if document??>

    <@hst.manageContent hippobean=document/>

    <article id="page-content" class="layout--site-item">

        <div class="grid"><!--
         --><div class="grid__item medium--nine-twelfths large--seven-twelfths">
                <h1 class="article-header">${document.title}</h1>

                <div class="body-content  leader--first-para">
                    <@hst.html hippohtml=document.content/>

                    <@hst.html hippohtml=document.additionalContent/>
                </div>
            </div><!--
     --></div>

    </article>

    <div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
            <#include 'common/feedback-wrapper.ftl'>
        </div><!--
    --></div>
</#if>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>

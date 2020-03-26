<#include "../include/imports.ftl">

<article id="page-content" class="layout--directorates-list">

    <div class="grid"><!--
--><div class="grid__item medium--three-twelfths large--three-twelfths">
            <@hst.include ref="side-menu"/>
        </div><!--


        --><div class="grid__item medium--nine-twelfths large--seven-twelfths">
            <h1>Directorates</h1>

            <#include 'common/a-z-list.ftl' />
        </div><!--

     
    --></div>
</article>

<@hst.headContribution category="pageTitle"><title>Directorates - gov.scot</title></@hst.headContribution>
<#if document??>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>
</#if>

<@hst.link var="canonicalitem" siteMapItemRefId="directorates" canonical=true/>
<#include "common/canonical.ftl">

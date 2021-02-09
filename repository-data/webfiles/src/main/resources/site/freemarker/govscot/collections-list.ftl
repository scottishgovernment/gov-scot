<#include "../include/imports.ftl">

<article id="page-content" class="layout--groups-list">

    <div class="grid"><!--
        --><div class="grid__item medium--nine-twelfths large--seven-twelfths">
        <h1>Collections</h1>

    <#include 'common/a-z-list.ftl' />
    </div><!--
    --></div>
</article>

<@hst.headContribution category="pageTitle"><title>Collections - gov.scot</title></@hst.headContribution>
<#if document??>
    <@hst.headContribution>
    <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>
</#if>

<@hst.link var="canonicalitem" siteMapItemRefId="collections" canonical=true/>
<#include "common/canonical.ftl" />

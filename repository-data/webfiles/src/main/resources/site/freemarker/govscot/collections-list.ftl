<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  ds_layout--article">
        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title">Collections</h1>
            </header>
        </div>

        <div class="ds_layout__content">
            <#include 'common/a-z-list.ftl' />
        </div>
    </main>
</div>

<@hst.headContribution category="pageTitle"><title>Collections - gov.scot</title></@hst.headContribution>
<#if document??>
    <@hst.headContribution>
    <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>
</#if>

<@hst.link var="canonicalitem" siteMapItemRefId="collections" canonical=true/>
<#include "common/canonical.ftl" />

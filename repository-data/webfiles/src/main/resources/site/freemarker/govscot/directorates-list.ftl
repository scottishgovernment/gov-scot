<#include "../include/imports.ftl">

<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  gov_layout--about">
        <div class="ds_layout__sidebar">
            <@hst.include ref="side-menu"/>
        </div>

        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title">Directorates</h1>
            </header>
        </div>

        <div class="ds_layout__content">
            <#include 'common/a-z-list.ftl' />
        </div>
    </main>
</div>

<@hst.headContribution category="pageTitle"><title>Directorates - gov.scot</title></@hst.headContribution>
<#if document??>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>
</#if>

<@hst.link var="canonicalitem" siteMapItemRefId="directorates" canonical=true/>
<#include "common/canonical.ftl">

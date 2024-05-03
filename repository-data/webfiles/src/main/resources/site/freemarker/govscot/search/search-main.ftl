<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#if document??>
<div class="cms-editable">
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <@hst.html hippohtml=document.content/>

                <#if autoCompleteEnabled>
                    <#assign ds_autocomplete = true />
                </#if>
                <#assign searchpagepath = hstRequestContext.servletRequest.pathInfo />

                <#include 'search.ftl'/>

                <section id="search-results" class="ds_search-results">
                    <@hst.include ref="results"/>
                </section>
            </div>

            <div class="ds_layout__feedback">
                <#include '../common/feedback-wrapper.ftl'>
            </div>
        </main>
    </div>
</div>

<@hst.headContribution category="pageTitle">
    <title>${document.title} - gov.scot</title>
</@hst.headContribution>

</#if>

<@hst.headContribution category="footerScripts">
    <script type="module" src='<@hst.webfile path="assets/scripts/search-page.js"/>'></script>
</@hst.headContribution>

<@hst.headContribution category="footerScripts">
    <script nomodule="true" src='<@hst.webfile path="assets/scripts/search-page.es5.js"/>'></script>
</@hst.headContribution>
<#include "../common/gtm-datalayer.ftl"/>

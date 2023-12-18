<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#-- @ftlvariable name="index" type="scot.gov.www.beans.SimpleContent" -->
<#if index??>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  gov_layout--filter-search">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${index.title}</h1>
                </header>

                <#if index.content.content?has_content>
                    <@hst.html hippohtml=index.content/>
                </#if>
            </div>

            <div class="ds_layout__sidebar">
                <@hst.include ref="side-filter"/>
            </div>

            <div class="ds_layout__content">
                <@hst.include ref="results"/>
            </div>

            <div class="ds_layout__feedback">
                <#include '../common/feedback-wrapper.ftl'>
            </div>
        </main>
    </div>
</#if>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/filtered-list-page.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/filtered-list-page.es5.js"/>"></script>
</@hst.headContribution>

<#if index??>
    <#if index.title?has_content>
    <@hst.headContribution category="pageTitle">
        <title>${index.title} - gov.scot</title>
    </@hst.headContribution>
    </#if>

    <#if index.metaDescription?has_content>
    <@hst.headContribution>
        <meta name="description" content="${index.metaDescription}"/>
    </@hst.headContribution>
    </#if>

    <#assign document = index/>
    <#include "../common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=index canonical=true/>

    <#assign canonicalitem = canonicalitem?remove_ending("index/")/>

    <#include "../common/canonical.ftl" />
    <#include "../common/gtm-datalayer.ftl"/>
</#if>

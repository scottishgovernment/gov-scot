<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="index" type="scot.gov.www.beans.SimpleContent" -->

<#assign term = "" />
<#if parameters['q']??>
    <#assign term = parameters['q'][0]?j_string />
</#if>

<#if index??>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${index.title?html}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <#if isPostcode??>
                    <div class="ds_inset-text">
                        <div class="ds_inset-text__text">
                            <@hst.html hippohtml=covidLookupPage.searchPageContent/>

                            <p>Use the <a href="<@hst.link hippobean=covidLookupPage/>#!/${normalisedPostcode}">
                                COVID postcode checker.</a></p>
                        </div>
                    </div>
                <#else>
                    <div class="ds_leader--first-paragraph">
                        <@hst.html hippohtml=index.content/>
                    </div>
                </#if>

                <#include "../common/search.ftl" />

                <@hst.include ref="results"/>


            </div>
        </main>
    </div>
</#if>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/search.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/search.es5.js"/>"></script>
</@hst.headContribution>

<#if index??>
    <@hst.headContribution category="pageTitle">
        <title>${index.title} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${index.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" path="/search" canonical=true/>
    <#include "../common/canonical.ftl" />
</#if>

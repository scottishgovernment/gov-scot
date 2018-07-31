<#include "../../include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="index" type="scot.gov.www.beans.SimpleContent" -->

<#assign term = "" />
<#if parameters['term']??>
    <#assign term = parameters['term'][0] />
</#if>

<div class="layout--search-results">

<div class="grid" id="page-content">
    <div class="grid__item medium--nine-twelfths large--seven-twelfths">
        <#if index??>
            <h1 class="article-header">${index.title?html}</h1>
            <div class="body-content  leader--first-para">
                ${index.content.content}
            </div>

            <div class="search-box search-box--large ">
                <form id="filters" class="search-box__form" method="GET" action="<@hst.link path='/search/'/>">
                    <label class="search-box__label hidden" for="search-box">Search</label>
                    <input value="${term}" name="term" required="" id="filters-search-term" class="search-box__input " type="text" placeholder="Search site">
                    <button type="submit" title="search" class="search-box__button button button--primary">
                        <span class="icon icon--search-white"></span>
                        <span class="hidden">Search</span>
                    </button>
                </form>
            </div>
        </#if>
    </div>
</div>

<div class="grid">
    <div class="grid__item medium--nine-twelfths large--seven-twelfths">
        <@hst.include ref="results"/>
    </div>
</div>

</div>

<@hst.headContribution category="footerScripts">
    <script src="<@hst.webfile path="/assets/scripts/filtered-list-page.js"/>" type="text/javascript"></script>
</@hst.headContribution>

<#if index??>
    <@hst.headContribution category="pageTitle"><title>${index.title} - gov.scot</title></@hst.headContribution>

    <@hst.link var="canonicalitem" path="/search" canonical=true />
    <#include "../common/canonical.ftl" />
</#if>

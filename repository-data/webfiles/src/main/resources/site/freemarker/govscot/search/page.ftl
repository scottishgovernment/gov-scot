<#include "../../include/imports.ftl">

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="index" type="scot.gov.www.beans.SimpleContent" -->

<div class="grid" id="page-content">
    <div class="grid__item medium--nine-twelfths large--seven-twelfths">
        <#if index??>
            <h1 class="article-header">${index.title?html}</h1>
            <div class="body-content  leader--first-para">
                <div class="search-box search-box--large ">
                    <form class="search-box__form" method="GET" action="/search/">
                        <label class="search-box__label hidden" for="search-box">Search</label>
                        <input name="q" required="" id="search-box" class="search-box__input " type="text" placeholder="Search site">
                        <button type="submit" title="search" class="search-box__button button button--primary">
                            <span class="icon icon--search-white"></span>
                            <span class="hidden">Search</span>
                        </button>
                    </form>
                </div>
                ${index.content.content}
            </div>
        </#if>
    <#--{{> search-box expandable=false additionalClass="search-box--large" }}-->
    </div>
</div>



<div class="grid">
    <div class="grid__item medium--nine-twelfths large--seven-twelfths">
        <@hst.include ref="results"/>
    </div>
</div>

<#--<@hst.headContribution category="footerScripts">-->
    <#--<script src="<@hst.webfile path="/assets/scripts/filtered-list-page.js"/>" type="text/javascript"></script>-->
<#--</@hst.headContribution>-->

<#include "../../include/imports.ftl">

<#-- @ftlvariable name="index" type="scot.gov.www.beans.SimpleContent" -->
<#if index??>
    <div class="grid" id="page-content"><!--
        --><div class="grid__item medium--eight-twelfths">
            <h1 class="article-header">${index.title?html}</h1>

            <#if index.content.content?has_content>
                <@hst.html hippohtml=index.content/>
            <#else>
                <p>${index.summary}</p>
            </#if>
            
        </div><!--
    --></div>
</#if>

<div class="grid"><!--
    --><div class="grid__item medium--four-twelfths large--three-twelfths">
        <@hst.include ref="side-filter"/>
    </div><!--
    --><div class="grid__item medium--eight-twelfths large--seven-twelfths">
        <@hst.include ref="results"/>
    </div><!--
--></div>

<@hst.headContribution category="footerScripts">
    <script src="<@hst.webfile path="/assets/scripts/filtered-list-page.js"/>" type="text/javascript"></script>
</@hst.headContribution>

<@hst.headContribution category="pageTitle"><title>${index.title} - gov.scot</title></@hst.headContribution>

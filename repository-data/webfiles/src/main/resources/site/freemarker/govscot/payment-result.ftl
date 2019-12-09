<#include "../include/imports.ftl">

<#if paymentResult??>

<article id="page-content" class="layout--site-item">

    <div class="grid">
        <h1 class="article-header">${paymentResult.title}</h1>
        <div class="body-content">
        ${paymentResult.content?html}
        </div>
    </div>

</article>

<div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
    <#include 'common/feedback-wrapper.ftl'>
</div><!--
    --></div>
</#if>

<#if paymentResult??>
    <@hst.headContribution category="pageTitle">
    <title>${paymentResult.title?html} - gov.scot</title>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=paymentResult canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>
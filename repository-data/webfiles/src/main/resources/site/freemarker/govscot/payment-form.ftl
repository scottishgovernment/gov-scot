<#include "../include/imports.ftl">

<#if document??>

    <@hst.manageContent hippobean=document/>

<article id="page-content" class="layout--site-item">

    <div class="grid">
        <h1 class="article-header">${document.title}</h1>
        <div class="body-content">

            <div class="leader leader--first-para">
                <p>${document.summary}</p>
            </div>
            <#if document.content??>
                <@hst.html hippohtml=document.content/>
            </#if>
        </div>
    </div>

</article>

<form id="online-payment-form" class="layout--site-item" action="http://localhost:9095" method="post">

    Payment Reference<br>
    <input type="text" name="paymentReference" required placeholder="Customer ID - invoice number">
    <br>
    Amount<br>
    <input type="number" step="0.01" name="amount" required placeholder="(GBP £)">
    <br>
    Description<br>
    <input type="text" name="description" placeholder="please advise of reason for payment if non-invoiced item">
    <br><br>
    <input type="submit" name="submit" content="Submit Payment" >
</form>

<div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
    <#include 'common/feedback-wrapper.ftl'>
</div><!--
    --></div>
</#if>

<#if document??>
    <@hst.headContribution category="pageTitle">
    <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
    <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>

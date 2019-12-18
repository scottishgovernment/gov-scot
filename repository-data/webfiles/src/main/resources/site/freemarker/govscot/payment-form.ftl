<#include "../include/imports.ftl">

<#if document??>

    <@hst.manageContent hippobean=document/>

<div class="grid"><!--
    --><div class="grid__item medium--nine-twelfths large--seven-twelfths <#if document.additionalContent?has_content>push--medium--three-twelfths</#if>">
        <article id="page-content" class="layout--site-item">

            <h1 class="article-header">${document.title}</h1>
            <div class="body-content">

                <div class="leader leader--first-para">
                    <p>${document.summary}</p>
                </div>
                <#if document.content??>
                    <@hst.html hippohtml=document.content/>
                </#if>
            </div>
        </article>

        <form id="payment-form" class="layout--site-item" method="post">

            <div id="error-summary-fail" class="ds_error-summary  hidden  hidden--hard  flashable" id="error-summary" aria-labelledby="error-summary-title" role="alert">
                <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                <p class="error-summary-message"></p>
            </div>

            <label class="ds_label" for="orderCode">Payment Reference</label>
            <input class="ds_input" type="text" id="orderCode" name="orderCode" required placeholder="Customer ID - invoice number">

            <label class="ds_label" for="amount">Amount</label>
            <div class="ds_currency-wrapper" data-symbol="£">
                <input class="ds_input  ds_input--fixed-10" step="0.01" placeholder="0.00" type="number" id="amount" name="amount" required />
            </div>

            <label class="ds_label" for="description">Description</label>
            <textarea rows="2" class="ds_input" type="text" id="description" name="description" placeholder="please advise of reason for payment if non-invoiced item"></textarea>

            <input type="submit" class="button  button--primary" name="submit" value="Submit Payment" >
        </form>
    </div><!--
--></div>


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

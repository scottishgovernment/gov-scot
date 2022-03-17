<#ftl output_format="HTML">
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
        <!--noindex-->
        <form id="payment-form" class="layout--site-item" method="post">

            <div id="error-summary" class="ds_error-summary  hidden  hidden--hard  flashable" aria-labelledby="error-summary-title" role="alert">
                <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                <div class="error-summary-message"></div>
            </div>

            <div class="ds_question" data-threshold="80" data-module="ds-character-count">
                <label class="ds_label" for="orderCode">Payment Reference</label>
                <p class="ds_question__message  hidden  hidden--hard" id="payment-ref-spaces">Payment Reference cannot contain spaces</p>
                <input maxlength="64" class="ds_input" type="text" id="orderCode" name="orderCode" required placeholder="Customer ID - invoice number">
            </div>

            <div class="ds_question">
                <label class="ds_label" for="amount">Amount</label>
                <p class="ds_question__message  hidden  hidden--hard" id="amount-min">Amount cannot be less than £0.01</p>
                <p class="ds_question__message  hidden  hidden--hard" id="amount-max">Amount cannot be more than £5000.00</p>
                <div class="ds_currency-wrapper" data-symbol="£">
                    <input class="ds_input  ds_input--fixed-10" step="0.01" placeholder="0.00" type="number" id="amount" name="amount" min="0.01" max="5000.00" required />
                </div>
            </div>

            <div class="ds_question">
                <label class="ds_label" for="description">Description</label>
                <textarea rows="2" class="ds_input" type="text" id="description" name="description" required placeholder="please advise of reason for payment if non-invoiced item"></textarea>
            </div>

            <div class="ds_question">
                <label class="ds_label" for="email">Email address</label>
                <p class="ds_question__message  hidden  hidden--hard" id="invalid-email">Email address is not in a valid format</p>
                <input class="ds_input" type="text" id="email" name="email" required placeholder="Your email address for payment confirmation"></input>
            </div>

            <input type="submit" class="button  button--primary" name="submit" value="Submit Payment" >
        </form>
        <!--endnoindex-->
    </div><!--
--></div>


<div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
    <#include 'common/feedback-wrapper.ftl'>
</div><!--
    --></div>
</#if>

<#if document??>
    <@hst.headContribution category="dcMeta">   
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>
    <#if document.tags??>
        <@hst.headContribution category="dcMeta">
            <meta name="dc.subject" content="<#list document.tags as tag>${tag}<#sep>, </#sep></#list>"/>
        </@hst.headContribution>
    </#if>  
    <@hst.headContribution category="pageTitle">
    <title>${document.title} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
    <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>

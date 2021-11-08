<#include "../include/imports.ftl">

<#if document??>

<@hst.manageContent hippobean=document/>
<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  ds_layout--article">
        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title">${document.title}</h1>
            </header>
        </div>

        <div class="ds_layout__content">
            <div class="ds_leader-first-paragraph">
                <p>${document.summary}</p>
            </div>
            <#if document.content??>
                <@hst.html hippohtml=document.content/>
            </#if>

            <form id="payment-form" class="form-box" method="post">
                <div id="error-summary" class="ds_error-summary  hidden  hidden--hard  flashable" aria-labelledby="error-summary-title" role="alert">
                    <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                    <div class="error-summary-message"></div>
                </div>

            <div class="ds_question">
                <label class="ds_label" for="amount">Amount</label>
                <p class="ds_question__message  hidden  hidden--hard" id="amount-min">Amount cannot be less than £0.01</p>
                <p class="ds_question__message  hidden  hidden--hard" id="amount-max">Amount cannot be more than £5000.00</p>
                <div class="ds_currency-wrapper" data-symbol="£">
                    <input class="ds_input  ds_input--fixed-10" step="0.01" placeholder="0.00" type="number" id="amount" name="amount" min="0.01" max="5000.00" required />
                </div>

                <div class="ds_question">
                    <label class="ds_label" for="amount">Amount</label>
                    <p class="ds_question__message  hidden  hidden--hard" id="amount-max">Amount cannot be more than £5000.00</p>
                    <div class="ds_currency-wrapper" data-symbol="£">
                        <input class="ds_input  ds_input--fixed-10" step="0.01" placeholder="0.00" type="number" id="amount" name="amount" required />
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

                <input type="submit" class="ds_button" name="submit" value="Submit Payment" >
            </form>
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>
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

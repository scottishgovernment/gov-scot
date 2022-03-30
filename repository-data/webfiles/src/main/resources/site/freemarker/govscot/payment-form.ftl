<#ftl output_format="HTML">
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

            <!--noindex-->
            <form id="payment-form" method="post">

                <div id="error-summary" class="ds_error-summary  fully-hidden  flashable" aria-labelledby="error-summary-title" role="alert">
                    <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                    <div class="error-summary-message"></div>
                </div>

                <div class="ds_question" data-threshold="80" data-module="ds-character-count">
                    <label class="ds_label" for="orderCode">Payment Reference</label>
                    <p class="ds_hint-text">Customer ID - invoice number</p>
                    <p class="ds_question__error-message  fully-hidden" id="payment-ref-spaces">Payment Reference cannot contain spaces</p>
                    <input maxlength="64" class="ds_input" type="text" id="orderCode" name="orderCode" required>
                </div>

                <div class="ds_question">
                    <label class="ds_label" for="amount">Amount</label>
                    <p class="ds_question__error-message  fully-hidden" id="amount-min">Amount cannot be less than £0.01</p>
                    <p class="ds_question__error-message  fully-hidden" id="amount-max">Amount cannot be more than £5000.00</p>
                    <div class="ds_currency-wrapper" data-symbol="£">
                        <input class="ds_input  ds_input--fixed-10" step="0.01" placeholder="0.00" type="number" id="amount" name="amount" min="0.01" max="5000.00" required />
                    </div>
                </div>

                <div class="ds_question">
                    <label class="ds_label" for="description">Description</label>
                    <p class="ds_hint-text">Please advise of reason for payment if non-invoiced item.</p>
                    <textarea rows="2" class="ds_input" type="text" id="description" name="description" required></textarea>
                </div>

                <div class="ds_question">
                    <label class="ds_label" for="email">Email address</label>
                    <p class="ds_hint-text">Your email address for payment confirmation.</p>
                    <p class="ds_question__error-message  fully-hidden" id="invalid-email">Email address is not in a valid format</p>
                    <input class="ds_input" type="text" id="email" name="email" required></input>
                </div>

                <button id="submit-payment" class="ds_button" name="submit">Submit payment</button>
            </form>
            <!--endnoindex-->
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>
</#if>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/payment-form.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/payment-form.es5.js"/>"></script>
</@hst.headContribution>

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

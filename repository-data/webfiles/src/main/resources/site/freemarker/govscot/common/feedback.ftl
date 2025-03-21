<#ftl output_format="HTML">

<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if feedbackIsEnabled?? && feedbackIsEnabled == true>
<div class="gov_feedback">
    <section id="feedback">
        <div class="js-error-summary-container  form-errors" role="alert"></div>

        <div class="js-confirmation-message  ds_confirmation-message  fully-hidden" aria-live="polite">
            <svg class="ds_confirmation-message__icon  ds_icon  ds_icon--24" aria-hidden="true" role="img"><use href="${iconspath}#check_circle"></use></svg>
            <div class="ds_confirmation-message__title  ds_h3  ds_no-margin">
                Thanks for your feedback
            </div>
        </div>

        <input id="page-category" type="hidden" value="${layoutName}" form=feedbackForm">

        <form id="feedbackForm">

        </form>
    </section>
</div>
</#if>

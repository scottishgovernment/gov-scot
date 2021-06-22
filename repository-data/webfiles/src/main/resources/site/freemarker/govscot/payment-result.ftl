<#include "../include/imports.ftl">

<#if paymentResult??>

<div class="ds_wrapper">
    <main id="main-content" class="ds_layout  ds_layout--article">
        <div class="ds_layout__header">
            <header class="ds_page-header">
                <h1 class="ds_page-header__title">${paymentResult.title}</h1>
            </header>
        </div>

        <div class="ds_layout__content">
            ${paymentResult.content?html}
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>

</#if>

<#if paymentResult??>
    <@hst.headContribution category="pageTitle">
    <title>${paymentResult.title?html} - gov.scot</title>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=paymentResult canonical=true/>
    <#include "common/canonical.ftl" />

    <#include "common/gtm-datalayer.ftl"/>
</#if>

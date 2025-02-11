<#ftl output_format="HTML">
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
            ${paymentResult.content}
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>

</#if>

<#if paymentResult??>

    
    <@hst.headContribution>
    <link rel="canonical" href="https://www.gov.scot/payment/${paymentResult.id}"/> 
    </@hst.headContribution>

    <@hst.headContribution category="pageTitle">
    <title>${paymentResult.title} - gov.scot</title>
    </@hst.headContribution>


</#if>

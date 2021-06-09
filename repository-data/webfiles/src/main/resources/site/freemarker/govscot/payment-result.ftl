<#include "../include/imports.ftl">

<#if paymentResult??>

<article id="page-content" class="layout--site-item">

<div class="grid"><!--
    --><div class="grid__item medium--nine-twelfths large--seven-twelfths <#if document.additionalContent?has_content>push--medium--three-twelfths</#if>">
        <h1 class="article-header">${paymentResult.title} ${paymentResult.id}</h1>
        <div class="body-content">
            ${paymentResult.content?html}
        </div>
    </div><!--
--></div>

</article>

<div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
    <#include 'common/feedback-wrapper.ftl'>
</div><!--
    --></div>
</#if>

<#if paymentResult??>

    <link rel="canonical" href="https://www.gov.scot/payment/${paymentResult.id}/>

    <@hst.headContribution category="pageTitle">
    <title>${paymentResult.title?html} - gov.scot</title>
    </@hst.headContribution>


</#if>

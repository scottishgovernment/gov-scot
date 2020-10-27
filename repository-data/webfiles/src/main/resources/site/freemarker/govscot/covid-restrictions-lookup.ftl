<#include "../include/imports.ftl">

<#if document??>

    <@hst.manageContent hippobean=document/>

<div class="grid"><!--
    --><div class="grid__item medium--nine-twelfths large--seven-twelfths <#if document.additionalContent?has_content>push--medium--three-twelfths</#if>">
        <section id="lookup-landing" class="hidden--hard">
        <article id="page-content" class="layout--site-item">
            <h1 class="article-header">${document.title}</h1>
            <div class="body-content">
                <#if document.content??>
                    <@hst.html hippohtml=document.content/>
                </#if>
            </div>
        </article>

        <form id="covid-lookup" class="hidden  hidden--hard  form-box" method="post">
            <div id="error-summary" class="ds_error-summary  hidden  hidden--hard  flashable" aria-labelledby="error-summary-title" role="alert">
                <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

                <div class="error-summary-message"></div>
            </div>

            <fieldset>
                <div class="ds_question">
                    <label class="ds_label" for="postcode">Enter your postcode</label>
                    <p class="ds_hint-text">For example EH5 2GG</p>
                    <p class="ds_question__message  ds_question__error-message  hidden  hidden--hard"></p>
                    <input class="ds_input" type="text" id="postcode" name="postcode"></input>
                </div>

                <div class="ds_question">
                    <p class="ds_question__message  ds_question__error-message  hidden  hidden--hard" data-form="error-find"></p>
                    <input id="covid-lookup-submit" aria-required="true" type="submit" class="button  button--primary  ds_no-margin" name="submit" data-button="button-find" value="Find">
                </div>
            </fieldset>

            <div><a href="https://www.royalmail.com/find-a-postcode">Find a postcode on Royal Mail's postcode finder</a></div>
        </form>
        </section>

        <section id="lookup-results" class="hidden  hidden--hard">
        </section>
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

    <@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/covid-lookup.js"/>"></script>
    </@hst.headContribution>
    <@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/covid-lookup.es5.js"/>"></script>
    </@hst.headContribution>
</#if>

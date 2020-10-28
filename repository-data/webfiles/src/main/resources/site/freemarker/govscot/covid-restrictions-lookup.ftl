<#include "../include/imports.ftl">

<#if document??>

    <@hst.manageContent hippobean=document/>

<div class="grid"><!--
    --><div class="grid__item medium--nine-twelfths large--seven-twelfths <#if document.additionalContent?has_content>push--medium--three-twelfths</#if>">
        <article id="page-content" class="layout--site-item">
            <section id="covid-restrictions-lookup-landing" class="hidden--hard">
                <div class="ds_error-summary  fully-hidden  flashable" id="covid-restrictions-error-summary" aria-labelledby="error-summary-title" role="alert"></div>

                <h1 class="article-header  overflow--medium--three-twelfths  overflow--large--two-twelfths  overflow--xlarge--two-twelfths">${document.title}</h1>
                <div class="body-content">
                    <#if document.content??>
                        <@hst.html hippohtml=document.content/>
                    </#if>

                    <form id="covid-restrictions-lookup-form" class="hidden  hidden--hard  form-box" method="post">
                        <fieldset>
                            <div class="ds_question">
                                <label class="ds_label" for="postcode">Enter your postcode</label>
                                <p class="ds_hint-text">You need to enter a full postcode.<br />For example EH5 2GG.</p>
                                <p class="ds_question__message  ds_question__error-message  hidden  hidden--hard  perf-error"></p>
                                <input class="ds_input" type="text" id="postcode" name="postcode">
                            </div>

                            <div class="ds_question">
                                <p class="ds_question__message  ds_question__error-message  hidden  hidden--hard  perf-error" data-form="error-find"></p>
                                <input id="covid-lookup-submit" type="submit" class="button  button--primary  button--primary--fluid  ds_no-margin" name="submit" data-button="button-find" value="Find">
                            </div>
                        </fieldset>

                        <div><a href="https://www.royalmail.com/find-a-postcode">Do not know a postcode? Get it on Royal Mail.</a></div>
                    </form>

                    <#if document.secondaryContent??>
                        <div>
                            <@hst.html hippohtml=document.secondaryContent/>
                        </div>
                    </#if>
                </div>
            </section>

            <section id="covid-restrictions-lookup-results" class="hidden  hidden--hard">
            </section>
        </article>
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
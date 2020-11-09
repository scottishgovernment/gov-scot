<#include "../include/imports.ftl">

<#if document??>

    <@hst.manageContent hippobean=document/>

    <@hst.html var="badpostcode" hippohtml=document.postcodeErrorMessage/>
    <@hst.html var="serviceUnavailable" hippohtml=document.serviceErrorMessage/>
    <@hst.html var="restrictionMessage" hippohtml=document.restrictionErrorMessage/>
    <@hst.html var="englishPostcode" hippohtml=document.englishPostcodeErrorMessage/>
    <@hst.html var="welshPostcode" hippohtml=document.welshPostcodeErrorMessage/>
    <@hst.html var="northernIrishPostcode" hippohtml=document.northernIrishPostcodeErrorMessage/>
    <@hst.html var="unrecognisedPostcode" hippohtml=document.unrecognisedPostcodeErrorMessage/>

<script>
    window.errorMessages = {
        badPostcode: '${badpostcode?js_string}',
        serviceUnavailable: '${serviceUnavailable?js_string}',
        restrictionMessage: '${restrictionMessage?js_string}',
        englishPostcode: '${englishPostcode?js_string}',
        welshPostcode: '${welshPostcode?js_string}',
        northernIrishPostcode: '${northernIrishPostcode?js_string}',
        unrecognisedPostcode: '${unrecognisedPostcode?js_string}'
    };
</script>

<div class="grid"><!--
    --><div class="grid__item medium--nine-twelfths large--seven-twelfths <#if document.additionalContent?has_content>push--medium--three-twelfths</#if>">
        <article id="page-content" class="layout--site-item">
            <section id="covid-restrictions-lookup-landing" class="hidden--hard">
                <div class="ds_error-summary  fully-hidden  flashable" id="covid-restrictions-error-summary" aria-labelledby="error-summary-title" role="alert"></div>
                <h1 class="article-header  overflow--medium--three-twelfths  overflow--large--two-twelfths  overflow--xlarge--two-twelfths">${document.title}</h1>
                <div class="body-content">
                    <noscript>
                        <div class="info-note caution">You need JavaScript enabled in your browser to use the postcode search.</div>
                    </noscript>
                    <style>
                    .visible-js {
                        display: none;
                    }
                    .js-enabled .visible-js {
                        display: block;
                    }
                    </style>
                    <div class="visible-js">
                        <#if document.content??>
                            <@hst.html hippohtml=document.content/>
                        </#if>
                        <form id="covid-restrictions-lookup-form" class="hidden  hidden--hard  form-box" method="post">
                            <fieldset>
                                <div class="ds_question">
                                    <label class="ds_label" for="postcode">Enter a postcode</label>
                                    <p class="ds_hint-text">${document.hintMessage}</p>
                                    <p class="ds_question__message  ds_question__error-message  hidden  hidden--hard  perf-error">${document.errorMessage}</p>
                                    <input class="ds_input" type="text" id="postcode" name="postcode">
                                </div>

                                <div class="ds_question">
                                    <p class="ds_question__message  ds_question__error-message  hidden  hidden--hard  perf-error" data-form="error-find"></p>
                                    <input id="covid-lookup-submit" type="submit" class="button  button--primary  button--primary--fluid  ds_no-margin" name="submit" data-button="button-find" value="Find">
                                </div>
                            </fieldset>

                            <div><a href="https://www.royalmail.com/find-a-postcode">Do not know a postcode? Get it on Royal Mail.</a></div>
                        </form>
                    </div>

                    <#if document.secondaryContent??>
                        <div>
                            <@hst.html hippohtml=document.secondaryContent/>
                        </div>
                    </#if>

                    <#if document.resultsContent??>
                        <div id="covid-restrictions-lookup-results-content" class="hidden  hidden--hard">
                            <@hst.html hippohtml=document.resultsContent/>
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

    <@hst.headContribution category="noscriptHead">
        <noscript>
            <meta http-equiv="refresh" content="0;url=/publications/coronavirus-covid-19-protection-levels/" />
        </noscript>
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

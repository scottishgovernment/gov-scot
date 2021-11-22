<#include "../include/imports.ftl">

<#if document??>

    <@hst.manageContent hippobean=document/>

    <article id="page-content" class="layout--site-item">

        <div class="grid"><!--
         --><div class="grid__item medium--nine-twelfths large--seven-twelfths <#if document.additionalContent?has_content>push--medium--three-twelfths</#if>">
                <h1 class="article-header">${document.title}</h1>

                <@hst.html hippohtml=document.content/>

                <div class="form-box" id="cookie-form-box">
                    <form id="cookie-preferences">
                        <fieldset>
                            <legend class="fully-hidden">Turn cookies on or off</legend>

                            <div class="ds_question  ds_!_margin--0">
                                <h3 class="beta  no-top-margin">Cookies needed for the website to work</h3>
                                <p>These cookies do things like keep the website secure. They always need to be on.</p>
                            </div>

                            <div class="ds_question">
                                <h3 class="beta">Cookies that remember your settings</h3>
                                <p>These cookies do things like remember pop-ups you’ve seen, so you're not shown them again.</p>
                                <div class="ds_field-group  ds_field-group--inline">
                                    <div class="ds_radio">
                                        <input id="preferences-yes" value="true" name="cookie-preferences" class="checkbox-group__input  fancy-radio" type="radio" checked="true">
                                        <label for="preferences-yes" class="checkbox-group__label  fancy-radio">On</label>
                                    </div>

                                    <div class="ds_radio">
                                        <input id="preferences-no" value="false" name="cookie-preferences" class="checkbox-group__input  fancy-radio" type="radio">
                                        <label for="preferences-no" class="checkbox-group__label  fancy-radio">Off</label>
                                    </div>
                                </div>
                            </div>

                            <div class="ds_question">
                                <h3 class="beta">Cookies that measure website use</h3>
                                <p>These cookies store information about how you use our website, such as what you click on.</p>
                                <div class="ds_field-group  ds_field-group--inline">
                                    <div class="ds_radio">
                                        <input id="statistics-yes" value="true" name="cookie-statistics" class="checkbox-group__input  fancy-radio" type="radio" checked="true">
                                        <label for="statistics-yes" class="checkbox-group__label  fancy-radio">On</label>
                                    </div>

                                    <div class="ds_radio">
                                        <input id="statistics-no" value="false" name="cookie-statistics" class="checkbox-group__input  fancy-radio" type="radio">
                                        <label for="statistics-no" class="checkbox-group__label  fancy-radio">Off</label>
                                    </div>
                                </div>
                            </div>

                            <div id="cookie-success-message" class="form-message  form-message--success  fully-hidden">
                                <p><b>Your cookie preferences have been saved.</b></p>
                            </div>

                            <button class="ds_button  ds_!_margin-bottom--0" type="submit">Save cookie preferences</button>
                        </fieldset>
                    </form>
                </div>

                <@hst.html hippohtml=document.additionalContent/>

            </div><!--
     --></div>

    </article>

    <div class="grid"><!--
        --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
            <#include 'common/feedback-wrapper.ftl'>
        </div><!--
    --></div>
</#if>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/cookie-preferences.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/cookie-preferences.es5.js"/>"></script>
</@hst.headContribution>

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

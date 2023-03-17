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
                <@hst.html hippohtml=document.content/>

                <div class="ds_!_margin-top--4  ds_!_margin-bottom--8" id="cookie-form-box">
                    <form id="cookie-preferences">

                        <h3>Cookies needed for the website to work</h3>
                        <p>These cookies do things like keep the website secure. They always need to be on.</p>

                        <div class="ds_question">
                            <fieldset>
                                <legend>Cookies that remember your settings</legend>
                                <p>These cookies do things like remember pop-ups you’ve seen, so you're not shown them again.</p>
                                <div class="ds_field-group">
                                    <div class="ds_radio">
                                        <input id="preferences-yes" value="true" name="cookie-preferences" class="ds_radio__input" type="radio" checked="true">
                                        <label for="preferences-yes" class="ds_radio__label">On</label>
                                    </div>

                                    <div class="ds_radio">
                                        <input id="preferences-no" value="false" name="cookie-preferences" class="ds_radio__input" type="radio">
                                        <label for="preferences-no" class="ds_radio__label">Off</label>
                                    </div>
                                </div>
                            </fieldset>
                        </div>

                        <div class="ds_question">
                            <fieldset>
                                <legend>Cookies that measure website use</legend>
                                <p>These cookies store information about how you use our website, such as what you click on.</p>
                                <div class="ds_field-group">
                                    <div class="ds_radio">
                                        <input id="statistics-yes" value="true" name="cookie-statistics" class="ds_radio__input" type="radio" checked="true">
                                        <label for="statistics-yes" class="ds_radio__label">On</label>
                                    </div>

                                    <div class="ds_radio">
                                        <input id="statistics-no" value="false" name="cookie-statistics" class="ds_radio__input" type="radio">
                                        <label for="statistics-no" class="ds_radio__label">Off</label>
                                    </div>
                                </div>
                            </fieldset>
                        </div>

                        <div class="ds_question">
                            <fieldset>
                                <legend>Cookies that help with our communications and marketing</legend>
                                <p>These cookies may be set by third party websites and do things like measure how you view YouTube videos that are on gov.scot.</p>
                                <div class="ds_field-group">
                                    <div class="ds_radio">
                                        <input id="marketing-yes" value="true" name="cookie-marketing" class="ds_radio__input" type="radio" checked="true">
                                        <label for="marketing-yes" class="ds_radio__label">On</label>
                                    </div>

                                    <div class="ds_radio">
                                        <input id="marketing-no" value="false" name="cookie-marketing" class="ds_radio__input" type="radio">
                                        <label for="marketing-no" class="ds_radio__label">Off</label>
                                    </div>
                                </div>
                            </fieldset>
                        </div>

                        <div id="cookie-success-message" class="form-message  form-message--success  fully-hidden">
                            <p><b>Your cookie preferences have been saved.</b></p>
                        </div>

                        <button class="ds_button  ds_no-margin" type="submit">Save cookie preferences</button>
                    </form>
                </div>

                <@hst.html hippohtml=document.additionalContent/>
            </div>
        </div>

        <div class="ds_layout__feedback">
            <#include 'common/feedback-wrapper.ftl'>
        </div>
    </main>
</div>

</#if>

<#if document??>
    <@hst.headContribution category="footerScripts">
        <script type="module" src="<@hst.webfile path="/assets/scripts/cookie-preferences.js"/>"></script>
    </@hst.headContribution>

    <@hst.headContribution category="footerScripts">
        <script nomodule="true" src="<@hst.webfile path="/assets/scripts/cookie-preferences.es5.js"/>"></script>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <#if document.tags?size gt 0>
        <@hst.headContribution category="dcMeta">
            <meta name="dc.subject" content="<#list document.tags as tag>${tag}<#sep>, </#sep></#list>"/>
        </@hst.headContribution>
    </#if>

    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=lastUpdated.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
    </@hst.headContribution>

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

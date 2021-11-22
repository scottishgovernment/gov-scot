<#include "../../include/imports.ftl">

<@hst.link var="cookieprefslink" path="/cookies"/>

<div id="cookie-notice" class="ds_notification  ds_notification--large  ds_notification--cookie  js-initial-cookie-content  fully-hidden" data-module="ds-cookie-notification">
    <div class="wrapper">
        <div class="ds_notification__content">
            <h2 class="visually-hidden">Information</h2>

            <div class="ds_notification__text">
                <p>We use <a href="${cookieprefslink}">cookies</a> to collect anonymous data to help us improve your site browsing
                    experience.</p>
                <p>Click 'Accept all cookies' to agree to all cookies that collect anonymous data.
                    To only allow the cookies that make the site work, click 'Use essential cookies only.' Visit 'Set cookie preferences' to control specific cookies.</p>
            </div>

            <div class="ds_notification__actions">
                <button class="ds_button  ds_button--small  js-accept-all-cookies">Accept all cookies</button>
                <button class="ds_button  ds_button--small  ds_button--secondary  js-accept-essential-cookies">Use essential cookies only</button>
                <#if cookieprefslink??>
                    <a href="${cookieprefslink}">Set cookie preferences</a>
                </#if>
            </div>
        </div>
    </div>
</div>

<div id="cookie-confirm" data-customcondition="true" class="ds_notification  ds_notification--cookie-success  ds_reversed  js-confirm-cookie-content  fully-hidden" data-module="ds-notification">
    <div class="wrapper">
        <div class="ds_notification__content">
            <div class="ds_notification__text">
                <p>
                    Your cookie preferences have been saved.

                    <#if cookieprefslink??>
                        You can <a href="${cookieprefslink}">change your cookie settings</a> at any time.
                    </#if>
                </p>
            </div>
        </div>
    </div>
</div>

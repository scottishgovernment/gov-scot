<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="notification-wrapper">
    <@hst.include ref="mourning-banner"/>

    <div id="cookie-notice" class="ds_notification  ds_notification--large  ds_notification--cookie  js-initial-cookie-content">
        <div class="ds_wrapper">
            <div class="ds_notification__content">
                <div role="heading" class="visually-hidden">Cookie notice</div>

                <div class="ds_notification__text">
                    <p>Gov.scot uses cookies which are essential for the site to work. We also use non-essential cookies to help us improve our websites. Any data collected is anonymised. By continuing to use this site, you agree to our use of cookies.</p>
                </div>

                <div class="ds_notification__actions">
                    <button class="ds_button  ds_button--small  js-accept-cookies" data-banner="banner-cookie-accept">Accept cookies</button>
                    <a href="<@hst.link path='/siteitems/cookies/'/>" class="ds_button  ds_button--small  ds_button--secondary" data-banner="banner-cookie-settings">Cookie settings</a>
                </div>
            </div>
        </div>
    </div>

    <@hst.include ref="important-banner"/>
</div>

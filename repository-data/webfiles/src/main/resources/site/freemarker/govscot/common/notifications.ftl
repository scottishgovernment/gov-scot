<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="notification-wrapper">
    <@hst.include ref="mourning-banner"/>

    <div id="cookie-notice" class="ds_notification  hidden  hidden--hard">
        <div class="wrapper">
            <div class="ds_notification__content  ds_notification__content--has-close">
                <div class="ds_notification__text">
                    <p>Gov.scot uses cookies which are essential for the site to work. We also use non-essential cookies to help us improve our websites. Any data collected is anonymised. By continuing to use this site, you agree to our use of cookies.  <a data-banner="banner-cookies-link" href="<@hst.link path='/siteitems/cookies/'/>">Find
                    out more about cookies</a></p>
                </div>

                <button data-banner="banner-cookies-close" class="ds_notification__close  js-close-notification" type="button">
                    <span class="hidden">Close this notification</span>
                    <svg data-banner="banner-close" class="ds_icon" role="img"><use xlink:href="${iconspath}#close-21"></use></svg>
                </button>
            </div>
        </div>
    </div>

    <@hst.include ref="important-banner"/>
</div>

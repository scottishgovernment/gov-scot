<#include "../../include/imports.ftl">

<#if showBetaBanner??>
    <div id="beta-banner" class="notification notification--gold">
        <div class="wrapper">
            <div class="notification__main-content">
                <button title="Close this notification" class="notification__close notification__close--minimised"></button>
                <span class="notification__beta-box">beta</span>
            </div>
            <div class="notification__extra-content hidden-xsmall">
                <p>You're viewing our new website - <a href="<@hst.link path='/siteitems/about-beta/'/>">find out more</a></p>
                <div class="grid">
                    <div class="grid__item six-twelfths">
                        <a class='notification__btn'
                            href="http://www.gov.scot/">current site</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</#if>

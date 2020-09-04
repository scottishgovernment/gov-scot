<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if importantBanner??>
    <div class="ds_notification  ds_notification--negative  hidden  hidden--hard  above-overlay" data-module="ds-notification">
        <div class="wrapper">
            <div class="ds_notification__content  ds_notification__content--has-close">
                <span class="ds_notification__icon" aria-hidden="true">
                    <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#bang-21"></use></svg>
                </span>
                <div class="ds_notification__text">
                    <@hst.html var="content" hippohtml=importantBanner.content/>
                    <#if content?has_content>
                        ${content}
                    </#if>
                </div>

                <button data-banner="banner-close" class="ds_notification__close  js-close-notification" type="button">
                    <span class="hidden">Close this notification</span>
                    <svg data-banner="banner-close" class="ds_icon" role="img"><use xlink:href="${iconspath}#close-21"></use></svg>
                </button>
            </div>
        </div>
    </div>
</#if>

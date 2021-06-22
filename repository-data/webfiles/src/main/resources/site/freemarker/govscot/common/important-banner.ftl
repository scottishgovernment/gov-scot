<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if importantBanner??>
    <div class="ds_notification  ds_reversed" data-module="ds-notification">
        <div class="ds_wrapper">
            <div class="ds_notification__content  ds_notification__content--has-close">
                <div role="heading" class="visually-hidden">Information</div>

                <span class="ds_notification__icon  ds_notification__icon--inverse  ds_notification__icon--colour" aria-hidden="true">
                    <svg class="ds_icon  ds_icon--fill" aria-hidden="true" role="img"><use xlink:href="${iconspath}#priority_high"></use></svg>
                </span>
                <div class="ds_notification__text">
                    <@hst.html var="content" hippohtml=importantBanner.content/>
                    <#if content?has_content>
                        ${content}
                    </#if>
                </div>

                <button class="ds_notification__close  js-close-notification" type="button">
                    <span class="visually-hidden">Close this notification</span>
                    <svg class="ds_icon  ds_icon--fill" role="img"><use xlink:href="${iconspath}#close"></use></svg>
                </button>
            </div>
        </div>
    </div>
</#if>

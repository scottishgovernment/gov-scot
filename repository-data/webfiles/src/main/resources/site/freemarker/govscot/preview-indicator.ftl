<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<@hst.setBundle basename="preview.indicator"/>

<#if isStaging>
<div class="ds_notification  dp_preview-notification">
    <div class="ds_wrapper">
        <div class="ds_notification__content">
            <h2 class="visually-hidden">Information</h2>
                <span class="ds_notification__icon" aria-hidden="true">
                    <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#info"></use></svg>
                </span>

            <div class="ds_notification__text">
                <p>
                    <@fmt.message key="indicator.content" var="content"/>${content}
                        <@fmt.message key="indicator.disclaimer" var="disclaimer"/>${disclaimer}
                </p>
            </div>
        </div>
    </div>
</div>
</#if>

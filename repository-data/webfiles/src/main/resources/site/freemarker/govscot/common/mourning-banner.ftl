<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#if mourningBanner??>
    <div id="mourning-banner" class="ds_notification  ds_reversed">
        <div class="ds_wrapper">
            <div class="ds_notification__content">
                <@hst.html var="content" hippohtml=mourningBanner.content/>
                <#if content?has_content>
                    <div class="ds_notification__text">
                        ${content?no_esc}
                    </div>
                </#if>
            </div>
        </div>
    </div>
</#if>

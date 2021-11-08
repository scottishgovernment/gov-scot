<#include "../../include/imports.ftl">

<#if mourningBanner??>
    <div id="mourning-banner" class="ds_notification  ds_notification--mourning">
        <div class="wrapper">
            <div class="ds_notification__content">
                <@hst.html var="content" hippohtml=mourningBanner.content/>
                <#if content?has_content>
                    <div class="ds_notification__text">
                        ${content}
                    </div>
                </#if>
            </div>
        </div>
    </div>
</#if>

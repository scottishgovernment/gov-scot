<#include "../../include/imports.ftl">

<#if mourningBanner??>
    <div id="mourning-banner" class="notification notification--mourning">
        <div class="wrapper">
            <div class="notification__main-content">
                <@hst.html var="content" hippohtml=mourningBanner.content/>
                <#if content?has_content>
                    ${content}
                </#if>
            </div>
        </div>
    </div>
</#if>
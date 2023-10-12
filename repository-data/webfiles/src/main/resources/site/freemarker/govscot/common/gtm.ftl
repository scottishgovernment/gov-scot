<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<@hst.headContribution category="googleTagManager">
<script id="gtm-script"
    src='<@hst.webfile path="assets/scripts/gtm.js"/>'
    data-containerId="${gtmContainerId?js_string}"
    <#if gtmEnv?has_content>data-env="${gtmEnv?js_string}"</#if>
    <#if gtmAuth?has_content>data-auth="${gtmAuth?js_string}"</#if>
    <#if gtmName??>data-format="${gtmName?js_string}"</#if>
    <#if gtmId??>data-siteid="${gtmId?js_string}"</#if>
    <#if userType??>data-usertype="${userType?js_string}"</#if>
></script>
</@hst.headContribution>

<!-- Google Tag Manager (noscript) -->
<noscript id="gtm-noscript"><iframe src="https://www.googletagmanager.com/ns.html?id=${gtmContainerId}<#if gtmAuth?has_content>&amp;gtm_auth=${gtmAuth}</#if><#if gtmEnv?has_content>&amp;gtm_preview=${gtmEnv}&amp;gtm_cookies_win=x</#if>"
                                    height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->

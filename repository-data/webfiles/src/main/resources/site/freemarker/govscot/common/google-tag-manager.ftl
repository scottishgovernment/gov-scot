<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<@hst.headContribution category="googleTagManager">
<!-- Google Tag Manager (GTM) -->
<script id="gtm-datalayer">
    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push({
        'gtm.whitelist': ['google', 'jsm', 'lcl'],
        'format' : '${gtmName}',
        'siteid' : '${gtmId}'
    });
</script>
</@hst.headContribution>

<#assign gtmAuthDev = "dLPjkzRtdQ0YqBmSCJ0GfA" />
<#assign gtmEnvDev = "58" />

<@hst.headContribution category="googleTagManager">
<#if useLiveAnalytics??>
<script>
initGTM = function () {
    (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
        new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
        j=d.createElement(s),dl=l!='dataLayer'?'&amp;l='+l:'';j.async=true;j.src=
        'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
    })(window,document,'script','dataLayer','GTM-PZ62X92');
}
</script>
<#else>
<script>
initGTM = function () {
    (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
        new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
        j=d.createElement(s),dl=l!='dataLayer'?'&amp;l='+l:'';j.async=true;j.src=
        'https://www.googletagmanager.com/gtm.js?id='+i+dl+ '&amp;gtm_auth=${gtmAuthDev}&amp;gtm_preview=env-${gtmEnvDev}&amp;gtm_cookies_win=x';f.parentNode.insertBefore(j,f);
    })(window,document,'script','dataLayer','GTM-PZ62X92');
}
</script>
</#if>
</@hst.headContribution>

    <!-- Google Tag Manager (noscript) -->
    <#if useLiveAnalytics??>
    <noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-PZ62X92"
        height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
    <#else>
    <noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-PZ62X92&gtm_auth=${gtmAuthDev}&gtm_preview=env-${gtmEnvDev}&gtm_cookies_win=x"
        height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
    </#if>
    <!-- End Google Tag Manager (noscript) -->

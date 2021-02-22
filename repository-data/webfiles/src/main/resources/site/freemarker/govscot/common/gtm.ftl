<#include "../../include/imports.ftl">

<@hst.headContribution category="googleTagManager">
<!-- Google Tag Manager (GTM) -->
<script id="gtm-datalayer">
    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push({
        'gtm.whitelist': ['google', 'jsm', 'lcl'],
            <#if gtmName??>'format' : '${gtmName}',</#if>
            <#if gtmId??>'siteid' : '${gtmId?js_string}'</#if>
    });
</script>
</@hst.headContribution>

<@hst.headContribution category="googleTagManager">
<script id="gtm-script">
    initGTM = function () {
        (function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
                new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
                j=d.createElement(s),dl=l!='dataLayer'?'&amp;l='+l:'';j.async=true;j.src=
                'https://www.googletagmanager.com/gtm.js?id='+i+dl+'<#if gtmAuth?has_content>&amp;gtm_auth=${gtmAuth}</#if><#if gtmEnv?has_content>&amp;gtm_preview=${gtmEnv}&amp;gtm_cookies_win=x</#if>';f.parentNode.insertBefore(j,f);
        })(window,document,'script','dataLayer','${gtmContainerId}');
    }
</script>
</@hst.headContribution>

<!-- Google Tag Manager (noscript) -->
<noscript id="gtm-noscript"><iframe src="https://www.googletagmanager.com/ns.html?id=${gtmContainerId}<#if gtmAuth?has_content>&amp;gtm_auth=${gtmAuth}</#if><#if gtmEnv?has_content>&amp;gtm_preview=${gtmEnv}&amp;gtm_cookies_win=x</#if>"
                                    height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<!-- End Google Tag Manager (noscript) -->

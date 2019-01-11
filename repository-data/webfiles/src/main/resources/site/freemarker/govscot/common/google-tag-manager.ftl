<#include "../../include/imports.ftl">

<#if useLiveAnalytics??>
    <#assign accountid = "UA-78735385-5" />
    <#assign containerid = "GTM-PZ62X92" />
    <#assign includewhitelist = true/>
    <#assign isactive = true/>
<#else>
    <#assign accountid = "UA-78735385-4" />
    <#assign containerid = "GTM-WMP56LD" />
    <#assign includewhitelist = false/>
    <#assign isactive = true/>
</#if>

<!-- Google Tag Manager (GTM) -->
<script id="gtm-datalayer">
    dataLayer = [
    {
        <#if includewhitelist>
        'gtm.whitelist': ['google', 'jsm', 'lcl', 'mf'],
        </#if>
        'format' : '${gtmName}',
        'siteid' : '${gtmId}'
    }
    ];
</script>

<noscript  id="gtm-noscript"><iframe src="//www.googletagmanager.com/ns.html?id=${containerid}"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<script id="gtm">(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push(
{'gtm.start': new Date().getTime(),event:'gtm.js'}
);var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','${containerid}');</script>
<!-- End Google Tag Manager -->


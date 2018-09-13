<#include "../../include/imports.ftl">

<#if useLiveAnalytics??>
    <#assign accountid = "UA-78735385-5" />
    <#assign containerid = "GTM-PZ62X92" />
    <#assign includewhitelist = true/>
    <#assign isactive = true/>
    <#assign optimizecontainerid = "GTM-5876ZHM" />
<#else>
    <#assign accountid = "UA-78735385-4" />
    <#assign containerid = "GTM-WMP56LD" />
    <#assign includewhitelist = false/>
    <#assign isactive = true/>
    <#assign optimizecontainerid = "GTM-MF68M3N" />
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

<noscript  id="gtm-noscript"><iframe src="//www.googletagmanager.com/ns.html?id=<@fmt.message key='containerid'/>"
height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
<script id="gtm">(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push(
{'gtm.start': new Date().getTime(),event:'gtm.js'}
);var f=d.getElementsByTagName(s)[0],
j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
'//www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
})(window,document,'script','dataLayer','${containerid}');</script>
<!-- End Google Tag Manager -->

<#--  Modified GA compliant GO code for on page implementation. Note these examples have our UA-ID (GOV.SCOT (Test)) and our GO container ID (devTest GO container) -->

<script>
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
    (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
    m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');

    ga('create', '${accountid}', 'auto', {allowLinker: true}); // {allowLinker: true} is optional at this point, however, it may satisfy portion of requirements for CDT
    ga('require', '${optimizecontainerid}'); // removed the ga('send', 'pageview') tracker portion in compliance with Google Optimize guidelines

</script>

<#--  Page hiding snippet for on page implementation -->

<!-- Page-hiding snippet (recommended)  -->
<style>.async-hide { opacity: 0 !important} </style>
<script>(function(a,s,y,n,c,h,i,d,e){s.className+=' '+y;h.start=1*new Date;
h.end=i=function(){s.className=s.className.replace(RegExp(' ?'+y),'')};
(a[n]=a[n]||[]).hide=h;setTimeout(function(){i();h.end=null;},c);h.timeout=c;
})(window,document.documentElement,'async-hide','dataLayer',4000,
{'${optimizecontainerid}':true});</script>

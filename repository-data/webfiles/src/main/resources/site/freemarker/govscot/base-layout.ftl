<#ftl output_format="HTML">
<!doctype html>
<#include "../include/imports.ftl">
<html dir="ltr" lang="en">
<head>
    <meta charset="UTF-8">
    <@hst.headContributions categoryIncludes="noscriptHead"/>
    <@hst.headContributions categoryIncludes="dataLayer"/>
    <!-- dataLayer code MUST be higher than google tag manager code -->
    <@hst.headContributions categoryIncludes="googleTagManager"/>
    <@hst.headContributions categoryIncludes="pageTitle"/>
    <@hst.headContributions categoryIncludes="schema"/>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" type="text/css" href="<@hst.webfile path="/assets/css/main.css"/>"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">
    <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,700,400italic&display=swap' rel='stylesheet' type='text/css'>

    <link rel="shortcut icon" href='<@hst.link path="favicon.ico" />' type="image/x-icon" />
    <link rel="apple-touch-icon" sizes="180x180" href='<@hst.link path="apple-touch-icon.png" />'>
    <link rel="icon" type="image/png" sizes="32x32" href='<@hst.link path="favicon-32x32.png" />'>
    <link rel="icon" type="image/png" sizes="16x16" href='<@hst.link path="favicon-16x16.png" />'>
    <link rel="mask-icon" href='<@hst.link path="safari-pinned-tab.svg" />' color="#0065bd">
    <meta name="msapplication-TileColor" content="#0065bd">
    <meta name="theme-color" content="#ffffff">

    <@hst.headContributions categoryExcludes="schema, noscriptHead, footerScripts, pageTitle, dataLayer, googleTagManager" xhtml=true/>

    <script>
        BR = window.BR || {};
        BR.webfile = function(path) {
            return '<@hst.webfile path="/"/>' + path;
        };
    </script>
    <script>
        var htmlClass = document.documentElement.getAttribute('class') || '';
        document.documentElement.setAttribute('class', (htmlClass ? htmlClass + ' ' : '') + 'js-enabled');
    </script>
</head>
<body>
    <input type="hidden" id="site-root-path" value="<@hst.link path="/"/>" />
    <@hst.include ref="googletagmanager"/>

    <#include 'common/accessibility-links.ftl' />

    <span id="page-top"></span>

    <@hst.include ref="preview-indicator"/>

    <div class="ds_page">
        <div class="ds_page__top">
            <#include 'common/notifications.ftl' />
            <@hst.include ref="menu"/>

            <#if !(isSearchpage!true)>
                <@hst.include ref="search"/>
            </#if>
        </div>

        <div class="ds_page__middle">
            <#if !(isPageNotFound!false) && !(isHomepage!false)>
                <div class="ds_wrapper">
                    <@hst.include ref="breadcrumb"/>
                </div>
            </#if>
            <@hst.include ref="main"/>
        </div>

        <#include "common/back-to-top.ftl" />

        <div class="ds_page__bottom">
            <@hst.include ref="footer"/>
        </div>
    </div>

    <script type="module" src="<@hst.webfile path="/assets/scripts/global.js"/>"></script>
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/global.es5.js"/>"></script>

    <script defer src="<@hst.link path="/assets/scripts/vendor/svgxuse.min.js"/>"></script>
    <@hst.headContributions categoryIncludes="footerScripts" xhtml=true/>

</body>
</html>

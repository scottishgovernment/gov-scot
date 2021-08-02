<!doctype html>
<#include "../include/imports.ftl">
<!--[if lte IE 9]><html dir="ltr" lang="en" class="old-ie"><![endif]-->
<!--[if gt IE 9]><!--><html dir="ltr" lang="en"><!--<![endif]-->
<head>
    <meta charset="UTF-8">
    <@hst.headContributions categoryIncludes="noscriptHead"/>
    <@hst.headContributions categoryIncludes="dataLayer"/>
    <!-- dataLayer code MUST be higher than google tag manager code -->
    <@hst.headContributions categoryIncludes="googleTagManager"/>
    <@hst.headContributions categoryIncludes="pageTitle"/>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" type="text/css" href="<@hst.webfile path="/assets/css/main.css"/>"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">

    <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>

    <link rel="shortcut icon" href='<@hst.link path="favicon.ico" />' type="image/x-icon" />
    <link rel="apple-touch-icon" sizes="180x180" href='<@hst.link path="apple-touch-icon.png" />'>
    <link rel="icon" type="image/png" sizes="32x32" href='<@hst.link path="favicon-32x32.png" />'>
    <link rel="icon" type="image/png" sizes="16x16" href='<@hst.link path="favicon-16x16.png" />'>
    <link rel="mask-icon" href='<@hst.link path="safari-pinned-tab.svg" />' color="#0065bd">
    <meta name="msapplication-TileColor" content="#0065bd">
    <meta name="theme-color" content="#ffffff">

    <#--  <@hst.headContributions categoryIncludes="htmlHead" xhtml=true/>  -->
    <@hst.headContributions categoryExcludes="noscriptHead, footerScripts, pageTitle, dataLayer, googleTagManager" xhtml=true/>

</head>

<body class="fontawesome site-header__container" >
    <@hst.include ref="googletagmanager"/>
    <script src="<@hst.link path='/assets/scripts/vendor/jquery.min.js'/>"></script>

    <#include 'common/accessibility-links.ftl' />
    <#include 'common/notifications.ftl' />

    <div id="main-wrapper">

        <@hst.include ref="menu"/>

        <#if !(isHomepage!false)>
        <div class="wrapper">
            <!-- search & breadcrumbs -->
            <!-- only show when not on homepage -->
                <div class="breadcrumbs__container">

                    <div class="grid"><!--

                        --><div class="grid__item medium--four-twelfths large--three-twelfths push--medium--eight-twelfths push--large--nine-twelfths hidden-xsmall">
                            <#if !(isSearchpage!true)>
                            <@hst.include ref="search"/>
                            </#if>
                        </div><!--

                        --><div class="grid__item medium--seven-twelfths large--seven-twelfths pull--medium--four-twelfths pull--large--three-twelfths">
                            <#if !(isPageNotFound!false)>
                            <nav role="navigation">
                                <@hst.include ref="breadcrumb"/>
                            </nav>
                            </#if>
                        </div><!--
                    --></div>
                </div>
            </#if>

        <!-- body / main -->
            <@hst.include ref="main"/>
        <#if !(isHomepage!false)>
            </div>
        </#if>

    </div>

    <!-- footer -->

    <@hst.include ref="footer"/>

    <#if isHomepage??><script>var headerSearch = document.querySelector('.ds_site-header__search'); headerSearch.parentNode.removeChild(headerSearch);</script></#if>
    <#if isSearchpage??><script>var headerSearch = document.querySelector('.ds_site-header__search'); headerSearch.parentNode.removeChild(headerSearch);</script></#if>

    <script type="module" src="<@hst.webfile path="/assets/scripts/global.js"/>"></script>
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/global.es5.js"/>"></script>

    <script defer src="<@hst.link path="/assets/scripts/vendor/svgxuse.min.js"/>"></script>
    <@hst.headContributions categoryIncludes="footerScripts" xhtml=true/>

</body>
</html>

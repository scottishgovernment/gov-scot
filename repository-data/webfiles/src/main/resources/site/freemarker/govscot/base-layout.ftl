<!doctype html>
<#include "../include/imports.ftl">
<!--[if lte IE 9]><html dir="ltr" lang="en" class="old-ie"><![endif]-->
<!--[if gt IE 9]><!--><html dir="ltr" lang="en"><!--<![endif]-->
<head>
    <meta charset="UTF-8">

    <@hst.headContributions categoryIncludes="pageTitle"/>

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="content">

    <link rel="stylesheet" type="text/css" href="<@hst.webfile path="/assets/css/main.css"/>"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">

    <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>

    <@hst.webfile var="favicon" path="/assets/images/icons/favicon.png" />
    <link rel="icon" href="${favicon}" type="image/x-icon" />
    <link rel="shortcut icon" href="${favicon}" type="image/x-icon" />
    

    <link rel="apple-touch-icon" href="<@hst.webfile path="/images/apple-touch-icon.png"/>" />

    <#--  <@hst.headContributions categoryIncludes="htmlHead" xhtml=true/>  -->
    <@hst.headContributions categoryExcludes="footerScripts, pageTitle" xhtml=true/>

</head>
<body class="fontawesome site-header__container" >
    <script src="<@hst.link path="/assets/scripts/vendor/jquery.min.js"/>"></script>

    <@hst.include ref="googletagmanager"/>

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
                            <#if !(isSearchpage!false)>
                            <@hst.include ref="search"/>
                            </#if>
                        </div><!--

                        --><div class="grid__item medium--seven-twelfths large--seven-twelfths pull--medium--four-twelfths pull--large--three-twelfths">
                            <#if !(isPageNotFound!false)>
                            <nav>
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

    <script src="<@hst.webfile path="/assets/scripts/global.js"/>" type="text/javascript"></script>
    <script defer src="<@hst.link path="/assets/scripts/vendor/svgxuse.min.js"/>"></script>
    <@hst.headContributions categoryIncludes="footerScripts" xhtml=true/>

</body>
</html>

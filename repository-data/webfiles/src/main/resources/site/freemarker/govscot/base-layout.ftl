<!doctype html>
<#include "../include/imports.ftl">
<!--[if lte IE 9]><html dir="ltr" lang="en" class="old-ie"><![endif]-->
<!--[if gt IE 9]><!--><html dir="ltr" lang="en"><!--<![endif]-->
<head>
    <meta charset="UTF-8">

    <title>gov.scot</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="content">

    <link rel="stylesheet" type="text/css" href="<@hst.webfile path="/assets/css/main.css"/>"/>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.4.0/css/font-awesome.min.css">

    <link href='https://fonts.googleapis.com/css?family=Roboto:100,300,400,500,700,900,400italic' rel='stylesheet' type='text/css'>

    <link rel="icon" href="<@hst.webfile path="/images/favicon.ico"/>" type="image/x-icon" />
    <link rel="shortcut icon" href="<@hst.webfile path="/images/favicon.ico"/>" type="image/x-icon" />

    <link rel="apple-touch-icon" href="<@hst.webfile path="/images/apple-touch-icon.png"/>" />

    <script src="<@hst.link path="/assets/scripts/vendor/jquery.min.js"/>"></script>

    <#--  <@hst.headContributions categoryIncludes="htmlHead" xhtml=true/>  -->
    <@hst.headContributions categoryExcludes="footerScripts" xhtml=true/>

</head>
<body class="fontawesome site-header__container" >

    <div id="main-wrapper">

        <@hst.include ref="menu"/>

        <!-- search & breadcrumbs -->
        <div class="wrapper">
            <div class="breadcrumbs__container">
                <div class="grid"><!--
                    --><div class="grid__item medium--four-twelfths large--three-twelfths push--medium--eight-twelfths push--large--nine-twelfths hidden-xsmall">
                        <div class="search-box">


                            <form class="search-box__form" method="GET" action="search.html">
                                <label class="search-box__label hidden" for="searchbox-inputtext">Search</label>
                                <input type="text" class="search-box__input" id="searchbox-inputtext" name="query"
                                    placeholder="Search site" />
                                <button type="submit" title="search" class="search-box__button button button--primary">
                                    <span class="icon icon--search-white"></span>
                                    <span class="hidden">Search</span>
                                </button>
                            </form>
                        </div>
                    </div><!--

                    --><div class="grid__item medium--seven-twelfths large--seven-twelfths pull--medium--four-twelfths pull--large--three-twelfths">
                        <nav>
                          <@hst.include ref="breadcrumb"/>
                        </nav>
                    </div><!--
                --></div>
            </div>
        </div>

        <!-- body / main -->
        <div class="wrapper">
            <@hst.include ref="main"/>
        </div>

    </div>

    <!-- footer -->

    <@hst.include ref="footer"/>

    <script src="<@hst.webfile path="/assets/scripts/global.js"/>" type="text/javascript"></script>
    <@hst.headContributions categoryIncludes="footerScripts" xhtml=true/>

</body>
</html>

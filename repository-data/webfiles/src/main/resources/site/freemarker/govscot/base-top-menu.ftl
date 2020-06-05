<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<header class="ds_site-header" role="banner" id="page-top">
    <div class="wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <@hst.link var="home" siteMapItemRefId="root" />
                <a data-header="header-logo" class="ds_site-branding__logo  ds_site-branding__link" href="${home}">
                    <img class="ds_site-branding__logo-image" alt="Scottish Government" src="<@hst.link path='assets/images/logos/scotgovlogo.svg' />"/>
                </a>

            </div>
            <@hst.include ref="search"/>
        </div>

        <nav role="navigation" data-module="ds-site-navigation">
            <button class="js-toggle-menu  ds_mobile-navigation__button" aria-expanded="false" aria-controls="mobile-navigation-menu">
                <span class="ds_site-header__control-text">Menu</span>
                <svg class="ds_icon  ds_site-header__control-icon" role="img"><use xlink:href="${iconspath}#menu-21"></use></svg>
                <svg class="ds_icon  ds_site-header__control-icon--close  ds_site-header__control-icon" role="img"><use xlink:href="${iconspath}#close-21"></use></svg>
            </button>

            <div id="mobile-navigation-menu" class="ds_mobile-navigation" data-offsetselector=".ds_site-header">
                <div class="ds_mobile-navigation__content">

                    <div class="ds_mobile-navigation__block">
                        <div class="ds_site-search">
                            <form role="search" class="ds_site-search__form" method="GET" action="<@hst.link path='/search/'/>">
                                <label class="ds_site-search__label hidden" for="site-search--mobile">Search</label>

                                <div class="ds_site-search__input-group">
                                    <input name="q" required="" id="site-search--mobile" class="ds_site-search__input" type="text" placeholder="Search" autocomplete="off" />
                                    <input name="cat" value="sitesearch" hidden>
                                    <button type="submit" title="search" class="ds_site-search__button  button  button--primary">
                                        <svg class="ds_icon  ds_site-search__icon" role="img"><use xlink:href="${iconspath}#search"></use></svg>

                                        <span class="hidden">Search gov.scot</span>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>


                    <#if menu??>
                        <#if menu.siteMenuItems??>
                            <div class="ds_mobile-navigation__block">
                                <ul class="ds_mobile-navigation__list"><!--
                                    <#list menu.siteMenuItems as item>
                                        <#if item.name == 'Statistics and research' && StatisticsandresearchMenu?? && StatisticsandresearchMenu == false>
                                        <#else>
                                            <#if item.hstLink??>
                                                <#assign href><@hst.link link=item.hstLink /></#assign>
                                            <#elseif item.externalLink??>
                                                <#assign href>${item.externalLink}</#assign>
                                            </#if>
                                            <#if item.selected || item.expanded>
                                            --><li class="ds_mobile-navigation__item">
                                                    <a class="ds_mobile-navigation__link ds_current" href="${href}" itemprop="url" data-header="header-link-${item?index + 1}" data-gtm="nav-main">${item.name?html}</a>
                                                </li><!--
                                            <#else>
                                            --><li class="ds_mobile-navigation__item">
                                                    <a class="ds_mobile-navigation__link" href="${href}" itemprop="url" data-header="header-link-${item?index + 1}" data-gtm="nav-main">${item.name?html}</a>
                                                </li><!--
                                            </#if>
                                        </#if>
                                    </#list>
                                --></ul>
                            </div>
                        </#if>
                    <@hst.cmseditmenu menu=menu/>
                    <#else>
                        <#if editMode>
                            <h5>[Menu Component]</h5>
                            <sub>Click to edit Menu</sub>
                        </#if>
                    </#if>

                    <button type="button" class="ds_mobile-navigation__backdrop  js-close-menu" aria-expanded="false" aria-controls="mobile-navigation-menu">
                        <span class="hidden">Close menu</span>
                    </button>
                </div>
            </div>
        </nav>
    </div>

    <#if menu??>
        <#if menu.siteMenuItems??>
            <div class="ds_site-header__navigation">
                <div class="wrapper">
                    <nav class="ds_site-navigation">
                        <ul class="ds_site-navigation__list"><!--
                            <#list menu.siteMenuItems as item>

                                <#if item.name == 'Statistics and research' && StatisticsandresearchMenu?? && StatisticsandresearchMenu == false>
                                <#else>
                                    <#if item.hstLink??>
                                        <#assign href><@hst.link link=item.hstLink /></#assign>
                                    <#elseif item.externalLink??>
                                        <#assign href>${item.externalLink}</#assign>
                                    </#if>
                                    <#if item.selected || item.expanded>
                                 --><li class="ds_site-navigation__item">
                                        <a class="ds_site-navigation__link ds_current" href="${href}" itemprop="url" data-header="header-link-${item?index + 1}" data-gtm="nav-main">${item.name?html}</a>
                                    </li><!--
                                    <#else>
                                 --><li class="ds_site-navigation__item">
                                        <a class="ds_site-navigation__link" href="${href}" itemprop="url" data-header="header-link-${item?index + 1}" data-gtm="nav-main">${item.name?html}</a>
                                    </li><!--
                                    </#if>
                                </#if>
                            </#list>
                     --></ul>
                    </nav>
                </div>
            </div>
        </#if>
        <@hst.cmseditmenu menu=menu/>
        <#else>
            <#if editMode>
                <h5>[Menu Component]</h5>
                <sub>Click to edit Menu</sub>
            </#if>
    </#if>
</header>





<div class="mobile-layer__overlay js-mobile-search-overlay mobile-layer__overlay--grey js-mobile-search-toggle"></div>
<div class="mobile-layer__overlay js-mobile-nav-overlay mobile-layer__overlay--grey js-mobile-nav-toggle"></div>

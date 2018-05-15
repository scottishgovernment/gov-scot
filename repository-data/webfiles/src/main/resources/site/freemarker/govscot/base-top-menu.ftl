<#include "../include/imports.ftl">

<header class="site-header" id="page-top">
    <div class="wrapper">
        <div class="site-brand">
            <div class="site-brand__link">
                <@hst.link var="home" siteMapItemRefId="root" />
                <a class="site-brand__logo" data-gtm="main-nav" href="${home}">
                    <img alt="Scottish Government" src="<@hst.link path='assets/images/logos/scotgovlogo.svg' />"/>
                </a>
            </div>
        </div>

        <div class="visible-xsmall site-header__buttons">

            <button aria-controls="mobile-search" type="button" class="
                site-header__button  site-header__button--search
                js-mobile-search-toggle">
                <span class="site-header__icon  site-header__icon--search">Search</span>
            </button>

            <button aria-controls="mobile-navigation" type="button" class="
                site-header__button  site-header__button--nav
                js-mobile-nav-toggle">
                <span class="site-header__icon site-header__icon--nav">Menu</span>
            </button>
        </div>

        <#if menu??>
            <#if menu.siteMenuItems??>
                <nav class="main-nav hidden-xsmall">
                    <div class="main-nav__wrap  scrollable  scrollable--mobile-only">
                        <ul class="main-nav__list  scrollable__content" id="main-navigation"><!--
                                <#list menu.siteMenuItems as item>
                                <#if item.hstLink??>
                                    <#assign href><@hst.link link=item.hstLink /></#assign>
                                <#elseif item.externalLink??>
                                    <#assign href>${item.externalLink}</#assign>
                                </#if>

                                <#if item.selected || item.expanded>
                             --><li class="main-nav__item">
                                    <a class="main-nav__link  main-nav__link--selected" href="${href}" itemprop="url" data-gtm="nav-main">${item.name?html}</a>
                                </li><!--
                                <#else>
                             --><li class="main-nav__item">
                                    <a class="main-nav__link" href="${href}" itemprop="url" data-gtm="nav-main">${item.name?html}</a>
                                </li><!--
                                </#if>
                            </#list>
                     --></ul>
                    </div>
                </nav>
            </#if>
            <@hst.cmseditmenu menu=menu/>
        <#else>
            <#if editMode>
                <h5>[Menu Component]</h5>
                <sub>Click to edit Menu</sub>
            </#if>
        </#if>
    </div>
</header>

<div class="mobile-layer mobile-search visible-xsmall" aria-expanded="false" id="mobile-search">
    <div class="mobile-layer__overlay mobile-layer__overlay--grey js-mobile-search-toggle"></div>
    <div class="mobile-layer__content">

    </div>
</div>

<div class="mobile-layer mobile-nav visible-xsmall" aria-expanded="false" id="mobile-navigation">
    <div class="mobile-layer__overlay mobile-layer__overlay--white js-mobile-nav-toggle"></div>
    <div class="mobile-layer__content">
    <#if menu??>
        <#if menu.siteMenuItems??>
            <nav class="main-nav main-nav--mobile">
                <div class="main-nav__wrap  scrollable  scrollable--mobile-only">
                    <ul class="main-nav__list  scrollable__content" id="main-navigation"><!--
                        <#list menu.siteMenuItems as item>
                            <#if item.hstLink??>
                                <#assign href><@hst.link link=item.hstLink /></#assign>
                            <#elseif item.externalLink??>
                                <#assign href>${item.externalLink}</#assign>
                            </#if>

                            <#if item.selected || item.expanded>
                                <#if item.name == "About">
                                 --><li class="main-nav__item">
                                        <div class="visible-xsmall">
                                            <@hst.include ref="about-menu"/>
                                        </div>
                                    </li><!--
                                    <#else>
                                     --><li class="main-nav__item">
                                            <a class="main-nav__link  main-nav__link--selected" href="${href}" itemprop="url" data-gtm="nav-main">${item.name?html}</a>
                                        </li><!--
                                </#if>
                            <#else>
                             --><li class="main-nav__item">
                                    <a class="main-nav__link" href="${href}" itemprop="url" data-gtm="nav-main">${item.name?html}</a>
                                </li><!--
                            </#if>
                         </#list>
                 --></ul>
                </div>
            </nav>
        </#if>
        <@hst.cmseditmenu menu=menu/>
    <#else>
        <#if editMode>
            <h5>[Menu Component]</h5>
            <sub>Click to edit Menu</sub>
        </#if>
    </#if>
    </div>
</div>

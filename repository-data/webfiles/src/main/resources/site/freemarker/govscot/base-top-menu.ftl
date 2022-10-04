<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<!--noindex-->
<header class="ds_site-header" role="banner" id="page-top">
    <div class="ds_wrapper">
        <div class="ds_site-header__content">
            <div class="ds_site-branding">
                <@hst.link var="home" siteMapItemRefId="root" />
                <a data-header="header-logo" class="ds_site-branding__logo  ds_site-branding__link" href="${home}">
                    <img width="269" height="40" class="ds_site-branding__logo-image" alt="Scottish Government" src="<@hst.link path='assets/images/logos/scotgovlogo.svg' />"/>
                </a>
            </div>

            <div class="ds_site-header__controls">
                <label aria-controls="mobile-navigation" class="ds_site-header__control  js-toggle-menu" for="menu">
                    <span class="ds_site-header__control-text">Menu</span>
                    <svg class="ds_icon  ds_site-header__control-icon" aria-hidden="true" role="img"><use href="${iconspath}#menu"></use></svg>
                    <svg class="ds_icon  ds_site-header__control-icon  ds_site-header__control-icon--active-icon" aria-hidden="true" role="img"><use href="${iconspath}#close"></use></svg>
                </label>
            </div>

            <#if menu??>
                <#if menu.siteMenuItems??>
                    <input class="ds_site-navigation__toggle" id="menu" type="checkbox"/>
                    <nav id="mobile-navigation" class="ds_site-navigation  ds_site-navigation--mobile" data-module="ds-mobile-navigation-menu">
                        <ul class="ds_site-navigation__list">
                            <#list menu.siteMenuItems as item>
                                <#if item.hstLink??>
                                    <#assign href><@hst.link link=item.hstLink /></#assign>
                                <#elseif item.externalLink??>
                                    <#assign href>${item.externalLink}</#assign>
                                </#if>
                                <#if item.selected || item.expanded>
                                    <li class="ds_site-navigation__item">
                                        <a class="ds_site-navigation__link  ds_current" href="${href}">${item.name}</a>
                                    </li>
                                <#else>
                                    <li class="ds_site-navigation__item">
                                        <a class="ds_site-navigation__link" href="${href}">${item.name}</a>
                                    </li>
                                </#if>
                            </#list>
                        </ul>
                    </nav>
                </#if>
            </#if>

            <#if hideSearch>
            <#else>
                <div class="ds_site-header__search">
                    <@hst.include ref="search"/>
                </div>
            </#if>
        </div>
    </div>

    <#if menu??>
        <#if menu.siteMenuItems??>
            <div class="ds_site-header__navigation">
                <div class="ds_wrapper">
                    <nav role="navigation" class="ds_site-navigation">
                        <ul class="ds_site-navigation__list">
                            <#list menu.siteMenuItems as item>
                                <#if item.name == 'Statistics and research' && StatisticsandresearchMenu?? && StatisticsandresearchMenu == false>
                                <#else>
                                    <#if item.hstLink??>
                                        <#assign href><@hst.link link=item.hstLink /></#assign>
                                    <#elseif item.externalLink??>
                                        <#assign href>${item.externalLink}</#assign>
                                    </#if>
                                    <#if item.selected || item.expanded>
                                        <li class="ds_site-navigation__item">
                                            <a class="ds_site-navigation__link  ds_current" href="${href}">${item.name}</a>
                                        </li>
                                    <#else>
                                        <li class="ds_site-navigation__item">
                                            <a class="ds_site-navigation__link" href="${href}">${item.name}</a>
                                        </li>
                                    </#if>
                                </#if>
                            </#list>
                        </ul>
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
<!--endnoindex-->

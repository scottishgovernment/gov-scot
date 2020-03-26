<#include "../../include/imports.ftl">

<#if aboutMenuItem??>
 <nav aria-label="Sections" class="ds_side-navigation" data-module="ds-side-navigation">
        <input type="checkbox" style="display:none;" class="fully-hidden js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
        <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation"><span class="hidden">Show all</span> Pages in this section <span class="ds_side-navigation__expand-indicator"></span></label>
            <ul class="ds_side-navigation__list  ds_side-navigation__list--root" id="side-navigation-root">
                <li class="ds_side-navigation__item  ds_side-navigation__item--has-children  ds_side-navigation__item--has-children--expanded">
                    <a href="<@hst.link link=aboutMenuItem.hstLink/>" data-navigation="sidenav-1" class="ds_side-navigation__link">${aboutMenuItem.name?html}</a>

                            <#if aboutMenuItem.childMenuItems?has_content>
                                <ul class="ds_side-navigation__list">
                                    <#list aboutMenuItem.childMenuItems as level1Item>
                                    <li class="ds_side-navigation__item">
                                        <a href="<@hst.link link=level1Item.hstLink/>" data-navigation="sidenav-1-1" class="ds_side-navigation__link">${level1Item.name?html}</a>
                                     
                                        <#if level1Item.childMenuItems?has_content>
                                            <ul class="ds_side-navigation__list">
                                                <#list level1Item.childMenuItems as level2Item>
                                                    <li class="ds_side-navigation__item">
                                                        <a href="<@hst.link link=level2Item.hstLink/>" data-navigation="sidenav-1-1-2" class="ds_side-navigation__link">${level2Item.name?html}</a>
                                                    </li>
                                                </#list>
                                            </ul>
                                        </#if>
                                    </li>
                                    </#list>
                                </ul>
                            </#if>
                </li>
            </ul>
    </nav>

    <aside class="page-group page-group--section-nav <#if isMobile??>page-group--mobile</#if>">
        <ul class="page-group__list">
            <li class="page-group__item page-group__item--level-0">
                <#if aboutMenuItem.selected>
                        <span class="page-group__link page-group__link--level-0 page-group__link--selected">${aboutMenuItem.name?html}</span>
                <#else>
                        <a class="page-group__link page-group__link--level-0" href="<@hst.link link=aboutMobileMenuItem.hstLink/>">${aboutMenuItem.name?html}</a>
                </#if>

                <#if aboutMenuItem.childMenuItems?has_content>
                    <ul class="page-group__list">
                        <#list aboutMenuItem.childMenuItems as level1Item>
                            <li class="page-group__item page-group__item--level-1">
                                <#if level1Item.selected>
                                    <span class="page-group__link page-group__link--level-1 page-group__link--selected">${level1Item.name?html}</span>
                                <#else>
                                    <a class="page-group__link page-group__link--level-1" href="<@hst.link link=level1Item.hstLink/>">${level1Item.name?html}</a>
                                </#if>

                                <#if level1Item.childMenuItems?has_content>
                                    <ul class="page-group__list">
                                        <#list level1Item.childMenuItems as level2Item>
                                            <li class="page-group__item page-group__item--level-2">
                                                <#if level2Item.selected>
                                                    <span class="page-group__link page-group__link--level-2 page-group__link--selected">${level2Item.name?html}</span>
                                                <#else>
                                                    <a class="page-group__link page-group__link--level-2" href="<@hst.link link=level2Item.hstLink/>">${level2Item.name?html}</a>
                                                </#if>
                                            </li>
                                        </#list>
                                    </ul>
                                </#if>
                            </li>
                        </#list>
                    </ul>
                </#if>
            </li>
        </ul>
    </aside>
</#if>
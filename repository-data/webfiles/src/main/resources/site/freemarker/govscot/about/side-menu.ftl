<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">

<#if aboutMenuItem??>
    <nav lang="en" aria-label="Sections" class="ds_side-navigation" data-module="ds-side-navigation">
        <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
        <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation"><span class="visually-hidden">Show all</span> Pages in this section <span class="ds_side-navigation__expand-indicator"></span></label>

        <ul class="ds_side-navigation__list" id="side-navigation-root">
            <li class="ds_side-navigation__item">
                <#if aboutMenuItem.selected>
                        <span class="ds_side-navigation__link  ds_current">${aboutMenuItem.name}</span>
                <#else>
                        <a class="ds_side-navigation__link" href="<@hst.link link=aboutMenuItem.hstLink/>">${aboutMenuItem.name}</a>
                </#if>

                <#if aboutMenuItem.childMenuItems?has_content>
                    <ul class="ds_side-navigation__list">
                        <#list aboutMenuItem.childMenuItems as level1Item>
                            <li class="ds_side-navigation__item">
                                <#if level1Item.selected>
                                    <span class="ds_side-navigation__link  ds_current">${level1Item.name}</span>
                                <#else>
                                    <a class="ds_side-navigation__link" href="<@hst.link link=level1Item.hstLink/>">${level1Item.name}</a>
                                </#if>

                                <#if level1Item.childMenuItems?has_content>
                                    <ul class="ds_side-navigation__list">
                                        <#list level1Item.childMenuItems as level2Item>
                                            <li class="ds_side-navigation__item">
                                                <#if level2Item.selected>
                                                    <span class="ds_side-navigation__link  ds_current">${level2Item.name}</span>
                                                <#else>
                                                    <a class="ds_side-navigation__link" href="<@hst.link link=level2Item.hstLink/>">${level2Item.name}</a>
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
    </nav>
</#if>

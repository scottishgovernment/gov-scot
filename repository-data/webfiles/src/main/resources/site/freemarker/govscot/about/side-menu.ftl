<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">

<#if aboutMenuItem??>
    <nav lang="en" aria-label="Sections" class="ds_side-navigation" data-module="ds-side-navigation">
        <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
        <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation"><span class="visually-hidden">Show all</span> Pages in this section <span class="ds_side-navigation__expand-indicator"></span></label>

        <ul class="ds_side-navigation__list" id="side-navigation-root">
            <li class="ds_side-navigation__item">
                <a class="ds_side-navigation__link<#if aboutMenuItem.selected>  ds_current</#if>" href="<@hst.link link=aboutMenuItem.hstLink/>"<#if aboutMenuItem.selected> aria-current="page"</#if>>${aboutMenuItem.name}</a>

                <#if aboutMenuItem.childMenuItems?has_content>
                    <ul class="ds_side-navigation__list">
                        <#list aboutMenuItem.childMenuItems as level1Item>
                            <li class="ds_side-navigation__item">
                                <a class="ds_side-navigation__link<#if level1Item.selected>  ds_current</#if>" href="<@hst.link link=level1Item.hstLink/>"<#if level1Item.selected> aria-current="page"</#if>>${level1Item.name}</a>
                                <#if level1Item.childMenuItems?has_content>
                                    <ul class="ds_side-navigation__list">
                                        <#list level1Item.childMenuItems as level2Item>
                                            <li class="ds_side-navigation__item">
                                                <a class="ds_side-navigation__link<#if level2Item.selected>  ds_current</#if>" href="<@hst.link link=level2Item.hstLink/>"<#if level2Item.selected> aria-current="page"</#if>>${level2Item.name}</a>
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

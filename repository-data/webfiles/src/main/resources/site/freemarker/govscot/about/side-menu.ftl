<#include "../../include/imports.ftl">

<#if menuItems??>
    <aside class="page-group page-group--section-nav page-grouppage-subnav--tablet">
        <ul class="page-group__list">
            <#list menuItems as item>
                <li class="page-group__item page-group__item--level-0">
                    <#if item.selected>
                            <span class="page-group__link page-group__link--level-0 page-group__link--selected">${item.name?html}</span>
                    <#else>
                            <a class="page-group__link page-group__link--level-0" href="<@hst.link link=item.hstLink/>">${item.name?html}</a>
                    </#if>

                    <ul class="page-group__list">
                        <#list item.childMenuItems as level1Item>
                            <li class="page-group__item page-group__item--level-1">
                                <#if level1Item.selected>
                                    <span class="page-group__link page-group__link--level-1 page-group__link--selected">${level1Item.name?html}</span>
                                <#else>
                                    <a class="page-group__link page-group__link--level-1" href="<@hst.link link=level1Item.hstLink/>">${level1Item.name?html}</a>
                                </#if>

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
                            </li>
                        </#list>
                    </ul>
                </li>
            </#list>
        </ul>
    </aside>
</#if>


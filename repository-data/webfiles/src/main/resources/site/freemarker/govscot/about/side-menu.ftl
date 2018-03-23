<#include "../../include/imports.ftl">
<#if menu??>
    <#if menu.siteMenuItems??>
        <aside class="page-group page-group--section-nav page-grouppage-subnav--tablet">
            <ul class="page-group__list">
                <#list menu.siteMenuItems as item>
                    <#if item.selected>
                        <li class="page-group__item page-group__item--level-0">
                            <span class="page-group__link page-group__link--selected">${item.name?html}</>
                        </li>
                    <#else>
                        <li class="page-group__item page-group__item--level-0">
                            <a class="page-group__link" href="<@hst.link link=item.hstLink/>" itemprop="url" data-gtm="nav-main">${item.name?html}</a>
                        </li>
                    </#if>
                </#list>
            </ul>

        </aside>
    </#if>
<#else>
    <#if editMode>
        <h5>[Menu Component]</h5>
        <sub>Click to edit Menu</sub>
    </#if>
</#if>


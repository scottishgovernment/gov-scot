<#include "../include/imports.ftl">

<#if menu??>
<nav class="ds_side-navigation">
    <input type="checkbox" class="fully-hidden  js-toggle-side-navigation" id="show-side-navigation" aria-controls="side-navigation-root" />
    <label class="ds_side-navigation__expand  ds_link" for="show-side-navigation">Choose section <span class="ds_side-navigation__expand-indicator"></span></label>

    <ul class="ds_side-navigation__list">
        <#if menu.siteMenuItems??>
            <#list menu.siteMenuItems as item>
                <#if item.selected || item.expanded>
                    <li class="ds_side-navigation__item">

                        <#if item.selected>
                        <span href="<@hst.link link=item.hstLink/>" class="ds_side-navigation__link  ds_current">
                            ${item.name?html}
                        </span>
                        <#else>
                        <a href="<@hst.link link=item.hstLink/>" class="ds_side-navigation">
                            ${item.name?html}
                        </a>
                        </#if>

                        <#if item.childMenuItems??>
                            <ul class="ds_side-navigation__list">
                                <#list item.childMenuItems as item>
                                    <li class="ds_side-navigation__item">
                                        <#if item.selected>
                                        <span href="<@hst.link link=item.hstLink/>" class="ds_side-navigation  ds_current">
                                            ${item.name?html}
                                        </span>
                                        <#else>
                                        <a href="<@hst.link link=item.hstLink/>" class="ds_side-navigation">
                                            ${item.name?html}
                                        </a>
                                        </#if>
                                    </li>
                                </#list>
                            </ul>
                        </#if>
                    </li>
                </#if>
            </#list>
        </#if>
    </ul>
</nav>
</#if>

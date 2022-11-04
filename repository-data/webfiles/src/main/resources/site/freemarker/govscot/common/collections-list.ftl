<#ftl output_format="HTML">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#include "./macros/lang-attributes.ftl">

<#if collections?has_content>
    <#if collections?size == 1>
        <#assign description = 'a collection'/>
    <#else>
        <#assign description = '${collections?size} collections'/>
    </#if>

    <div <@revertlang document/> class="ds_accordion" data-module="ds-accordion">
        <div class="ds_accordion-item">
            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-collections" aria-labelledby="panel-collections-heading" />
            <div class="ds_accordion-item__header">
                <h3 id="panel-collections-heading" class="ds_accordion-item__title">
                    <svg class="ds_icon ds_!_margin-right--1">
                        <use href="${iconspath}#topic"></use>
                    </svg>
                    This document is part of ${description}
                </h3>
                <span class="ds_accordion-item__indicator"></span>
                <label class="ds_accordion-item__label" for="panel-collections"><span class="visually-hidden">Show this section</span></label>
            </div>
            <div class="ds_accordion-item__body">
                <ul class="ds_contents-nav__list">
                    <#list collections as collection>
                        <li class="ds_contents-nav__item">
                            <a class="ds_contents-nav__link  sg-meta__collection" href="<@hst.link hippobean=collection/>">${collection.title}</a>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
    </div>
</#if>

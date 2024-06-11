<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#assign termParam = parameters['q']![]/>
<#assign beginParam = parameters['begin']![]/>
<#assign endParam = parameters['end']![]/>

<#list termParam as nested>
    <#assign term = nested?j_string />
</#list>
<#list beginParam as nested>
    <#assign begin = nested?j_string />
</#list>
<#list endParam as nested>
    <#assign end = nested?j_string />
</#list>

<!-- todo: pull this into a separate stylesheet -->
<style>

.js-disabled-search {
    opacity: 0.5;
    pointer-events: none;
}
</style>

<div class="ds_search-filters">
    <input type="hidden" id="imagePath" value="<@hst.webfile path='assets/images/icons/' />">

    <div class="ds_details  ds_no-margin" data-module="ds-details">
        <input id="filters-toggle" type="checkbox" class="ds_details__toggle  visually-hidden">

        <label for="filters-toggle" class="ds_details__summary">
            <span class="visually-hidden">Show </span>Search filters
        </label>

        <div class="ds_skip-links  ds_skip-links--static">
            <ul class="ds_skip-links__list">
                <li class="ds_skip-links__item"><a class="ds_skip-links__link" href="#search-results">Skip to results</a></li>
            </ul>
        </div>

        <div class="ds_details__text">
            <#if !searchpagepath??>
                <#assign searchpagepath = "/search" />
            <#else>
                <#assign searchpagepath = "${searchpagepath}" />
            </#if>
            <form id="filters" method="GET" action="<@hst.link path=searchpagepath/>">
                <h3 class="ds_search-filters__title  ds_h4">Filter by</h3>

                <input type="hidden" value="${term}" name="q">

                <div class="ds_accordion  ds_accordion--small  ds_!_margin-top--0" data-module="ds-accordion">
                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-1" aria-labelledby="panel-1-heading" />
                        <div class="ds_accordion-item__header">
                            <h3 id="panel-1-heading" class="ds_accordion-item__title">
                                Content type
                                <div class="ds_search-filters__filter-count">
                                    <#assign count = 0/>
                                    <#list publicationTypes as item>
                                        <#if hstRequestContext.servletRequest.parameterMap["type"]??>
                                            <#list hstRequestContext.servletRequest.parameterMap["type"] as selectedItem>
                                                <#if selectedItem == item.key>
                                                    <#assign count = count + 1 />
                                                </#if>
                                            </#list>
                                        </#if>
                                    </#list>
                                    <#if count gt 0>
                                        (${count} selected)
                                    </#if>
                                </div>
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-1"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <fieldset>
                                <legend class="visually-hidden">Select which publication types you would like to see</legend>

                                <div class="ds_search-filters__scrollable">
                                    <div class="ds_search-filters__checkboxes">
                                        <#list publicationTypes as item>
                                            <#assign isSelected = false/>
                                            <#if hstRequestContext.servletRequest.parameterMap["type"]??>
                                                <#list hstRequestContext.servletRequest.parameterMap["type"] as selectedItem>
                                                    <#if selectedItem == item.key>
                                                        <#assign isSelected = true/>
                                                    </#if>
                                                </#list>
                                            </#if>

                                            <div class="ds_checkbox  ds_checkbox--small">
                                                <input
                                                    <#if isSelected == true>
                                                        checked=true
                                                    </#if>
                                                    id="${item.key}" name="type" value="${item.key}" class="ds_checkbox__input" type="checkbox" >
                                                <label for="${item.key}" class="ds_checkbox__label">${item.label}</label>
                                            </div>
                                        </#list>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>

                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-2" aria-labelledby="panel-2-heading" />
                        <div class="ds_accordion-item__header">
                            <h3 id="panel-2-heading" class="ds_accordion-item__title">
                                Topic
                                <div class="ds_search-filters__filter-count">
                                    <#assign count = 0/>
                                    <#list topics as item>
                                        <#if hstRequestContext.servletRequest.parameterMap["topic"]??>
                                            <#list hstRequestContext.servletRequest.parameterMap["topic"] as selectedItem>
                                                <#if selectedItem == item.node.name>
                                                    <#assign count = count + 1 />
                                                </#if>
                                            </#list>
                                        </#if>
                                    </#list>

                                    <#if count gt 0>
                                        (${count} selected)
                                    </#if>
                                </div>
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-2"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <fieldset>
                                <legend class="visually-hidden">Select which topics you would like to see</legend>

                                <div class="ds_search-filters__scrollable">
                                    <div class="ds_search-filters__checkboxes">
                                        <#list topics as item>
                                            <#assign isSelected = false/>
                                            <#if hstRequestContext.servletRequest.parameterMap["topic"]??>
                                                <#list hstRequestContext.servletRequest.parameterMap["topic"] as selectedItem>
                                                    <#if selectedItem == item.node.name>
                                                        <#assign isSelected = true/>
                                                    </#if>
                                                </#list>
                                            </#if>

                                            <div class="ds_checkbox  ds_checkbox--small">
                                                <input
                                                    <#if isSelected == true>
                                                        checked=true
                                                    </#if>
                                                    id="${item.node.name}" name="topic" value="${item.node.name}" class="ds_checkbox__input" type="checkbox">
                                                <label for="${item.node.name}" class="ds_checkbox__label">${item.title}</label>
                                            </div>
                                        </#list>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>

                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-3" aria-labelledby="panel-3-heading" />
                        <div class="ds_accordion-item__header">
                            <h3 id="panel-3-heading" class="ds_accordion-item__title">
                                Updated

                                <div class="ds_search-filters__filter-count">
                                    <#assign count = 0/>
                                    <#if begin?? && begin?length gt 0>
                                        <#assign count = count + 1/>
                                    </#if>
                                    <#if end?? && end?length gt 0>
                                        <#assign count = count + 1/>
                                    </#if>

                                    <#if count gt 0>
                                        (${count} selected)
                                    </#if>
                                </div>
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-3"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <fieldset id="filter-date-range" class="filters__fieldset">
                                <legend class="visually-hidden">Filter by date</legend>

                                <div class="ds_question">
                                    <div data-module="ds-datepicker" class="ds_datepicker" id="fromDatePicker">
                                        <label class="ds_label  ds_no-margin--bottom" for="date-from">Updated after</label>
                                        <p class="ds_hint-text  ds_!_margin-bottom--1">For example, 21/01/2022</p>
                                        <div class="ds_input__wrapper">
                                            <input value="<#if begin??>${begin}</#if>" name="begin" id="date-from" class="ds_input  ds_input--fixed-10" type="text">
                                        </div>
                                    </div>
                                </div>

                                <div class="ds_question">
                                    <div data-module="ds-datepicker" class="ds_datepicker" id="toDatePicker">
                                        <label class="ds_label  ds_no-margin--bottom" for="date-to">Updated before</label>
                                        <p class="ds_hint-text  ds_!_margin-bottom--1">For example, 21/01/2022</p>
                                        <div class="ds_input__wrapper">
                                            <input value="<#if end??>${end}</#if>" name="end" id="date-to" class="ds_input  ds_input--fixed-10" type="text">
                                        </div>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </div>

                <button class="ds_button  ds_button--primary  ds_button--small  ds_button--max  ds_no-margin  js-apply-filter">
                    Apply filter
                </button>

            </form>
        </div>
    </div>
</div>

<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#-- determine whether we have active parameters -->
<#assign hasActiveParameters = false/>
<#if parameters['q']?has_content || parameters['begin']?has_content || parameters['end']?has_content || parameters['topics']?has_content || parameters['publicationTypes']?has_content>
    <#assign hasActiveParameters = true/>
</#if>

<#assign termParam = parameters['q']![]/>
<#assign beginParam = parameters['begin']![]/>
<#assign endParam = parameters['end']![]/>

<#assign term = ''/>
<#assign begin = ''/>
<#assign end = ''/>

<#list termParam as nested>
    <#assign term = nested?j_string />
</#list>
<#list beginParam as nested>
    <#assign begin = nested?j_string />
</#list>
<#list endParam as nested>
    <#assign end = nested?j_string />
</#list>

<#function strSlug string>
  <#local string = string?lower_case />
  <#local string = string?replace("[ \t\n\x0B\f\r]+", "-", "r") />
  <#local string = string?replace("[^-a-z0-9]+", "-", "r") />
  <#local string = string?replace("(-)+", "-", "r") />

  <#return string />
</#function>

<#-- @ftlvariable name="publicationTypes" type="org.onehippo.forge.selection.hst.contentbean.ValueList" -->
<#-- @ftlvariable name="type" type="org.onehippo.forge.selection.hst.contentbean.ValueListItem" -->
<#-- @ftlvariable name="topic" type="scot.gov.www.beans.Topic" -->

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
            <form id="filters" method="GET" action="#">

                <fieldset id="filter-search" class="gov_filters__search">
                    <legend class="visually-hidden"><h2>Keyword search</h2></legend>
                    <label class="ds_label" for="filters-search-term">Search</label>

                    <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                        <input class="ds_input" type="search" name="q" id="filters-search-term" maxlength="160" value="${term}" />
                        <button class="ds_button  js-filter-search-submit" type="submit" title="Submit" id="filters-search-submit">
                            <span class="visually-hidden">Search</span>
                            <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#search"></use></svg>
                        </button>
                    </div>
                </fieldset>
                <h2 class="ds_search-filters__title  ds_h4  ds_!_padding-top--0">Filter by</h2>

                <div class="ds_accordion  ds_accordion--small  ds_!_margin-top--0" data-module="ds-accordion">
                <#if publicationTypes??>
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
                                                <label for="${item.key}" class="ds_checkbox__label">${(item.label)?replace("/","/<wbr>")?no_esc}</label>
                                            </div>
                                        </#list>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </#if>

                <#if topics??>
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
                                                <label for="${item.node.name}" class="ds_checkbox__label">${(item.title)?replace("/","/<wbr>")?no_esc}</label>
                                            </div>
                                        </#list>
                                    </div>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </#if>
                <#if dates??>
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
                
                </#if>
                </div>

                <button class="ds_button  ds_button--primary  ds_button--small  ds_button--max  ds_no-margin  js-apply-filter">
                    Apply filter
                </button>

            </form>
        </div>
    </div>
</div>
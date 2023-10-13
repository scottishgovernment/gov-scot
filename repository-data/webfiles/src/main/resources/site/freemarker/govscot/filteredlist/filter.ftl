<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#-- determine whether we have active parameters -->
<#assign hasActiveParameters = false/>
<#if parameters['term']?has_content || parameters['begin']?has_content || parameters['end']?has_content || parameters['topics']?has_content || parameters['publicationTypes']?has_content>
    <#assign hasActiveParameters = true/>
</#if>

<#assign termParam = parameters['term']![]/>
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

<div class="gov_filters" data-module="gov-filters">
    <input type="checkbox" class="fully-hidden  js-toggle-filters" id="show-filters" aria-controls="filters" />
    <label class="gov_filters__expand  ds_link" for="show-filters"><span class="js-show-filters-text">Show search filters</span> <span class="gov_filters__expand-indicator"></span></label>

    <form id="filters" action="#" method="GET" class="gov_filters__form  gov_filters--tab-title">
        <input type="hidden" id="imagePath" value="<@hst.webfile path='assets/images/icons/' />" />

        <div class="ds_search-filters">
            <h2 class="gov_filters__title  visually-hidden">Filters</h2>

            <#if term??>
                <fieldset id="filter-search" class="filters__fieldset  filter-search">
                    <legend class="visually-hidden">Keyword search</legend>
                    <label class="ds_label" for="filters-search-term">Search</label>

                    <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                        <input class="ds_input" type="text" id="filters-search-term" maxlength="160" value="${term}" />
                        <button class="ds_button  js-filter-search-submit" type="submit" title="Submit" id="filters-search-submit" >
                            <span class="visually-hidden">Search</span>
                            <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#search"></use></svg>
                        </button>
                    </div>
                </fieldset>
            </#if>

            <#if dates??>
                <fieldset id="filter-date-range" class="filters__fieldset">
                    <legend class="visually-hidden">Filter by date</legend>

                    <div class="ds_question">
                        <div data-module="ds-datepicker" class="ds_datepicker" id="fromDatePicker">
                            <label class="ds_label  ds_no-margin--bottom" for="date-from">Updated after</label>
                            <p class="ds_hint-text  ds_!_margin-bottom--1">For example, 21/01/2022</p>
                            <div class="ds_input__wrapper">
                                <input id="date-from" class="ds_input" type="text" value="${begin}" data-form="textinput-date-from" />
                            </div>
                        </div>
                    </div>

                    <div class="ds_question">
                        <div data-module="ds-datepicker" class="ds_datepicker" id="toDatePicker">
                            <label class="ds_label  ds_no-margin--bottom" for="date-to">Updated before</label>
                            <p class="ds_hint-text  ds_!_margin-bottom--1">For example, 21/01/2022</p>
                            <div class="ds_input__wrapper">
                                <input id="date-to" class="ds_input" type="text" value="${end}" data-form="textinput-date-to" />
                            </div>
                        </div>
                    </div>
                </fieldset>
            </#if>

            <#if publicationTypes?? || topics??>
                <div class="ds_accordion  ds_accordion--small  ds_no-margin" data-module="ds-accordion">
                    <#if publicationTypes??>
                        <div class="ds_accordion-item">
                            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-types" aria-labelledby="panel-types-heading" />
                            <div class="ds_accordion-item__header">
                                <h3 id="panel-types-heading" class="ds_accordion-item__title">
                                    Publication type
                                    <div class="js-publication-types-count  gov_filters__filter-info  gov_filters__filter-info--count"></div>
                                </h3>
                                <span class="ds_accordion-item__indicator"></span>
                                <label class="ds_accordion-item__label" for="panel-types"><span class="visually-hidden">Show this section</span></label>
                            </div>
                            <div class="ds_accordion-item__body">
                                <fieldset>
                                    <legend class="visually-hidden">Select which publication types you would like to see</legend>
                                    <#assign noItems = true />
                                    <#assign itemsTrigger = false />

                                    <div class="gov_filters__scrollable">
                                        <div class="ds_field-group  ds_field-group--checkboxes">
                                            <#list publicationTypes as item>
                                                <#assign isSelected = false/>
                                                <#if parameters['publicationTypes']??>
                                                    <#list parameters['publicationTypes'] as selectedItem>
                                                        <#if selectedItem == item.key>
                                                            <#assign isSelected = true/>
                                                            <#assign itemsTrigger = true />
                                                        </#if>
                                                    </#list>
                                                </#if>

                                                <div class="ds_radio  ds_radio--small">
                                                    <input
                                                        <#if isSelected == true>
                                                            <#if noItems == true>
                                                                checked=true
                                                            </#if>
                                                            data-checkedonload=true
                                                        </#if>
                                                        id="${strSlug(item.label)}" name="publicationTypes" value="${item.key}" class="ds_radio__input" type="radio" />
                                                    <label for="${strSlug(item.label)}" class="ds_radio__label">${item.label?replace('/', ' / ')}</label>
                                                </div>

                                                <#if itemsTrigger>
                                                    <#assign noItems = false />
                                                </#if>
                                            </#list>
                                        </div>
                                    </div>
                                </fieldset>
                            </div>
                        </div>
                    </#if>

                    <#if topics??>
                        <div class="ds_accordion-item">
                            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-topics" aria-labelledby="panel-topics-heading" />
                            <div class="ds_accordion-item__header">
                                <h3 id="panel-topics-heading" class="ds_accordion-item__title">
                                    Topics
                                    <div class="js-topics-count  gov_filters__filter-info  gov_filters__filter-info--count"></div>
                                </h3>
                                <span class="ds_accordion-item__indicator"></span>
                                <label class="ds_accordion-item__label" for="panel-topics"><span class="visually-hidden">Show this section</span></label>
                            </div>
                            <div class="ds_accordion-item__body">
                                <fieldset>
                                    <legend class="visually-hidden">Select which topics you would like to see</legend>

                                    <div class="gov_filters__scrollable">
                                        <div class="ds_field-group  ds_field-group--checkboxes">
                                            <#assign noItems = true />
                                            <#assign itemsTrigger = false />
                                            <#list topics as item>
                                                <#assign isSelected = false/>
                                                <#if parameters['topics']??>
                                                    <#list parameters['topics'] as selectedItem>
                                                        <#if selectedItem == item.title>
                                                            <#assign isSelected = true/>
                                                            <#assign itemsTrigger = true />
                                                        </#if>
                                                    </#list>
                                                </#if>

                                                <div class="ds_radio  ds_radio--small">
                                                    <input
                                                        <#if isSelected == true>
                                                            <#if noItems == true>
                                                                checked=true
                                                            </#if>
                                                            data-checkedonload=true
                                                        </#if>
                                                        id="${strSlug(item.title)}" name="topics" class="ds_radio__input" type="radio" value="${item.title}">
                                                    <label for="${strSlug(item.title)}" class="ds_radio__label">${item.title}</label>
                                                </div>

                                                <#if itemsTrigger>
                                                    <#assign noItems = false />
                                                </#if>
                                            </#list>
                                        </div>
                                    </div>
                                </fieldset>
                            </div>
                        </div>
                    </#if>
                </div>
            </#if>

            <div id="filter-actions">
                <#--  <button type="button" id="cancel-filters" name="cancel-filters" class="visible-xsmall  ds_button  ds_button--cancel  filter-actions__cancel  js-cancel-filters">Cancel</button>  -->
                <button type="submit" id="apply-filters" name="apply-filters" class="ds_button  ds_button--max  ds_no-margin--bottom  filter-actions__apply  js-apply-filters">Apply</button>
            </div>
        </div>
    </form>
</div>

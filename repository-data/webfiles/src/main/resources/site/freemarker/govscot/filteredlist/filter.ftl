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

<#-- @ftlvariable name="publicationTypes" type="org.onehippo.forge.selection.hst.contentbean.ValueList" -->
<#-- @ftlvariable name="type" type="org.onehippo.forge.selection.hst.contentbean.ValueListItem" -->
<#-- @ftlvariable name="topic" type="scot.gov.www.beans.Topic" -->

<form id="filters" action="#" method="GET" class="gov_filters  gov_filters--tab-title">
    <input type="hidden" id="imagePath" value="<@hst.webfile path='assets/images/icons/' />" />

    <div class="ds_search-filters">
        <h2 class="gov_filters__title">Filters</h2>

        <#if term??>
            <fieldset id="filter-search" class="filters__fieldset filter-search">
                <legend class="visually-hidden">Keyword search</legend>
                <label class="ds_label" for="filters-search-term">Keyword</label>

                <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
                    <input placeholder="Search ${searchType}" class="ds_input" type="text" id="filters-search-term" maxlength="160" value="${term}" />
                    <button class="ds_button  js-filter-search-submit" type="submit" title="Submit" id="filters-search-submit" >
                        <span class="visually-hidden">Search</span>
                        <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                    </button>
                </div>
            </fieldset>
        </#if>

        <#if dates??>
            <fieldset id="filter-date-range" class="filters__fieldset">
                <legend class="visually-hidden">Filter by date</legend>

                <div data-module="ds-datepicker" class="ds_datepicker" id="fromDatePicker">
                    <label class="ds_label" for="date-from">Date from</label>
                    <div class="ds_input__wrapper">
                        <input placeholder="dd/mm/yyyy" id="date-from" class="ds_input" type="text" value="${begin}" data-form="textinput-date-from" />
                    </div>
                </div>

                <div data-module="ds-datepicker" class="ds_datepicker" id="toDatePicker">
                    <label class="ds_label" for="date-to">Date to</label>
                    <div class="ds_input__wrapper">
                        <input placeholder="dd/mm/yyyy" id="date-to" class="ds_input" type="text" value="${end}" data-form="textinput-date-from" />
                    </div>
                </div>
            </fieldset>
        </#if>

        <#if publicationTypes?? || topics??>
            <div class="ds_accordion" data-module="ds-accordion">
                <#if publicationTypes??>
                    <div class="ds_accordion-item">
                        <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-types" aria-labelledby="panel-types-heading" />
                        <div class="ds_accordion-item__header">
                            <h3 id="panel-types-heading" class="ds_accordion-item__title">
                                Publication type
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-types"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <fieldset>
                                <legend class="visually-hidden">Select which publication types you would like to see</legend>
                                <#assign noItems = true />
                                <#assign itemsTrigger = false />

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
                                                id="${item.key}" name="publicationTypes" value="${item.key}" class="ds_radio__input" type="radio" />
                                            <label for="${item.key}" class="ds_radio__label">${item.label}</label>
                                        </div>

                                        <#if itemsTrigger>
                                            <#assign noItems = false />
                                        </#if>
                                    </#list>
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
                            </h3>
                            <span class="ds_accordion-item__indicator"></span>
                            <label class="ds_accordion-item__label" for="panel-topics"><span class="visually-hidden">Show this section</span></label>
                        </div>
                        <div class="ds_accordion-item__body">
                            <fieldset>
                                <legend class="visually-hidden">Select which topics you would like to see</legend>

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
                                                id="${item.canonicalPath}" name="topics" class="ds_radio__input" type="radio" value="${item.title}">
                                            <label for="${item.canonicalPath}" class="ds_radio__label">${item.title}</label>
                                        </div>

                                        <#if itemsTrigger>
                                            <#assign noItems = false />
                                        </#if>
                                    </#list>
                                </div>
                            </fieldset>
                        </div>
                    </div>
                </#if>
            </div>
        </#if>

        <div id="filter-actions">
            <button type="button" id="cancel-filters" name="cancel-filters" class="visible-xsmall  button button--medium button--cancel filter-actions__cancel js-cancel-filters">Cancel</button>
            <button type="submit" id="apply-filters" name="apply-filters" class="button button--medium button--primary filter-actions__apply js-apply-filters">Apply</button>
        </div>
    </div>
</form>



<!--
<form id="filters" action="#" method="GET">
    <input type="hidden" id="imagePath" value="<@hst.webfile path='assets/images/icons/' />" />

    <fieldset id="filters-fields">
        <legend class="hidden-xsmall gamma filters-container__title">Filters</legend>

        <div class="buttons filter-buttons visible-xsmall">
            <button type="button" class="button  button--primary  js-show-filters">Show filters</button>
        </div>

        <div class="mobile-layer mobile-layer--show-tablet mobile-layer--highest mobile-layer--dark filters-layer">
            <div class="mobile-layer__overlay"></div>
            <div class="mobile-layer__content">
                <div class="filters-container">

                    <button type="button" class="button filters-container__close js-cancel-filters visible-xsmall">
                        <svg class="svg-icon  mg-icon  mg-icon--full  mg-icon--inline">
                            <use xlink:href="${iconspath}#close"></use>
                        </svg>
                        <span class="hidden">Close</span>
                    </button>

                    <div class="filters">

                        <a href="?" class="<#if hasActiveParameters == false>hidden  </#if>js-clear-filters  filters__button--clear  button button--cancel  button--xsmall">Clear</a>

                        <#if term??>
                        <fieldset id="filter-search" class="filters__fieldset filter-search">
                            <legend class="filters__legend">Keyword search</legend>
                            <label class="filters__label" for="filters-search-term">Keyword</label>
                            <div class="filters-input__wrapper">
                                <input placeholder="Search ${searchType}" type="text" title="Filter by keyword" name="term" id="filters-search-term" maxlength="160" class="filter-search__input filters__input--search-term" value="${term}" />
                                <button type="submit" title="Submit" id="filters-search-submit" class="filter-search__button filter-search__button--submit js-filter-search-submit button button--clear"></button>
                            </div>
                        </fieldset>
                        </#if>

                        <#if dates??>
                            <fieldset id="filter-date-range" class="filters__fieldset">
                                <legend class="filters__legend">Filter by date</legend>
                                <div data-module="ds-datepicker" class="ds_datepicker" id="fromDatePicker">
                                    <label class="filters__label" for="date-from">Date from</label>
                                    <div class="ds_input__wrapper">
                                        <input placeholder="dd/mm/yyyy" id="date-from" style="margin-bottom: 21px;" type="text" value="${begin}" data-form="textinput-date-from" />
                                    </div>
                                </div>

                                <div data-module="ds-datepicker" class="ds_datepicker" id="toDatePicker">
                                    <label class="filters__label" for="date-to">Date to</label>
                                    <div class="ds_input__wrapper">
                                        <input placeholder="dd/mm/yyyy" id="date-to" style="margin-bottom: 21px;" type="text" value="${end}" data-form="textinput-date-from" />
                                    </div>
                                </div>
                            </fieldset>
                        </#if>




                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</form>
-->

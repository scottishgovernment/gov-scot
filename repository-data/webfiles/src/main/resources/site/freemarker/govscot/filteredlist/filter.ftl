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

<form id="filters" action="#" method="GET">

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
                            <input placeholder="Search ${searchType}" type="text" title="Filter by keyword" name="term" id="filters-search-term" maxlength="160" class="filter-search__input filters__input--search-term" value="${term}" />
                            <button type="submit" title="Submit" id="filters-search-submit" class="filter-search__button filter-search__button--submit js-filter-search-submit button button--clear"></button>
                        </fieldset>
                        </#if>

                        <#if dates??>
                            <fieldset id="filter-date-range">
                                <legend class="filters__legend">Filter by date</legend>

                                <div class="date-entry">
                                    <label class="filters__label" for="date-from">Date from</label>
                                    <div class="date-entry__input-group input-group">
                                        <input name="begin" type="text" id="date-from" placeholder="dd/mm/yyyy" pattern="\d\d\/\d\d\/\d\d\d\d" class="date-entry__input datepicker" value="${begin}">
                                        <button id="date-start-trigger" type="button" class="date-entry__trigger  button button--primary  button--xsmall  js-show-calendar  hidden  hidden--hard">
                                            <svg class="svg-icon  mg-icon  mg-icon--full  mg-icon--inline">
                                                <use xlink:href="${iconspath}#calendar"></use>
                                            </svg>
                                            Choose start date
                                        </button>
                                    </div>
                                    <div class="date-entry__calendar">
                                        <button class="date-entry__close filters-container__close hidden-xsmall js-close-calendar" type="button">
                                            <svg class="svg-icon  mg-icon  mg-icon--full  mg-icon--inline">
                                                <use xlink:href="${iconspath}#close"></use>
                                            </svg>
                                            Close
                                        </button>
                                    </div>
                                </div>

                                <div class="date-entry">
                                    <label class="filters__label" for="date-to">Date to</label>
                                    <div class="date-entry__input-group input-group">
                                        <input name="end" type="text" id="date-to" placeholder="dd/mm/yyyy" pattern="\d\d\/\d\d\/\d\d\d\d" class="date-entry__input datepicker" value="${end}">
                                        <button id="date-end-trigger" type="button" class="date-entry__trigger  button button--primary  button--xsmall  js-show-calendar  hidden  hidden--hard">
                                            <svg class="svg-icon  mg-icon  mg-icon--full  mg-icon--inline">
                                                <use xlink:href="${iconspath}#calendar"></use>
                                            </svg>
                                            Choose end date
                                        </button>
                                    </div>
                                    <div class="date-entry__calendar">
                                        <button class="date-entry__close filters-container__close hidden-xsmall js-close-calendar" type="button">
                                            <svg class="svg-icon  mg-icon  mg-icon--full  mg-icon--inline">
                                                <use xlink:href="${iconspath}#close"></use>
                                            </svg>
                                            Close
                                        </button>
                                    </div>
                                </div>
                            </fieldset>
                        </#if>

                        <div class="expandable expandable--single filters-expandable">

                            <#if topics??>
                                <div class="expandable-item  expandable-item--open  expandable-item--init-open">
                                    <button type="button" class="expandable-item__header js-toggle-expand" tabindex="0">
                                        <h2 class="delta  expandable-item__title">Topics</h2>
                                        <span class="expandable-item__icon">
                                            <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more">
                                                <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                                            </svg>
                                            <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less">
                                                <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                                            </svg>
                                        </span> 
                                    </button>

                                    <div class="expandable-item__body  expandable-item__body--with-padding  scrollable scrollable--shadow">
                                        <div class="scrollable__content scrollable__content--40 checkbox-group">
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

                                                <input
                                                    <#if isSelected == true>
                                                        <#if noItems == true>
                                                        checked=true
                                                        </#if>
                                                        data-checkedonload=true
                                                    </#if>
                                                    id="${item.canonicalPath}" name="topics" class="fancy-checkbox checkbox-group__input" type="radio" value="${item.title}">
                                                <label for="${item.canonicalPath}" class="checkbox-group__label fancy-radio fancy-radio--min">${item.title}</label>

                                                <#if itemsTrigger>
                                                    <#assign noItems = false />
                                                </#if>
                                            </#list>
                                        </div>
                                    </div>
                                </div>
                            </#if>

                            <#if publicationTypes??>
                                <div class="expandable-item  expandable-item--open  expandable-item--init-open">
                                    <button type="button" class="expandable-item__header js-toggle-expand" tabindex="0">
                                        <h2 class="delta  expandable-item__title">Publication Types</h2>
                                        <span class="expandable-item__icon">
                                            <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more">
                                                <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                                            </svg>
                                            <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less">
                                                <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                                            </svg>
                                        </span>
                                    </button>

                                    <div class="expandable-item__body  expandable-item__body--with-padding  scrollable scrollable--shadow">
                                        <div class="scrollable__content scrollable__content--40 checkbox-group">
                                            <#assign noItems = true />
                                            <#assign itemsTrigger = false />
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

                                                <input
                                                    <#if isSelected == true>
                                                        <#if noItems == true>
                                                                checked=true
                                                        </#if>
                                                                data-checkedonload=true
                                                    </#if>
                                                                id="${item.key}" name="publicationTypes" class="fancy-checkbox checkbox-group__input" type="radio" value="${item.key}">
                                                <label for="${item.key}" class="checkbox-group__label fancy-radio fancy-radio--min">${item.label}</label>

                                                <#if itemsTrigger>
                                                    <#assign noItems = false />
                                                </#if>
                                            </#list>
                                        </div>
                                    </div>
                                </div>
                            </#if>
                        </div>

                        <div id="filter-actions">
                            <button type="button" id="cancel-filters" name="cancel-filters" class="visible-xsmall  button button--medium button--cancel filter-actions__cancel js-cancel-filters">Cancel</button>
                            <button type="submit" id="apply-filters" name="apply-filters" class="button button--medium button--primary filter-actions__apply js-apply-filters">Apply</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </fieldset>
</form>

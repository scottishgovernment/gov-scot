<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#-- determine whether we have active parameters -->
<#assign hasActiveParameters = false/>
<#if parameters['term']?has_content || parameters['begin']?has_content || parameters['end']?has_content || parameters['topics']?has_content || parameters['types']?has_content>
    <#assign hasActiveParameters = true/>
</#if>

<#assign termParam = parameters['term']![]/>
<#assign beginParam = parameters['begin']![]/>
<#assign endParam = parameters['end']![]/>

<#assign term = ''/>
<#assign begin = ''/>
<#assign end = ''/>

<#list termParam as nested>
    <#assign term = nested />
</#list>
<#list beginParam as nested>
    <#assign begin = nested />
</#list>
<#list endParam as nested>
    <#assign end = nested />
</#list>

<#-- @ftlvariable name="publicationTypes" type="org.onehippo.forge.selection.hst.contentbean.ValueList" -->
<#-- @ftlvariable name="type" type="org.onehippo.forge.selection.hst.contentbean.ValueListItem" -->
<#-- @ftlvariable name="topic" type="scot.gov.www.beans.Topic" -->

<form id="filters" action="#" method="GET">

    <div class="buttons filter-buttons visible-xsmall">
        <button class="button  button--primary  js-show-filters">Show filters</button>
    </div>

    <div class="mobile-layer mobile-layer--show-tablet mobile-layer--highest mobile-layer--dark filters-layer">
        <div class="mobile-layer__overlay"></div>
        <div class="mobile-layer__content">
            <div class="filters-container">

                <h2 class="hidden-xsmall gamma filters-container__title">Filters</h2>

                <button type="button" class="button filters-container__close js-cancel-filters visible-xsmall">
                    <svg class="svg-icon  mg-icon  mg-icon--full  mg-icon--inline">
                        <use xlink:href="${iconspath}#close"></use>
                    </svg>
                    <span class="hidden">Close</span>
                </button>

                <div class="filters">

                    <a href="?" class="<#if hasActiveParameters == false>hidden  </#if>js-clear-filters  filters__button--clear  button button--cancel  button--xsmall">Clear</a>

                    <fieldset id="filter-search" class="filters__fieldset filter-search">
                        <legend class="filters__legend">Keyword search</legend>
                        <input type="text" title="Filter by keyword" name="term" id="filters-search-term" maxlength="160" class="filter-search__input filters__input--search-term" value="${term}" />
                        <button type="submit" title="Submit" id="filters-search-submit" class="filter-search__button filter-search__button--submit js-filter-search-submit button button--clear"></button>
                    </fieldset>

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

                    <div class="expandable expandable--single filters-expandable">

                        <#if topics??>
                            <div class="expandable-item  expandable-item--open  expandable-item--init-open">
                                <button type="button" class="expandable-item__header js-toggle-expand" tabindex="0">
                                    <h4 class="expandable-item__title">Topics</h4>
                                    <span class="expandable-item__icon"></span>
                                </button>

                                <div class="expandable-item__body scrollable scrollable--shadow">
                                    <div class="scrollable__content scrollable__content--40 checkbox-group">
                                        <#assign noTopics = true />
                                        <#assign topicsTrigger = false />
                                        <#list topics as topic>
                                            <#assign isSelected = false/>
                                            <#if parameters['topics']??>
                                                <#list parameters['topics'] as selectedTopic>
                                                    <#if selectedTopic == topic.title>
                                                        <#assign isSelected = true/>
                                                        <#assign topicsTrigger = true />
                                                    </#if>
                                                </#list>
                                            </#if>

                                            <input
                                                <#if isSelected == true>
                                                    <#if noTopics == true>
                                                    checked=true
                                                    </#if>
                                                    data-checkedonload=true
                                                </#if>
                                                id="${topic.canonicalPath}" name="topics" class="fancy-checkbox checkbox-group__input" type="radio" value="${topic.title}">
                                            <label for="${topic.canonicalPath}" class="checkbox-group__label fancy-radio fancy-radio--min">${topic.title}</label>

                                            <#if topicsTrigger>
                                                <#assign noTopics = false />
                                            </#if>
                                        </#list>
                                    </div>
                                </div>
                            </div>
                        </#if>

                        <#if publicationTypes??>
                            <button type="button" class="expandable-item__header js-toggle-expand" tabindex="0">
                                <h4 class="expandable-item__title">Type</h4>
                                <span class="expandable-item__icon"></span>
                            </button>

                            <div class="expandable-item__body scrollable scrollable--shadow">
                                <div class="checkbox-group">
                                    <#list publicationTypes.categories as publicationType>
                                        <h5 class="checkbox-group__title">${publicationType.getInfo(locale).name}</h5>
                                        <#list publicationType.children as category>
                                            <input id="${category.key}" name="publicationTypes[]" class="checkbox-group__input" type="checkbox" value="${category.key}"/>
                                            <label for="${category.key}" class="checkbox-group__label fancy-checkbox">${category.getInfo(locale).name}</label>
                                        </#list>

                                    </#list>
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
</form>

<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#setting url_escaping_charset='utf-8'>
<#macro highlightSearchTerm text>
    <#if response.resultPacket.queryHighlightRegex??>
        <#assign pattern = "(?i)(" + response.resultPacket.queryHighlightRegex?replace("(?i)","") + ")" />
        ${text?replace(pattern, "<mark>$1</mark>", 'ri')?no_esc!}
    <#else>
        ${text}
    </#if>
</#macro>

<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#-- @ftlvariable name="parameters" type="java.util.Map" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.News" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Publication" -->
<#-- @ftlvariable name="item" type="scot.gov.www.beans.Policy" -->
<#-- @ftlvariable name="searchTermSingular" type="java.lang.String" -->
<#-- @ftlvariable name="searchTermPlural" type="java.lang.String" -->
<#-- @ftlvariable name="showSort" type="java.lang.Boolean" -->

<#-- Set number format to exclude comma separators -->
<#setting number_format="0.##">


<#if response??>
    <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching &gt; 0>
        <h2 aria-live="polite" class="ds_search-results__title">
            <#if response.resultPacket.resultsSummary.fullyMatching <= response.resultPacket.resultsSummary.numRanks ||
            response.resultPacket.resultsSummary.currStart <= response.resultPacket.resultsSummary.numRanks >

            ${response.resultPacket.resultsSummary.totalMatching} <#if response.resultPacket.resultsSummary.totalMatching gt 1>results<#else>result</#if><#if question.originalQuery?has_content> for <span class="ds_search-results__title-query">${question.originalQuery}</span></#if>
            <#else>
                Showing ${response.resultPacket.resultsSummary.currStart} to ${response.resultPacket.resultsSummary.currEnd}
                of ${response.resultPacket.resultsSummary.totalMatching} <#if response.resultPacket.resultsSummary.totalMatching gt 1>results<#else>result</#if><#if question.originalQuery?has_content> for <span class="ds_search-results__title-query">${question.originalQuery}</span></#if>
            </#if>
        </h2>
    </#if>

    <div class="ds_skip-links  ds_skip-links--static">
        <ul class="ds_skip-links__list">
            <li class="ds_skip-links__item"><a class="ds_skip-links__link" href="#search-results">Skip to results</a></li>
        </ul>
    </div>

    <div class="ds_search-controls">
        <#assign filtersCount = filterButtons.types?size +
            filterButtons.topics?size + filterButtons.dates?size />

        <#if filtersCount gt 0>

        <div class="ds_facets">
            <p class="visually-hidden">
                <#if filtersCount == 1>
                    There is 1 search filter applied.
                <#else>
                    There are ${filtersCount} search filters applied.
                </#if>
            </p>

            <dl class="ds_facets__list">
                <#if filterButtons.types?? && filterButtons.types?size gt 0>
                    <div class="ds_facet-group">
                        <dt class="ds_facet__group-title">
                            Content type:
                        </dt>
                        <#list filterButtons.types as item>
                            <dd class="ds_facet-wrapper">
                                <span class="ds_facet">
                                    ${item.label}

                                    <a href="?${item.url}" role="button" aria-label="Remove '${item.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="${item.id}">
                                        <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                    </a>
                                </span>
                            </dd>
                        </#list>
                    </div>
                </#if>

                <#if filterButtons.topics?? && filterButtons.topics?size gt 0>
                    <div class="ds_facet-group">
                        <dt class="ds_facet__group-title">
                            Topic:
                        </dt>
                        <#list filterButtons.topics as item>
                            <dd class="ds_facet-wrapper">
                                <span class="ds_facet">
                                    ${item.label}

                                    <a href="?${item.url}" role="button" aria-label="Remove '${item.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="${item.id}">
                                        <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                    </a>
                                </span>
                            </dd>
                        </#list>
                    </div>
                </#if>

                <#if filterButtons.dates?? && filterButtons.dates?size gt 0>
                    <#if filterButtons.dates.begin?? && filterButtons.dates.end??>
                        <#assign dateLabel = "Updated between"/>
                    <#elseif filterButtons.dates.begin??>
                        <#assign dateLabel = "Updated after"/>
                    <#elseif filterButtons.dates.end??>
                        <#assign dateLabel = "Updated before"/>
                    </#if>

                    <div class="ds_facet-group">
                        <dt class="ds_facet__group-title">
                            ${dateLabel}:
                        </dt>

                        <#if filterButtons.dates.begin?? && filterButtons.dates.end??>
                            <dd class="ds_facet-wrapper">
                                <span class="ds_facet">
                                    ${filterButtons.dates.begin.label}

                                    <a href="?${filterButtons.dates.begin.url}" aria-label="Remove 'updated after ${filterButtons.dates.begin.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="date-from">
                                        <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                    </a>
                                </span> and
                            </dd>

                            <dd class="ds_facet-wrapper">
                                <span class="ds_facet">
                                    ${filterButtons.dates.end.label}

                                    <a href="?${filterButtons.dates.end.url}" aria-label="Remove 'updated before ${filterButtons.dates.end.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="date-to">
                                        <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                    </a>
                                </span>
                            </dd>
                        <#elseif filterButtons.dates.begin??>
                            <dd class="ds_facet-wrapper">
                                <span class="ds_facet">
                                    ${filterButtons.dates.begin.label}

                                    <a href="?${filterButtons.dates.begin.url}" role="button" aria-label="Remove 'updated after ${filterButtons.dates.begin.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="date-from">
                                        <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                    </a>
                                </span>
                            </dd>
                        <#elseif filterButtons.dates.end??>
                            <dd class="ds_facet-wrapper">
                                <span class="ds_facet">
                                    ${filterButtons.dates.end.label}

                                    <a href="?${filterButtons.dates.end.url}" role="button" aria-label="Remove 'updated before ${filterButtons.dates.end.label}' filter" class="ds_facet__button  js-remove-facet" data-slug="date-to">
                                        <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
                                    </a>
                                </span>
                            </dd>
                        </#if>
                    </div>
                </#if>
            </dl>

            <#if (RequestParameters.q)?has_content>
                <#assign clearAll = "?q=" + RequestParameters.q/>
            <#else>
                <#assign clearAll = "."/>
            </#if>
            <a href="${clearAll}" role="button" class="ds_facets__clear-button  ds_button  ds_button--secondary  js-clear-filters">
                Clear all filters
                <svg class="ds_facet__button-icon" aria-hidden="true" role="img" focusable="false"><use href="${iconspath}#cancel"></use></svg>
            </a>
        </div>

        </#if>

        <hr class="ds_search-results__divider">

        <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
            response.resultPacket.resultsSummary.totalMatching &gt; 0>
            <#if showSort??>
                <div class="ds_sort-options">
                    <label class="ds_label" for="sort-by">Sort by</label>
                    <span class="ds_select-wrapper">
                        <select form="filters" name="sort" class="ds_select  js-sort-by" id="sort-by">
                            <option <#if hstRequest.request.getParameter('sort')?? && hstRequest.request.getParameter('sort') == "date">selected</#if> value="date">Updated (newest)</option>
                            <option <#if hstRequest.request.getParameter('sort')?? && hstRequest.request.getParameter('sort') == "adate">selected</#if> value="adate">Updated (oldest)</option>
                        </select>
                        <span class="ds_select-arrow" aria-hidden="true"></span>
                    </span>

                    <button form="filters" class="ds_button  ds_button--secondary  ds_button--small  js-apply-sort" type="submit" data-button="button-apply-sort">Apply sort</button>
                </div>
            </#if>
        </#if>
    </div>

    <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching == 0 &&
        !(response.curator.simpleHtmlExhibits)?has_content &&
        !(response.curator.advertExhibits)?has_content>
        <h2 class="visually-hidden">Search</h2>
        <div id="no-search-results" class="ds_no-search-results">
                <@hst.html hippohtml=document.noResultsMessage/>
        </div>
    </#if>

    <#if (response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching == -1 &&
        !(response.curator.simpleHtmlExhibits)?has_content &&
        !(response.curator.advertExhibits)?has_content>
        <h2 class="visually-hidden">Search</h2>
        <div id="no-search-results" class="ds_no-search-results">
            <@hst.html hippohtml=document.blankSearchQueryMessage/>
        </div>
    </#if>

<#if pagination??>
    <#if ((response.resultPacket.resultsSummary.totalMatching)!?has_content &&
        response.resultPacket.resultsSummary.totalMatching &gt; 0 ) >
    <ol start="${response.resultPacket.resultsSummary.currStart?c}" id="search-results" class="ds_search-results__list" data-total="${response.resultPacket.resultsSummary.totalMatching?c}">
    <#-- Using same result template as search -->
    <#list pageable.items as item>
       <#include "../search/bloomreach-search-result.ftl">
    </#list> 
    </ol>
    </#if>

    <#if pagination.pages?has_content>
    <nav id="pagination" class="ds_pagination" aria-label="Search result pages">
        <ul class="ds_pagination__list">
            <#if pagination.previous??>
                <li class="ds_pagination__item">
                    <a aria-label="Previous page" class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${pagination.previous.url}">
                        <svg class="ds_icon" aria-hidden="true" role="img">
                            <use href="${iconspath}#chevron_left"></use>
                        </svg>
                        <span class="ds_pagination__link-label">${pagination.previous.label}</span>
                    </a>
                </li>
            </#if>

            <#if pagination.first??>
                <li class="ds_pagination__item">
                    <a aria-label="Page ${pagination.first.label}" class="ds_pagination__link" href="${pagination.first.url}">
                        <span class="ds_pagination__link-label">${pagination.first.label}</span>
                    </a>
                </li>
                <li class="ds_pagination__item" aria-hidden="true">
                    <span class="ds_pagination__link  ds_pagination__link--ellipsis">&hellip;</span>
                </li>
            </#if>

            <#list pagination.pages as page>
                <li class="ds_pagination__item">
                    <#if page.selected>
                        <a aria-label="Page ${page.label}" aria-current="page" class="ds_pagination__link  ds_current" href="${page.url}">
                            <span class="ds_pagination__link-label">${page.label}</span>
                        </a>
                    <#else>
                        <a aria-label="Page ${page.label}" class="ds_pagination__link" href="${page.url}">
                            <span class="ds_pagination__link-label">${page.label}</span>
                        </a>
                    </#if>
                </li>
            </#list>

            <#if pagination.last??>
                <li class="ds_pagination__item" aria-hidden="true">
                    <span class="ds_pagination__link  ds_pagination__link--ellipsis">&hellip;</span>
                </li>
                <li class="ds_pagination__item">
                    <a aria-label="Last page, page ${pagination.last.label}" class="ds_pagination__link" href="${pagination.last.url}">${pagination.last.label}</a>
                </li>
            </#if>

            <#if pagination.next??>
                <li class="ds_pagination__item">
                    <a aria-label="Next page" class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${pagination.next.url}">
                        <span class="ds_pagination__link-label">${pagination.next.label}</span>
                        <svg class="ds_icon" aria-hidden="true" role="img">
                            <use href="${iconspath}#chevron_right"></use>
                        </svg>
                    </a>
                </li>
            </#if>
        </ul>
    </nav>
    </#if>

</#if>

</#if>
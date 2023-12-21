<#ftl output_format="HTML">
<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#include "./imports.ftl">
<#if pageable??>
    <@hst.setBundle basename="essentials.pagination"/>
    <@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

    <nav id="pagination" class="ds_pagination" aria-label="Search pages">
        <ul class="ds_pagination__list">
            <#if pageable.totalPages gt 1>
                <#list pageable.pageNumbersArray as pageNr>
                    <@hst.renderURL var="pageUrl">
                        <@hst.param name="page" value="${pageNr}"/>
                    </@hst.renderURL>
                    <#if (pageNr_index==0 && pageable.previous)>
                        <@hst.renderURL var="pageUrlPrevious">
                            <@hst.param name="page" value="${pageable.previousPage}"/>
                        </@hst.renderURL>
                        <li class="ds_pagination__item">
                            <a aria-label="Previous page" class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${pageUrlPrevious?no_esc}">
                                <svg class="ds_icon" aria-hidden="true" role="img">
                                    <use href="${iconspath}#chevron_left"></use>
                                </svg>
                                <span class="ds_pagination__link-label"><@fmt.message key="page.previous" var="prev"/>${prev}</span>
                            </a>
                        </li>
                    </#if>
                    <#if pageable.currentPage == pageNr>
                        <li class="ds_pagination__item">
                            <a aria-label="Page ${pageNr}" aria-current="page" class="ds_pagination__link  ds_current" href="${pageUrl?no_esc}">
                                <span class="ds_pagination__link-label">${pageNr}</span>
                            </a>
                        </li>
                    <#else>
                        <li class="ds_pagination__item">
                            <a aria-label="Page ${pageNr}" class="ds_pagination__link" href="${pageUrl?no_esc}">
                                <span class="ds_pagination__link-label">${pageNr}</span>
                            </a>
                        </li>
                    </#if>

                    <#if !pageNr_has_next && pageable.next>
                        <@hst.renderURL var="pageUrlNext">
                            <@hst.param name="page" value="${pageable.nextPage}"/>
                        </@hst.renderURL>
                        <li class="ds_pagination__item ">
                            <a aria-label="Next page" class="ds_pagination__link  ds_pagination__link--text  ds_pagination__link--icon" href="${pageUrlNext?no_esc}">
                                <span class="ds_pagination__link-label"><@fmt.message key="page.next" var="next"/>${next}</span>
                                <svg class="ds_icon" aria-hidden="true" role="img">
                                    <use href="${iconspath}#chevron_right"></use>
                                </svg>
                            </a>
                        </li>
                    </#if>
                </#list>
            </#if>
        </ul>
    </nav>
</#if>

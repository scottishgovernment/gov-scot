<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<#include "../include/imports.ftl">
<#if pageable??>
    <@hst.setBundle basename="essentials.pagination"/>

    <div class="search-results__pagination search-results__pagination--full pagination">
        <ul class="pagination__list"><!--
            <#if pageable.totalPages gt 1>
                <#list pageable.pageNumbersArray as pageNr>
                    <@hst.renderURL var="pageUrl">
                        <@hst.param name="page" value="${pageNr}"/>
                    </@hst.renderURL>
                    <#if (pageNr_index==0 && pageable.previous)>
                        <@hst.renderURL var="pageUrlPrevious">
                            <@hst.param name="page" value="${pageable.previousPage}"/>
                        </@hst.renderURL>
                        --><li class="pagination__item ">
                            <a data-gtm="${gtmslug}-p-prev" class="pagination__page" href="${pageUrlPrevious}"><@fmt.message key="page.previous" var="prev"/>${prev?html}</a>
                        </li><!--
                    </#if>
                    <#if pageable.currentPage == pageNr>
                        --><li class="pagination__item active">
                            <span class="pagination__page  pagination__page--active  pagination__page--no-link" href="#">${pageNr}</span>
                        </li><!--
                    <#else >
                        --><li class="pagination__item ">
                            <a data-gtm="${gtmslug}-p-${pageNr}" class="pagination__page" href="${pageUrl}">${pageNr}</a>
                        </li><!--
                    </#if>

                    <#if !pageNr_has_next && pageable.next>
                        <@hst.renderURL var="pageUrlNext">
                            <@hst.param name="page" value="${pageable.nextPage}"/>
                        </@hst.renderURL>
                        --><li class="pagination__item ">
                            <a data-gtm="${gtmslug}-p-next" class="pagination__page" href="${pageUrlNext}"><@fmt.message key="page.next" var="next"/>${next?html}</a>
                        </li><!--
                    </#if>
                </#list>
            </#if>
        --></ul>
    </div>
</#if>

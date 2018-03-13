<#include "../include/imports.ftl">

<nav class="page-group">
    <!-- toggler -->
    <button type="button" class="page-group__toggle visible-xsmall js-show-page-group-list">Choose section &hellip;</button>
    <!-- nav links -->
    <ul class="page-group__list">
        <li class="page-group__item page-group__item--level-0">
          <span class="page-group__link page-group__link--level-0
          page-group__link--selected page-group__link--level-0--selected">
              <span class="page-group__text">Test menu item</span>
          </span>
        </li>
        <#list policypages as policypage>
            <li class="page-group__item page-group__item--level-1">
                <@hst.link var="link" hippobean=policypage/>
                <a class="page-group__link page-group__link--level-1" href="${link}">
                    <span class="page-group__text">${policypage.title}</span>
                </a>
            </li>
        </#list>
    </ul>
</nav>


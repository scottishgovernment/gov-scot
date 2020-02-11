<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="ds_site-header__search  ds_site-search  ds_site-search--collapsible">
    <form role="search" class="ds_search-box__form" method="GET" action="<@hst.link path='/search/'/>">
        <label class="search-box__label hidden" for="search-box">Search</label>
        <div class="ds_site-search__input-group">
            <input name="q" required="" id="search-box" class="search-box__input" type="text" placeholder="Search site">
            <input name="cat" value="search" hidden>

            <button type="submit" title="search" class="search-box__button button button--primary">
                <svg class="ds_icon  ds_site-search__icon" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                <span class="hidden">Search gov.scot</span>
            </button>
        </div>
    </form>
</div>

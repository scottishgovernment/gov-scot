<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="ds_site-header__search  ds_site-search  ds_site-search--collapsible">

<form role="search" class="ds_site-search__form" method="GET" action="<@hst.link path='/search/'/>">
    <label class="ds_site-search__label hidden" for="site-search">Search</label>

    <div class="ds_site-search__input-group">
        <input name="q" required="" id="site-search" class="ds_site-search__input" type="text" placeholder="Search" autocomplete="off" />

            <button type="submit" title="search" class="ds_site-search__button  button  button--primary">
                <svg class="ds_icon  ds_site-search__icon" role="img"><use xlink:href="${iconspath}#search"></use></svg>
                <span class="hidden">Search gov.scot</span>
            </button>
        </div>
    </form>
</div>

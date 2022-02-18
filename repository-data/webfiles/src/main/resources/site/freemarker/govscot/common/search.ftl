<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#assign term = "" />
<#if parameters?? && parameters['q']??>
    <#assign term = parameters['q'][0]?j_string />
</#if>
<!--noindex-->
<div class="ds_site-search">
    <form role="search" class="ds_site-search__form" method="GET" action="<@hst.link path='/search/'/>">
        <label class="ds_label  visually-hidden" for="site-search">Search</label>

        <div class="ds_input__wrapper  ds_input__wrapper--has-icon">
            <input value="${term}" name="q" required="" id="site-search" class="ds_input  ds_site-search__input" type="text" placeholder="Search" autocomplete="off" />
            <input name="cat" value="sitesearch" hidden>

            <button type="submit" class="ds_button  js-site-search-button">
                <span class="visually-hidden">Search gov.scot</span>
                <svg class="ds_icon" aria-hidden="true" role="img"><use xlink:href="${iconspath}#search"></use></svg>
            </button>
        </div>
    </form>
</div>
<!--endnoindex-->

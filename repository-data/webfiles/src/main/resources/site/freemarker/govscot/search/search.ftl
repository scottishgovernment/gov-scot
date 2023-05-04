<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#assign term = ''/>
<#if hstRequestContext.servletRequest.getParameter("q")??>
    <#assign term = hstRequestContext.servletRequest.getParameter("q") />
</#if>

<#if !searchpagepath??>
    <#assign searchpagepath = "/search" />
<#else>
    <#assign searchpagepath = "${searchpagepath}" />
</#if>

<div class="ds_site-search  <#if ds_autocomplete??>ds_autocomplete</#if>" <#if ds_autocomplete??>data-module="ds-autocomplete"</#if>>
    <form action="<@hst.link path=searchpagepath/>" role="search" class="ds_site-search__form" method="GET">
        <label class="ds_label  visually-hidden" for="site-search">Search</label>
        <div id="autocomplete-status" class="visually-hidden"></div>
        <div class="ds_input__wrapper  ds_input__wrapper--has-icon">

        <#if ds_autocomplete??>
            <input
                    aria-autocomplete="list"
                    aria-expanded="false"
                    aria-owns="autocomplete-suggestions-large"
                    autocomplete="off"
                    class="ds_input  ds_site-search__input  js-autocomplete-input"
                    haspopup="true"
                    id="site-search"
                    name="q"
                    placeholder="Search"
                    required=""
                    type="search"
                    value="${term}"
            />
        <#else>
            <input value="${term}" name="q" required="" id="site-search" class="ds_input  ds_site-search__input" type="text" placeholder="Search" autocomplete="off" />
        </#if>

            <button type="submit" class="ds_button  ds_button--icon-only  js-site-search-button">
                <span class="visually-hidden">Search</span>
                <svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#search"></use></svg>
            </button>

        <#if ds_autocomplete??>
            <div id="autocomplete-suggestions-large" class="ds_autocomplete__suggestions">
                <ol class="ds_autocomplete__suggestions-list" role="listbox" aria-labelledby="site-search-label"></ol>
            </div>
        </#if>

        </div>
    </form>
</div>

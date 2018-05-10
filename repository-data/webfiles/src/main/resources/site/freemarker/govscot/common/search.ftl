<#include "../../include/imports.ftl">

<div class="search-box">
    <form class="search-box__form" method="GET" action="search.html">
        <label class="search-box__label hidden" for="searchbox-inputtext">Search</label>
        <input type="text" class="search-box__input" id="searchbox-inputtext" name="query"
               placeholder="Search site" />
        <button type="submit" title="search" class="search-box__button button button--primary">
            <span class="icon icon--search-white"></span>
            <span class="hidden">Search</span>
        </button>
    </form>
</div>
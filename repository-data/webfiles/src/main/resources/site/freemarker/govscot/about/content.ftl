<#include "../../include/imports.ftl">

<header class="ds_page-header">
    <h1 class="ds_page-header__title">${document.title}</h1>

    <#include "../common/metadata.ftl"/>
</header>

<div class="body-content  ds_leader-first-paragraph">
    <@hst.html hippohtml=document.content/>
</div>

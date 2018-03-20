<#include "../include/imports.ftl">

<h1 class="article-header__title">${document.title?html}</h1>


${document.content.content}

<@hst.html hippohtml=document.content />

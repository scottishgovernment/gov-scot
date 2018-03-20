<#include "../include/imports.ftl">

<h1 class="article-header__title">${document.title?html}</h1>

${document.content.content}

<#list document.images as image>
    <@hst.link var="link" hippobean=image/>
<img src="${link}">blah</img><#sep>, </sep>
</#list>
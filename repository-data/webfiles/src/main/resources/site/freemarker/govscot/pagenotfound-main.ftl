<#include "../include/imports.ftl">

<#if document??>
    <div class="grid"><!--
     --><div class="grid__item medium--nine-twelfths large--seven-twelfths">
          <h1 class="article-header">${document.title}</h1>
          <@hst.html hippohtml=document.content/>
        </div><!--
    --></div>
</#if>

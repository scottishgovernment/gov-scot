<#include "../include/imports.ftl">

<@hst.setBundle basename="essentials.pagenotfound"/>

<div class="grid"><!--
 --><div class="grid__item medium--nine-twelfths large--seven-twelfths">
      <h1 class="article-header"><@fmt.message key="pagenotfound.title" var="title"/>${title?html}</h1>
      <p><@fmt.message key="pagenotfound.text"/><#--Skip XML escaping--></p>
    </div><!--
--></div>

<div>
  <@hst.include ref="container"/>
</div>
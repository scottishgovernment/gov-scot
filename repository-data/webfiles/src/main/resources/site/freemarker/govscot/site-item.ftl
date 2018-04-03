<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content">

        <div class="grid"><!--
         --><div class="grid__item medium--nine-twelfths large--seven-twelfths <#if document.additionalContent?has_content>push--medium--three-twelfths</#if>">
                <h1 class="article-header">${document.title}</h1>

                <div class="body-content  leader--first-para">
                    <@hst.html hippohtml=document.content/>
                </div>
            </div><!--
     --></div>

    </article>
</#if>
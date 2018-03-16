<#include "../../include/imports.ftl">

<#if document??>
    <article id="page-content">
    <@hst.manageContent hippobean=document/>
        <div class="grid"><!--
         --><div class="grid__item medium--eight-twelfths">
                <header class="article-header">
                    <p class="article-header__label">Policy</p>
                    <h1 class="article-header__title">${index.title?html}</h1>

                    <@hst.include ref="content-data"/>
                </header>
            </div><!--
     --></div>

        <div class="grid"><!--
         --><div class="grid__item medium--four-twelfths large--three-twelfths">
                <@hst.include ref="side-menu"/>
            </div><!--

         --><div class="grid__item medium--eight-twelfths large--seven-twelfths">
                <@hst.include ref="content"/>
            </div><!--
     --></div>
    </article>

<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

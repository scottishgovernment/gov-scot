<#include "../include/imports.ftl">

<#if document??>
    <div id="page-content">
    <@hst.cmseditlink hippobean=document/>
        <header class="article-header">
            <p class="article-header__label">Policy</p>
            <h1 class="article-header__title">${document.title?html}</h1>
        </header>
        <div class="grid">
            <div class="grid__item medium--nine-twelfths large--seven-twelfths push--medium--three-twelfths">
                <div class="body-content">
                    <div class="page-group__content body-content inner-shadow-top inner-shadow-top--no-desktop">
                        ${document.content.content}
                    </div>
                </div>
            </div>
            <div class="grid__item medium--three-twelfths pull--medium--nine-twelfths pull--large--seven-twelfths">
                <@hst.include ref="menu"/>
            </div>
    </div>
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

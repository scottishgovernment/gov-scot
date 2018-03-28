<#include "../../include/imports.ftl">

<#if document??>
    <div id="page-content">
    <@hst.manageContent hippobean=document/>

        <div class="grid"><!--
         --><div class="grid__item medium--nine-twelfths large--seven-twelfths push--medium--three-twelfths">
                <@hst.include ref="content"/>
            </div><!--

         --><div class="grid__item medium--three-twelfths pull--medium--nine-twelfths pull--large--seven-twelfths">
                <@hst.include ref="side-menu"/>
            </div><!--
     --></div>
    </div>

<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

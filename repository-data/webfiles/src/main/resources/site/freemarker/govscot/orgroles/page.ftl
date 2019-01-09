<#include "../../include/imports.ftl">

<#if document??>
<div class="layout--org-roles">
    <@hst.manageContent hippobean=document/>

    <div class="grid" id="page-content"><!--
    --><div class="grid__item medium--nine-twelfths push--medium--three-twelfths">

        <@hst.include ref="content"/>

    </div><!--
    --><div class="grid__item medium--three-twelfths pull--medium--nine-twelfths">
        <@hst.include ref="side-menu"/> 
    </div><!--
--></div>

<div class="grid"><!--
    --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths  push--medium--three-twelfths">
        <#include '../common/feedback-wrapper.ftl'>
    </div><!--
--></div>

</div>

<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "../common/canonical.ftl" />
</#if>

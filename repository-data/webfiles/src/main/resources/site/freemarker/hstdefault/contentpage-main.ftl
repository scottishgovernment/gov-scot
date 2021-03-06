<#include "../include/imports.ftl">

<#-- @ftlvariable name="document" type="org.example.beans.ContentDocument" -->
<#if document??>
  <article class="has-edit-button">
    <@hst.manageContent hippobean=document/>
    <h1>${document.title?html}</h1>
    <#if document.publicationDate??>
      <p>
        <@fmt.formatDate value=document.publicationDate.time type="both" dateStyle="medium" timeStyle="short"/>
      </p>
    </#if>
    <#if document.introduction??>
      <p>
        ${document.introduction?html}
      </p>
    </#if>
    <@hst.html hippohtml=document.content/>

    <@hst.html hippohtml=document.additional/>
  </article>
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Simple Content
  </div>
</#if>

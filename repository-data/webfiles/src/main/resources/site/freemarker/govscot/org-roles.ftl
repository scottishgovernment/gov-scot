<#include "../include/imports.ftl">

<#if document??>
<pre>TODO:
====
* list responsibilities
* duplicated John Swinney (two roles)
====
</pre>
<style>
#page-content a:not([href]), #page-content a[href="#"], #page-content a[href=""] {
    background: #fdd;
}
</style>

<#--  
<ul>
<#list document?keys as prop>
<li>${prop} = ${document[prop]}</li>
</#list>
</ul>  -->

    <div class="grid" id="page-content"><!--
        --><div class="grid__item medium--nine-twelfths push--medium--three-twelfths">

            <h1 class="article-header">
                ${document.title}
            </h1>

            <div class="grid">
                <div class="grid__item large--seven-ninths">

                    <#if document.content.content??>
                        ${document.content.content}
                    </#if>

                    <#assign orgName = document.organisationName/>
                    <#assign orgDescription = document.organisationDescription/>
                    <#assign orgRoles = document.orgRole/>
                    <#include 'about/org-roles-grid.ftl' />

                    <#assign orgName = document.secondaryOrganisationName/>
                    <#assign orgDescription = document.secondaryOrganisationDescription/>
                    <#assign orgRoles = document.secondaryOrgRole/>
                    <#include 'about/org-roles-grid.ftl' />

                </div>
            </div>

        </div><!--
        --><div class="grid__item medium--three-twelfths pull--medium--nine-twelfths">
            <div>
                <@hst.include ref="side-menu"/>
            </div>
        </div><!--
    --></div>
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

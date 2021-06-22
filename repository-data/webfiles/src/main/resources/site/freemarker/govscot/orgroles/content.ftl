<#include "../../include/imports.ftl">

<@hst.html hippohtml=document.content/>

<#assign orgName = document.organisationName/>
<#assign orgDescription = document.organisationDescription/>
<#if primaryPeople??>
    <#assign people = primaryPeople/>
    <#include 'org-roles-grid.ftl' />
</#if>

<#assign orgName = document.secondaryOrganisationName/>
<#assign orgDescription = document.secondaryOrganisationDescription/>
<#if secondaryPeople??>
    <#assign people = secondaryPeople/>
    <#include 'org-roles-grid.ftl' />
</#if>

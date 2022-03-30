<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<@hst.html hippohtml=document.content/>

<#if primaryPeople??>
    <#assign orgName = document.organisationName/>
    <#assign orgDescription = document.organisationDescription/>
    <#assign people = primaryPeople/>
    <#include 'org-roles-grid.ftl' />
</#if>

<#if secondaryPeople??>
    <#assign orgName = document.secondaryOrganisationName/>
    <#assign orgDescription = document.secondaryOrganisationDescription/>
    <#assign people = secondaryPeople/>
    <#include 'org-roles-grid.ftl' />
</#if>

<#if tertiaryPeople?? && document.tertiaryOrginisationName?? && document.tertiaryOrginisationName?has_content >
    <#assign orgName = document.tertiaryOrginisationName/>
    <#assign orgDescription = document.tertiaryOrganisationDescription/>
    <#assign people = tertiaryPeople/>
    <#include 'org-roles-grid.ftl' />
</#if>

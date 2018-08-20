<#include "../../include/imports.ftl">

<div class="body-content">
    <div class="page-group__content body-content inner-shadow-top inner-shadow-top--no-desktop">

        <h1 class="article-header">${document.title}</h1>

        <div class="grid">
            <div class="grid__item large--seven-ninths">

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
                
            </div>
        </div>


    </div>

</div>

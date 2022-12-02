<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.DynamicIssue" -->

<div class="ds_cb  ds_cb--page-title
    <#if backgroundcolor?? && backgroundcolor?length gt 0>ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>ds_cb--fullwidth</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">
            <#if document??>
            <div class="ds_cb__text  ds_cb__content">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>

                <@hst.html hippohtml=document.description/>
            </div>

            <@hst.manageContent hippobean=document documentTemplateQuery="new-dynamic-issue" parameterName="document" rootPath="topics"/>

            <#elseif editMode>
                <div class="ds_cb__text  ds_cb__content">
                    <header class="ds_page-header">
                        <h1 class="ds_page-header__title"><@placeholdertext lines=2/></h1>
                    </header>

                    <@placeholdertext lines=4/>
                </div>

                <div class="ds_cb__poster">
                    <@placeholderimage/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-dynamic-issue" parameterName="document" rootPath="topics"/>
            </#if>
        </div>
    </div>
</div>

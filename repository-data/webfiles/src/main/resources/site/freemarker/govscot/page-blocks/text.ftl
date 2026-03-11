<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Text" -->

<div class="ds_pb  ds_pb--text
<#if backgroundcolor?has_content> 
<#switch backgroundcolor?lower_case> 
  <#case 'secondary'>
  ds_pb--background-secondary
  <#break>
  <#case 'tertiary'>
  ds_pb--background-tertiary
  <#break>
  <#case 'theme'>
  ds_pb__theme--background-secondary
  <#break>
</#switch>
</#if>">
    <div class="ds_wrapper">
        <div class="ds_pb__inner <#if removebottompadding>  ds_!_padding-bottom--0</#if>">
            <#if document??>
                <div class="ds_pb__text  <#if position??>ds_pb__text--${position}</#if>">
                    <@hst.html hippohtml=document.content/>
                </div>

                <@hst.manageContent hippobean=document documentTemplateQuery="new-text-document" parameterName="document"/>
            <#elseif editMode>
                <div class="cms-blank  ds_pb__text  <#if position??>ds_pb__text--${position}</#if>">
                    <@placeholdertext lines=7/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document"/>
            </#if>
        </div>
    </div>
</div>

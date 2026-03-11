<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">

<div class="ds_pb  ds_pb--double-text
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
        <div class="ds_pb__inner <#if removebottompadding> ds_!_padding-bottom--0</#if>">

            <#if document1??>
                <div class="ds_pb__text" style="position: relative">
                    <@hst.html hippohtml=document1.content/>

                    <@hst.manageContent hippobean=document1 documentTemplateQuery="new-text-document" parameterName="document1"/>
                </div>
            <#elseif editMode>
                <div class="ds_pb__text  cms-blank">
                    <@placeholdertext lines=7/>

                    <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document1"/>
                </div>
            </#if>

            <#if document2??>
                <div class="ds_pb__text">
                    <@hst.html hippohtml=document2.content/>

                    <@hst.manageContent hippobean=document2 documentTemplateQuery="new-text-document" parameterName="document2"/>
                </div>
            <#elseif editMode>
                <div class="ds_pb__text  cms-blank">
                    <@placeholdertext lines=7/>

                    <@hst.manageContent documentTemplateQuery="new-text-document" parameterName="document2"/>
                </div>
            </#if>
        </div>
    </div>
</div>

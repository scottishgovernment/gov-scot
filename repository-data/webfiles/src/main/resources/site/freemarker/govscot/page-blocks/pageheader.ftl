<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Pageheading" -->
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document?? || editMode>
<header>
    <div class="ds_cb  ds_cb--page-title  ds_cb--page-title--text-only
    <#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>  ds_cb--fullwidth</#if>
    <#if neutrallinks>  ds_cb--neutral-links</#if>
    <#if widetext>  ds_cb--page-title--wide</#if>
    ">
        <div class="ds_wrapper">
            <div class="ds_cb__inner">
            <#if document??>
                <div class="ds_cb__text  ds_cb__content<#if verticalalign??>  ds_cb__text--${verticalalign}</#if>">
                    <div class="ds_page-header">
                        <h1 class="ds_page-header__title<#if lightheader>  ds_page-header__title--light</#if>">${document.title}</h1>
                    </div>
                    <@hst.html hippohtml=document.description/>
                    <#if document.cta??>
                    <#if document.link??>
                    <a href="<@hst.link hippobean=document.link/>" class="ds_button ds_button--has-icon">${document.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                        <use href="${iconspath}#chevron_right"></use>
                    </svg></a>
                    <#elseif document.externalLink?has_content>
                    <a href="${document.externalLink}" class="ds_button ds_button--has-icon">${document.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                        <use href="${iconspath}#chevron_right"></use>
                    </svg></a>
                    </#if>
                    </#if>
                </div>
            <@hst.manageContent hippobean=document documentTemplateQuery="new-pageheading-document" parameterName="document"/>

            <#elseif editMode>
                <div class="ds_cb__text  ds_cb__content">
                    <div class="ds_page-header">
                        <h1 class="ds_page-header__title"><@placeholdertext lines=2/></h1>
                    </div>

                    <@placeholdertext lines=8/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-pageheading-document" parameterName="document"/>
            </#if>
            </div>
        </div>
    </div>
</header>
</#if>

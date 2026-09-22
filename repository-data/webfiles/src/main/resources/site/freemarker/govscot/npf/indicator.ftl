<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/indicator-status-tag.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>
    <@hst.manageContent hippobean=document/>

    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
            <header class="ds_page-header  ds_layout__header">
                <span class="ds_page-header__label ds_content-label"><@indicatorStatusTag status=document.status/> National Indicator</span>
                <h1 class="ds_page-header__title">${document.title}</h1>
                <#assign lastUpdated = document.dateModified!document.getSingleProperty('hippostdpubwf:lastModificationDate')/>
                <#if lastUpdated??>
                <dl class="ds_metadata">
                    <div class="ds_metadata__item">
                        <dt class="ds_metadata__key">Last updated</dt>
                        <dd class="ds_metadata__value"><@fmt.formatDate value=lastUpdated.time type="both" pattern="d MMMM yyyy"/></dd>
                    </div>
                </dl>
                </#if>
                <hr />
                <#if document.summary?has_content>
                    <p class="ds_leader">${document.summary}</p>
                </#if>
                <#if document.dashboardUrl?has_content>
                    <a href="${document.dashboardUrl}" class="ds_button  ds_button--has-icon">Data dashboard<svg class="ds_icon" aria-hidden="true" role="img">
                        <use href="${iconspath}#chevron_right"></use>
                    </svg></a>
                </#if>
            </header>

            <div class="ds_layout__content">
                <article class="body-content">
                    <#if document.content??>
                        <@hst.html hippohtml=document.content/>
                    </#if>
                </article>
            </div>

            <div class="ds_layout__feedback">
                <#include '../common/feedback-wrapper.ftl'>
            </div>
        </main>
    </div>
</#if>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title} - National Performance Framework - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>

    <#if parent??>
        <@hst.headContribution category="dcMeta">
        <meta name="dc.title.series" content="${parent.title}"/>
        </@hst.headContribution>
        <@hst.headContribution category="dcMeta">
        <meta name="dc.title.series.link" content="<@hst.link hippobean=parent/>"/>
        </@hst.headContribution>
    </#if>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="National Indicator"/>
    </@hst.headContribution>

    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=lastUpdated.time type="both" pattern="yyyy-MM-dd HH:mm"/>"/>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <#include "../common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "../common/canonical.ftl" />
    <#include "../common/gtm-datalayer.ftl"/>
</#if>

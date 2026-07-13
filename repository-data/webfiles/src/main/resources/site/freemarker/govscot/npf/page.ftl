<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  gov_layout--npf">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <span class="ds_page-header__label  ds_content-label">National Performance Framework</span>
                    <h1 class="ds_page-header__title">${document.title}</h1>
                </header>
            </div>

            <div class="ds_layout__content">
                <article class="body-content">
                    <#if document.summary?has_content>
                        <p class="ds_leader">${document.summary}</p>
                    </#if>

                    <#if document.content??>
                        <@hst.html hippohtml=document.content/>
                    </#if>
                </article>

                <#if outcomes?has_content>
                    <div class="ds_card-grid-container">
                        <div class="ds_card-grid  ds_card-grid--min-height  ds_card-grid--narrow  ds_card-grid--medium-2">
                            <#list outcomes as outcome>
                                <div class="ds_card  ds_card--navigation">
                                    <div class="ds_card__content">
                                        <div class="ds_card__content-header">
                                            <h2 class="ds_card__title">
                                                <a class="ds_card__link  ds_card__link--cover" href="<@hst.link hippobean=outcome/>">${outcome.title}</a>
                                            </h2>
                                        </div>
                                        <#if outcome.summary?has_content>
                                            <div class="ds_card__content-main">${outcome.summary}</div>
                                        </#if>
                                    </div>
                                </div>
                            </#list>
                        </div>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__feedback">
                <#include '../common/feedback-wrapper.ftl'>
            </div>
        </main>
    </div>
</#if>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="National Performance Framework"/>
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

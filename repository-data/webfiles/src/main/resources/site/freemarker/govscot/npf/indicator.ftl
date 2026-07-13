<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  gov_layout--npf-indicator">
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

                    <#if document.indicatorCharts?has_content>
                        <#list document.indicatorCharts as chart>
                            <section class="gov_npf-chart-block">
                                <#if chart.title?has_content>
                                    <h2>${chart.title}</h2>
                                </#if>

                                <#if chart.content??>
                                    <@hst.html hippohtml=chart.content/>
                                </#if>

                                <#assign csvData = ""/>
                                <#if chart.chartData?? && chart.chartData.data?has_content>
                                    <#assign csvData = chart.chartData.data/>
                                </#if>

                                <#assign jsonConfig = "{}"/>
                                <#if chart.chartConfig?? && chart.chartConfig.config?has_content>
                                    <#assign jsonConfig = chart.chartConfig.config/>
                                </#if>

                                <div class="gov_npf-chart"
                                     data-csv="${csvData}"
                                     data-config="${jsonConfig}">
                                </div>
                            </section>
                        </#list>
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

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="NPF Indicator"/>
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

    <#if document.indicatorCharts?has_content>
        <@hst.headContribution category="footerScripts">
            <script type="module" src="<@hst.link path="/assets/scripts/npf-indicator.js"/>"></script>
        </@hst.headContribution>
        <@hst.headContribution category="footerScripts">
            <script nomodule="true" src="<@hst.link path="/assets/scripts/npf-indicator.es5.js"/>"></script>
        </@hst.headContribution>
    </#if>
</#if>

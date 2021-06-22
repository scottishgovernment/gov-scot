<#include "../../include/imports.ftl">
<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  gov_layout--about">
            <div class="ds_layout__sidebar">
                <@hst.include ref="side-menu"/>
            </div>

            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title?html}</h1>
                    <dl class="ds_page-header__metadata  ds_metadata">
                        <#if document.lastUpdatedDate??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Last updated</dt>
                                <dd class="ds_metadata__value"><@fmt.formatDate value=document.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </div>
                        </#if>
                    </dl>
                    <#include "../common/metadata.ftl"/>
                </header>
            </div>

            <div class="ds_layout__content">
                <@hst.html hippohtml=document.content/>

                <#if document.updateHistory?has_content>
                    <div class="update-history">
                        <#include '../common/update-history.ftl'/>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__feedback">
                <#include '../common/feedback-wrapper.ftl'>
            </div>
        </main>
    </div>

<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
    <div>
        <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
    </div>
</#if>

<@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/about.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/about.es5.js"/>"></script>
</@hst.headContribution>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>


    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "../common/canonical.ftl" />
    <#include "../common/gtm-datalayer.ftl"/>
</#if>

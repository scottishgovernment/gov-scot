<#include "../../include/imports.ftl">
<#if document??>
    <article id="page-content" class="layout--about">

    <@hst.manageContent hippobean=document/>
     <div class="grid"><!--
        --><div class="grid__item medium--three-twelfths large--three-twelfths">
                <!--noindex-->
                <@hst.include ref="side-menu"/>
                <!--endnoindex-->
            </div><!--
        --><div class="grid__item medium--nine-twelfths large--seven-twelfths">
                <@hst.include ref="content"/>

                <#if document.updateHistory?has_content>
                <div class="update-history">
                    <#include '../common/update-history.ftl'/>
                </div>
                </#if>
            </div><!--
     --></div>
    </article>

    <div class="grid"><!--
        --><div class="grid__item push--medium--three-twelfths medium--nine-twelfths large--seven-twelfths">
            <#include '../common/feedback-wrapper.ftl'>
        </div><!--
    --></div>

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

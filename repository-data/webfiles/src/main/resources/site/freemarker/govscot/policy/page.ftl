<#include "../../include/imports.ftl">

<#if document??>
    <article id="page-content" class="layout--policy">
    <@hst.manageContent hippobean=document/>
        <div class="grid"><!--
         --><div class="grid__item medium--eight-twelfths">
                <header class="article-header">
                    <@hst.link var="link" hippobean=index />
                    <p class="article-header__label">Policy<#if latest??> - Latest</#if></p>
                    <h1 class="article-header__title">${index.title?html}</h1>

                    <section class="content-data">
                        <#include '../common/content-data.ftl'/>
                    </section>
                </header>
            </div><!--
     --></div>
    </article>

    <div class="grid"><!--
     --><div class="grid__item medium--four-twelfths large--three-twelfths">
            <#include 'side-menu.ftl'>
        </div><!--

     --><div class="grid__item medium--eight-twelfths large--seven-twelfths">
            <#if latest??>
                <#include 'latest.ftl'/>
            <#else>
                <#include 'content.ftl'/>
            </#if>
        </div><!--
 --></div>

    <div class="grid"><!--
        --><div class="grid__item  push--medium--four-twelfths  push--large--three-twelfths  medium--eight-twelfths  large--seven-twelfths">
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
    <script type="module" src="<@hst.webfile path="/assets/scripts/policy.js"/>"></script>
</@hst.headContribution>
<@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/policy.es5.js"/>"></script>
</@hst.headContribution>


<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${index.title?html}<#if document.title != index.title>: ${document.title?html}</#if> - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "../common/canonical.ftl" />

    <#assign uuid = document.getProperty('jcr:uuid')/>
    <#assign lastUpdated = document.getProperty('hippostdpubwf:lastModificationDate')/>
    <#assign dateCreated = document.getProperty('hippostdpubwf:creationDate')/>
    <#assign document = index/>
    <#include "../common/gtm-datalayer.ftl"/>
</#if>

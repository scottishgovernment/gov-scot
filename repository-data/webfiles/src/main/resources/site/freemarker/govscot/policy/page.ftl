<#include "../../include/imports.ftl">

<#if document??>
    <div id="page-content">
    <@hst.manageContent hippobean=document/>
        <div class="grid"><!--
         --><div class="grid__item medium--eight-twelfths">
                <header class="article-header">
                    <@hst.link var="link" hippobean=index />
                    <p class="article-header__label">Policy<#if link?ends_with('/latest')> - Latest</#if></p>
                    <h1 class="article-header__title">${index.title?html}</h1>

                    <section class="content-data">
                        <#include '../common/content-data.ftl'/>
                    </section>
                </header>
            </div><!--
     --></div>

        <div class="grid"><!--
         --><div class="grid__item medium--four-twelfths large--three-twelfths">
                <@hst.include ref="side-menu"/>
            </div><!--

         --><div class="grid__item medium--eight-twelfths large--seven-twelfths">
                <@hst.include ref="content"/>
            </div><!--
     --></div>
    </div>

<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

<@hst.headContribution category="footerScripts">
    <script src="<@hst.webfile path="/assets/scripts/policy.js"/>" type="text/javascript"></script>
</@hst.headContribution>
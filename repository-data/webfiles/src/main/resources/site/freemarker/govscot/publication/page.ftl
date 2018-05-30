<#include "../../include/imports.ftl">
<#include "../common/macros/format-file-size.ftl">

<#if document??>
</div>

<@hst.manageContent hippobean=document/>

<article id="page-content">

    <#--------------------- HEADER SECTION --------------------->
    <div class="top-matter">
        <div class="wrapper">
            <header class="article-header no-bottom-margin">
                <#if isMultiPagePublication>

                    <div class="grid"><!--
                     --><div class="grid__item">
                            <p class="article-header__label">Publication - ${document.label}</p>
                        </div><!--
                 --></div>
                    <div class="grid"><!--
                     --><div class="grid__item medium--nine-twelfths">
                            <div class="grid">
                                <div class="grid__item"><h1 class="article-header__title">${document.title}</h1></div><!--

                             --><div class="grid__item large--four-twelfths">
                                    <#include 'metadata.ftl'/>
                                </div><!--

                             --><div class="grid__item large--eight-twelfths">
                                    <p class="leader">${document.summary}</p>
                                </div><!--
                         --></div>
                        </div><!--

                     --><div class="grid__item  push--large--one-twelfth medium--three-twelfths large--two-twelfths">
                            <#if pages?has_content>

                                <#if documents?has_content>
                                    <div class="hidden-xsmall">
                                        <#include 'header-document-info.ftl'/>
                                    </div>
                                </#if>

                                <div class="publication-info__header">
                                    <a href="#files-list" class="js-expand-downloads publication-info__preamble publication-info__preamble--icon publication-info__preamble--icon--pdf visible-xsmall">

                                        <span class="publication-info__file-icon file-icon file-icon--gen"></span>
                                        This publication is available to download in other formats. <span class="publication-info__preamble-expand">More</span>&hellip;

                                    </a>
                                </div>

                                <div class="publication-info__body visible-xsmall">
                                    <section id="files-list" class="publication-info__section publication-info__collapsible publication-info__collapsible--collapsed-initial publication-info__collapsible--not-tablet">
                                        <#if documents?has_content>
                                            <#include 'header-document-info.ftl'/>
                                            <#include 'supporting-files.ftl'/>
                                        </#if>
                                    </section>
                                </div>
                            </#if>
                        </div><!--
                 --></div>

                <#else>

                    <div class="grid"><!--
                     --><div class="grid__item large--ten-twelfths">
                            <p class="article-header__label">Publication - ${document.label}</p>
                            <h1 class="article-header__title">${document.title}</h1>
                        </div><!--
                 --></div>
                    <div class="grid"><!--
                     --><div class="grid__item  large--three-twelfths">
                            <#include 'metadata.ftl'/>
                        </div><!--

                     --><div class="grid__item  large--seven-twelfths">
                            <div class="leader">
                                <@hst.html var="executiveSummary" hippohtml=document.executiveSummary/>
                                <#if executiveSummary?has_content>
                                    ${executiveSummary}
                                <#else>
                                    ${document.summary}
                                </#if>
                            </div>
                        </div><!--
                 --></div>

                </#if>
            </header>
        </div>
    </div>

    <#include 'sticky-document-info.ftl'/>

    <#--------------------- BODY SECTION --------------------->
    <div class="inner-shadow-top  js-sticky-header-position <#if isMultiPagePublication>inner-shadow-top--no-mobile</#if>">

        <div class="wrapper js-content-wrapper">
            <div class="grid"><!--

             --><div class="grid__item  medium--four-twelfths  large--three-twelfths <#if !isMultiPagePublication>hidden-xsmall  hidden-small  hidden-medium</#if>">
                    <#if pages??>
                        <#include 'side-menu.ftl'/>
                    </#if>
                </div><!--

                <#if isMultiPagePublication>
                 --><div class="grid__item medium--eight-twelfths large--seven-twelfths">

                        <div class="body-content publication-content js-content-wrapper inner-shadow-top  inner-shadow-top--no-desktop ">
                            <@hst.html hippohtml=currentPage.content/>
                        </div>

                        <div class="grid  page-nav"><!--
                         --><div class="grid__item  push--medium--six-twelfths  medium--six-twelfths  page-nav__item">
                                <#if next??>
                                    <a title="Next page" href="<@hst.link hippobean=next/>" class="page-nav__button  page-nav__button--right  js-next">
                                        <span data-label="next" class="page-nav__text">${next.title}</span>
                                    </a>
                                </#if>
                            </div><!--
                         --><div class="grid__item  medium--six-twelfths  pull--medium--six-twelfths  page-nav__item">
                                <#if prev??>
                                    <a title="Previous page" href="<@hst.link hippobean=prev/>" class="page-nav__button  page-nav__button--left  js-previous">
                                        <span data-label="prev" class="page-nav__text">${prev.title}</span>
                                    </a>
                                </#if>
                            </div><!--
                     --></div>

                        <hr>

                        <@hst.html hippohtml=document.contact var="contact"/>
                        <#if contact?has_content>
                            <section class="publication-info__section publication-info__contact">
                                <h3 class="emphasis">Contact</h3>
                                ${contact}
                            </section>
                        </#if>

                    </div><!--
                <#else>
                 --><div class="grid__item large--seven-twelfths">

                        <#if document.content?has_content ||
                        document.attendees?has_content ||
                        document.actions?has_content>
                            <div id="preamble">
                                <@hst.html hippohtml=document.content var="content"/>
                                <#if content?has_content>
                                    <div class="body-content publication-body">
                                        ${content}
                                    </div>
                                </#if>

                                <#--! BEGIN 'minutes' format-specific fields-->
                                <@hst.html hippohtml=document.attendees var="attendees"/>
                                <#if attendees?has_content>
                                    <h2>Attendees and apologies</h2>
                                    ${attendees}
                                </#if>

                                <@hst.html hippohtml=document.actions var="actions"/>
                                <#if actions?has_content>
                                    <h2>Items and actions</h2>
                                    ${actions}
                                </#if>
                                <#--! END 'minutes' format-specific fields-->
                            </div>
                        </#if>

                        <#if documents??>
                            <#assign hasAttachedDocument = '' />
                            <#list documents as attachedDocument>
                                <#assign hasAttachedDocument = true />
                            </#list>

                            <#if hasAttachedDocument?has_content>
                                <section class="document-section">
                                    <#list documents as attachedDocument>
                                        <#include 'body-document-info.ftl'/>
                                    </#list>
                                </section>
                            </#if>

                            <#if groupedDocumentFolders??>
                                <#list groupedDocumentFolders as folder>
                                    <section class="document-section">
                                        <h2>${folder.displayName}</h2>
                                        <#list folder.documents as attachedDocument>
                                            <#include 'body-document-info.ftl'/>
                                        </#list>
                                    </section>
                                </#list>
                            </#if>
                        </#if>

                        <@hst.html hippohtml=document.epilogue var="epilogue"/>
                        <#if epilogue?has_content>
                        <div id="epilogue">
                            ${epilogue}
                        </div>
                        </#if>

                        <@hst.html hippohtml=document.contact var="contact"/>
                        <#if contact?has_content>
                            <div class="publication-info__contact">
                                <h3 class="emphasis">Contact</h3>
                                ${contact}
                            </div>
                        </#if>
                    </div><!--
                </#if>

         --></div>

        </div>
    </div>
</article>

</#if>

<@hst.headContribution category="footerScripts">
<script src="<@hst.webfile path="/assets/scripts/publication.js"/>" type="text/javascript"></script>
</@hst.headContribution>

<@hst.headContribution category="pageTitle"><title>${document.title} - gov.scot</title></@hst.headContribution>

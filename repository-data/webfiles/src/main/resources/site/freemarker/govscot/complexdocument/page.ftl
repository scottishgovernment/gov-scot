<#include "../../include/imports.ftl">

<#if document??>
</div>

<@hst.manageContent hippobean=document/>

<article id="page-content" class="layout--publication">

    <#--------------------- HEADER SECTION --------------------->
    <div class="top-matter">
        <div class="wrapper">
            <header class="article-header no-bottom-margin">

                    <div class="grid"><!--
                     --><div class="grid__item large--ten-twelfths">
                            <p class="article-header__label">Publication - ${document.label}</p>
                            <h1 class="article-header__title">${document.title}</h1>
                        </div><!--
                 --></div>
                    <div class="grid"><!--
                     --><div class="grid__item  large--three-twelfths">
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

            </header>
        </div>
    </div>

    <#--------------------- BODY SECTION --------------------->
    <div class="inner-shadow-top  js-sticky-header-position inner-shadow-top--no-mobile">

        <div class="wrapper js-content-wrapper">
            <div class="grid"><!--

             --><div class="grid__item  medium--four-twelfths  large--three-twelfths ">
                </div><!--

                 --><div class="grid__item medium--eight-twelfths large--seven-twelfths">

                        <div class="body-content publication-content js-content-wrapper inner-shadow-top  inner-shadow-top--no-desktop ">

                            <@hst.link var="baseurl" hippobean=document canonical=true/>

                            <p>
                                <a href="${baseurl}">
                                Menu
                                </a>
                            </p>
                        </div>

                        <#if isAboutPage??>

                            <p>This is the About this publication page.</p>

                            <p>Content: <@hst.html hippohtml=document.content/></p>

                            <#if documents?has_content>
                                <p>Main document: ${documents[0].title}</p>
                            </#if>

                        <#elseif isDocumentsPage??>

                            This is the Supporting documents page.

                            <ul>
                                <#list documents as document>
                                    <li>${document.title}</li>
                                </#list>
                            </ul>

                        <#elseif currentPage == document>

                            <ul>
                                <#list chapters as chapter>
                                    <li>${chapter.displayName}
                                        <#list chapter.documents as section>
                                            <ol><a href="<@hst.link hippobean=section/>">${section.title}</a></ol>
                                        </#list>
                                    </li>
                                </#list>
                            </ul>

                            <a href="${baseurl + 'about/'}">About this publication</a><br><br>

                            <#if documents?has_content>
                                <a href="${baseurl + 'documents/'}">Supporting documents</a><br><br>
                            </#if>

                            <#if next??>
                                <a title="Next page" href="<@hst.link hippobean=next/>" class="page-nav__button  page-nav__button--right  js-next">
                                    <span data-label="next" class="page-nav__text">${next.title}</span>
                                </a>
                            </#if>

                            <#if prev??>
                                <a title="Previous page" href="<@hst.link hippobean=prev/>" class="page-nav__button  page-nav__button--left  js-previous">
                                    <span data-label="prev" class="page-nav__text">${prev.title}</span>
                                </a>
                            </#if>

                        <#else>
                            <h3>${currentChapter.displayName}</h3>
                            <h4>${currentPage.title}</h4>
                            This is a section

                            <#if next??>
                                <a title="Next page" href="<@hst.link hippobean=next/>" class="page-nav__button  page-nav__button--right  js-next">
                                    <span data-label="next" class="page-nav__text">${next.title}</span>
                                </a>
                            </#if>

                            <#if prev??>
                                <a title="Previous page" href="<@hst.link hippobean=prev/>" class="page-nav__button  page-nav__button--left  js-previous">
                                    <span data-label="prev" class="page-nav__text">${prev.title}</span>
                                </a>
                            </#if>
                        </#if>

                    </div><!--


         --></div>

        </div>
    </div>
</article>

<div class="wrapper">
    <div class="grid"><!--
        --><div class="grid__item  large--seven-twelfths  push--large--three-twelfths">
            <#include '../common/feedback-wrapper.ftl'>
        </div><!--
    --></div>
</div>

</#if>

<#--<@hst.headContribution category="footerScripts">-->
<#--<script src="<@hst.webfile path="/assets/scripts/publication.js"/>" type="text/javascript"></script>-->
<#--</@hst.headContribution>-->

<#if document??>
    <@hst.headContribution category="pageTitle"><title>${document.title} - gov.scot</title></@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "../common/canonical.ftl" />
</#if>

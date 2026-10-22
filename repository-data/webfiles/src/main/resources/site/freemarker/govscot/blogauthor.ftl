<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#if document??>
    <@hst.manageContent hippobean=document/>
    <div class="ds_wrapper">
        <main id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <#if document.summary?has_content>
                        <p class="ds_page-header__subtitle">${document.summary}</p>
                    </#if>
                </header>
            </div>

            <div class="ds_layout__content">
                <#if pageable?? && pageable.items?has_content>
                    <h2>Posts by ${document.title}</h2>
                    <div class="ds_search-results">
                        <ul class="ds_search-results__list">
                            <#list pageable.items as post>
                                <@hst.link var="postlink" hippobean=post/>
                                <li class="ds_search-result<#if post.heroImage??>  ds_search-result__has-media</#if>">
                                    <#if post.heroImage??>
                                        <div class="ds_search-result__media-wrapper">
                                            <a class="ds_search-result__media-link" href="${postlink}">
                                                <img class="ds_search-result__media" src="<@hst.link hippobean=post.heroImage.smallcolumns/>" alt="${post.heroImageAlt!""}"/>
                                            </a>
                                        </div>
                                    </#if>
                                    <div>
                                        <h3 class="ds_search-result__title">
                                            <a class="ds_search-result__link" href="${postlink}">${post.title}</a>
                                        </h3>
                                        <#if post.summary?has_content>
                                            <p class="ds_search-result__summary">${post.summary}</p>
                                        </#if>
                                        <dl class="ds_search-result__metadata  ds_metadata">
                                            <div class="ds_metadata__item">
                                                <dt class="ds_metadata__key">Published</dt>
                                                <dd class="ds_metadata__value"><@fmt.formatDate value=post.publicationDate.time type="both" pattern="dd MMMM yyyy HH:mm"/></dd>
                                            </div>
                                        </dl>
                                    </div>
                                </li>
                            </#list>
                        </ul>
                    </div>
                    <#include "../include/pagination.ftl">
                </#if>
            </div>

            <div class="ds_layout__feedback">
                <#include './common/feedback-wrapper.ftl'>
            </div>
        </main>
    </div>
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
<div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
</div>
</#if>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>

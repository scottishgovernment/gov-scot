<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "./common/macros/lang-attributes.ftl">

<#if document??>
    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <dl <@revertlang document /> class="ds_page-header__metadata  ds_metadata">
                        <div class="ds_metadata__item">
                            <dt class="ds_metadata__key">Published</dt>
                            <dd class="ds_metadata__value"><@fmt.formatDate value=document.publicationDate.time type="both" pattern="dd MMMM yyyy HH:mm"/></dd>
                        </div>

                        <#if document.author??>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Author</dt>
                                <dd class="ds_metadata__value">
                                    <@hst.link var="authorlink" hippobean=document.author/>
                                    <a href="${authorlink}">${document.author.title}</a>
                                </dd>
                            </div>
                        </#if>

                        <#if document.categories?size gt 0>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Category</dt>
                                <dd class="ds_metadata__value">
                                    <#list document.categories as category>${category}<#sep>, </#sep></#list>
                                </dd>
                            </div>
                        </#if>
                    </dl>
                 </header>
            </div>

            <div class="ds_layout__content">
                <#if document.heroImage??>
                    <div class="ds_article-hero">
                        <img class="ds_article-hero__image" src="<@hst.link hippobean=document.heroImage.original/>" alt="${document.heroImageAlt!""}"/>
                    </div>
                </#if>

                <#if document.summary != ''>
                    <div class="ds_leader-first-paragraph">
                        <p>${document.summary}</p>
                    </div>

                    <@hst.html hippohtml=document.content/>
                <#else>
                    <div class="ds_leader-first-paragraph">
                        <@hst.html hippohtml=document.content/>
                    </div>
                </#if>
            </div>

            <div class="ds_layout__sidebar">
                <!--noindex-->
                <#if blog??>
                    <#if blog.summary?has_content>
                        <section class="gov_content-block">
                            <h2 class="gov_content-block__title">About ${blog.title}</h2>
                            <p>${blog.summary}</p>
                        </section>
                    </#if>

                    <#if blog.contactInformation.twitter?has_content
                        ||   blog.contactInformation.flickr?has_content
                        ||   blog.contactInformation.website?has_content
                        ||   blog.contactInformation.email?has_content
                        ||   blog.contactInformation.facebook?has_content
                        ||   blog.contactInformation.youtube?has_content
                        ||   blog.contactInformation.blog?has_content>

                        <#assign contactInformation = blog.contactInformation/>
                        <#assign abbreviatedContactInformation = false />
                        <section class="gov_content-block">
                            <#include 'common/contact-information.ftl' />
                        </section>
                    </#if>
                </#if>
                <!--endnoindex-->
            </div>

            <div <@revertlang document /> class="ds_layout__feedback">
                <#include 'common/feedback-wrapper.ftl'>
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
    <#if document.heroImage??>
        <#assign socialImage = document.heroImage.original/>
    </#if>
    <#assign schemaType = "BlogPosting"/>
    <#if document.author??>
        <#assign authorName = document.author.title/>
        <@hst.link var="authorUrl" hippobean=document.author fullyQualified=true/>
    </#if>
    <#include "common/schema.article.ftl"/>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Blog post"/>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>

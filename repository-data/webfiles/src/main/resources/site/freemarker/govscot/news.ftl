<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#include "./common/macros/lang-attributes.ftl">

<#if document??>
    <div class="ds_wrapper">
        <main <@lang document/> id="main-content" class="ds_layout  ds_layout--article">
            <div class="ds_layout__header">
                <header class="ds_page-header">
                    <span <@revertlang document /> class="ds_page-header__label  ds_content-label"><span id="sg-meta__publication-type">News</span></span>

                    <h1 class="ds_page-header__title">${document.title}</h1>
                    <dl <@revertlang document /> class="ds_page-header__metadata  ds_metadata">
                        <div class="ds_metadata__item">
                            <dt class="ds_metadata__key">Published</dt>
                            <dd class="ds_metadata__value"><@fmt.formatDate value=document.publicationDate.time type="both" pattern="dd MMMM yyyy HH:mm"/></dd>
                        </div>

                        <#if document.topics?size gt 0>
                            <div class="ds_metadata__item">
                                <dt class="ds_metadata__key">Topic</dt>

                                <dd class="ds_metadata__value">
                                    <#list document.topics as topic>
                                        <@hst.link var="link" hippobean=topic/>
                                        <a data-navigation="topic-${topic?index + 1}" href="${link}" class="sg-meta__topic">${topic.title}</a><#sep>, </#sep>
                                    </#list>
                                </dd>
                            </div>
                        </#if>
                    </dl>
                 </header>
            </div>

            <div class="ds_layout__content">
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
                <div <@revertlang document /> class="ds_article-aside">
                    <h2>Contact</h2>
                    <p>
                        <a href="/about/contact-information/media-enquiries/">Media enquiries</a>
                    </p>
                </div>

                <#if document.heroImage.title?has_content || (document.attachments?? && document.attachments?size > 0)>
                    <div <@revertlang document /> class="ds_article-aside">
                        <h2>Media</h2>

                        <ul class="ds_no-bullets">
                            <#if document.heroImage??>
                                <#if document.heroImage.url != ''>
                                    <li><a href="${document.heroImage.url}">Image 1: ${document.heroImage.title}</a></li>
                                    <#assign firstAttachmentOffset=2>
                                <#else>
                                    <#assign firstAttachmentOffset=1>
                                </#if>
                            </#if>

                            <#list document.attachments as attachment>
                                <li><a href="${attachment.url}">Image ${attachment?index + firstAttachmentOffset}: ${attachment.title}</a></li>
                            </#list>
                        </ul>
                    </div>
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
    <#include "common/schema.article.ftl"/>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="News"/>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>

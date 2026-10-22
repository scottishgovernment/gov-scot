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
                                            <#if post.author??>
                                                <div class="ds_metadata__item">
                                                    <dt class="ds_metadata__key">Author</dt>
                                                    <dd class="ds_metadata__value">
                                                        <@hst.link var="authorlink" hippobean=post.author/>
                                                        <a href="${authorlink}">${post.author.title}</a>
                                                    </dd>
                                                </div>
                                            </#if>
                                        </dl>
                                    </div>
                                </li>
                            </#list>
                        </ul>
                    </div>
                    <#include "../include/pagination.ftl">
                </#if>

                <@hst.html hippohtml=document.content/>
            </div>

            <div class="ds_layout__sidebar">
                <!--noindex-->
                <#if document.slug?has_content>
                    <section class="gov_content-block">
                        <h2>Subscribe</h2>
                        <p><a class="ds_link" href="<@hst.link path="/feed/${document.slug}.xml"/>">RSS feed for ${document.title}</a></p>
                    </section>
                </#if>
                <#if document.contactInformation.twitter?has_content
                    ||   document.contactInformation.flickr?has_content
                    ||   document.contactInformation.website?has_content
                    ||   document.contactInformation.email?has_content
                    ||   document.contactInformation.facebook?has_content
                    ||   document.contactInformation.youtube?has_content
                    ||   document.contactInformation.blog?has_content>

                    <#assign contactInformation = document.contactInformation/>
                    <#assign abbreviatedContactInformation = false />
                    <section class="gov_content-block">
                        <#include 'common/contact-information.ftl' />
                    </section>
                </#if>
                <!--endnoindex-->
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
    <@hst.link var="bloglink" canonical=true fullyQualified=true/>
    <@hst.headContribution category="schema">
        <script type="application/ld+json" nonce="${nonce}">
{
    "@context": "https://schema.org",
    "@type": "Blog",
    "name": "${document.title?json_string}",
    "url": "${bloglink}",
    "mainEntityOfPage": {
        "@type": "WebPage",
        "@id": "${bloglink}"
    }<#if document.summary?has_content>,
    "description": "${document.summary?json_string}"</#if>
}
        </script>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.description" content="${document.summary}"/>
    </@hst.headContribution>

    <@hst.headContribution category="dcMeta">
        <meta name="dc.format" content="Blog"/>
    </@hst.headContribution>

    <@hst.headContribution category="pageTitle">
        <title>${document.title} - gov.scot</title>
    </@hst.headContribution>

    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription}"/>
    </@hst.headContribution>

    <#include "common/metadata.social.ftl"/>

    <@hst.link var="canonicalitem" hippobean=document canonical=true/>
    <#include "common/canonical.ftl" />
    <#include "common/gtm-datalayer.ftl"/>
</#if>

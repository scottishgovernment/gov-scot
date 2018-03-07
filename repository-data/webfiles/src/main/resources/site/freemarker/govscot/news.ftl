<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content">
    <@hst.cmseditlink hippobean=document/>
        <div class="grid">
            <div class="grid__item medium--eight-twelfths xlarge--seven-twelfths">

                <header class="article-header">
                    <p class="article-header__label">News</p>
                    <h1 class="article-header__title">${document.title?html}</h1>
                </header>

                <section class="content-data">
                    <div class="content-data__list">
                        <span class="content-data__label">Published:</span>
                        <span class="content-data__value"><strong><@fmt.formatDate value=document.publishedDate.time type="both" pattern="dd MMM yyyy HH:mm"/></strong></span>
                    </div>

                    <#if document.topics?first??>
                        <div>
                            <dl class="content-data__list">

                                <dt class="content-data__label">Part of:</dt>

                                <dd class="content-data__value">
                                    <#list document.topics as topic>
                                        <@hst.link var="link" hippobean=topic/>
                                        <a href="${link}">${topic.title}</a><#sep>, </sep>
                                    </#list>
                                </dd>
                            </dl>
                        </div>
                    </#if>

                </section>

                <div class="body-content">

                    <#if document.summary??>
                        <div class="leader leader--first-para">
                            <p>${document.summary?html}</p>
                        </div>

                        ${document.content.content}
                    <#else>
                        <div class="leader leader--first-para">
                            ${document.content.content}
                        </div>
                    </#if>

                    <#if document.background.content != ''>
                        <h2>Background</h2>
                        ${document.background.content}
                    </#if>


                    <aside class="visible-xsmall visible-medium">
                        <div class="sidebar-block no-bullets">
                            <h3 class="emphasis sidebar-block__heading">Contacts</h3>
                            <p>
                                <a href="/about/contact-information/media-enquiries/">Media enquiries</a>
                            </p>
                        </div>

                        <#if document.heroImage?? || document.attachments??>       
                            <div class="sidebar-block no-bullets">
                                <h3 class="emphasis sidebar-block__heading">Media</h3>
                                <ul>
                                    <li><a href="${document.heroImage.url}">Image 1: ${document.heroImage.title}</a></li>
                                    <#list document.attachments as attachment>
                                        <li><a href="${attachment.url}">Image ${attachment?index + 2}: ${attachment.title}</a></li>
                                    </#list>
                                </ul>
                            </div>
                        </#if>

                    </aside>

                </div>

            </div>

            <div class="grid__item medium--three-twelfths push--medium--one-twelfth push--xlarge--two-twelfths">
                <aside class="hidden-xsmall hidden-medium">
                    <div class="sidebar-block no-bullets">
                        <h3 class="emphasis sidebar-block__heading no-top-margin">Contacts</h3>
                        <p>
                            <a href="/about/contact-information/media-enquiries/">Media enquiries</a>
                        </p>
                    </div>

                    <#if document.heroImage.url != '' || document.attachments??>
                        <div class="sidebar-block no-bullets">
                            <h3 class="emphasis sidebar-block__heading no-top-margin">Media</h3>
                            <ul>
                                <#if document.heroImage.url != ''>
                                    <li><a href="${document.heroImage.url}">Image 1: ${document.heroImage.title}</a></li>
                                    <#assign firstAttachmentOffset=2>
                                <#else>
                                    <#assign firstAttachmentOffset=1>
                                </#if>
                                <#list document.attachments as attachment>
                                    <li><a href="${attachment.url}">Image ${attachment?index + firstAttachmentOffset}: ${attachment.title}</a></li>
                                </#list>
                            </ul>
                        </div>
                    </#if>
                </aside>
            </div>


        </div>

    </article>
<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

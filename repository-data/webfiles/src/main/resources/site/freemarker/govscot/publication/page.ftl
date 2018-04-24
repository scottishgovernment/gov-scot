<#include "../../include/imports.ftl">

<#if document??>
</div>

<article id="page-content">

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
                        <section class="content-data">
                            <div class="content-data__list">
                                <span class="content-data__label">Published:</span>
                                <span class="content-data__value"><strong><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMM yyyy"/></strong></span>
                            </div>

                            <#assign index=document/>
                            <#include '../common/content-data.ftl'/>

                            <#--! BEGIN 'minutes' format-specific fields-->
                            <#if document.label == 'minutes'>
                                <#if document.officialdate?has_content>
                                    <div class="content-data__list">
                                        <span class="content-data__label">Date of meeting:</span>
                                        <span class="content-data__value"><strong><@fmt.formatDate value=document.officialdate.time type="both" pattern="d MMM yyyy"/></strong></span>
                                    </div>
                                </#if>

                                <#if document.nextMeetingDate?has_content>
                                    <div class="content-data__list">
                                        <span class="content-data__label">Date of next meeting:</span>
                                        <span class="content-data__value"><strong><@fmt.formatDate value=document.nextMeetingDate.time type="both" pattern="d MMM yyyy"/></strong></span>
                                    </div>
                                </#if>

                                <#if document.location?has_content>
                                    <div class="content-data__list">
                                        <span class="content-data__label">Location:</span>
                                        <span class="content-data__value"><strong>${document.location}</strong></span>
                                    </div>
                                </#if>
                            </#if>
                            <#--! END 'minutes' format-specific fields-->

                            <#--! BEGIN 'speech or statement' format-specific fields-->
                            <#if document.label == 'speech / ministerial statement'>
                                <#if document.officialdate?has_content>
                                    <div class="content-data__list">
                                        <span class="content-data__label">Date of speech:</span>
                                        <span class="content-data__value"><strong><@fmt.formatDate value=document.officialdate.time type="both" pattern="d MMM yyyy"/></strong></span>
                                    </div>
                                </#if>

                                <#if document.speechDeliveredBy?has_content>
                                    <div class="content-data__list">
                                        <span class="content-data__label">Delivered by:</span>
                                        <span class="content-data__value"><strong>${document.speechDeliveredBy}</strong></span>
                                    </div>
                                </#if>

                                <#if document.location?has_content>
                                    <div class="content-data__list">
                                        <span class="content-data__label">Location:</span>
                                        <span class="content-data__value"><strong>${document.location}</strong></span>
                                    </div>
                                </#if>
                            </#if>
                            <#--! END 'speech or statement' format-specific fields-->

                        </section>
                    </div><!--

                 --><div class="grid__item  large--seven-twelfths">
                        <div class="leader">
                            <@hst.html hippohtml=document.executiveSummary/>
                        </div>
                    </div><!--
             --></div>
            </header>
        </div>
    </div>

    <div class="inner-shadow-top  js-sticky-header-position">

        <div class="wrapper js-content-wrapper">
            <div class="grid"><!--

             --><div class="grid__item  medium--four-twelfths  large--three-twelfths  hidden-xsmall  hidden-small  hidden-medium">
                    <#--sidebar-->
                </div><!--

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
                    <section class="document-section">
                        <#-- todo: groups of documents and titles -->
                            <#--{{#if title}}<h2>{{title}}</h2>{{/if}}-->

                        <#list documents as attachedDocument>
                            <div class="document-info <#if attachedDocument.highlighted>document-info--limelight</#if>">

                                <#assign filenameExtension = attachedDocument.document.filename?keep_after_last(".")?upper_case/>
                                <#assign filenameWithoutExtension = attachedDocument.document.filename?keep_before_last(".")/>
                                <#--<@hst.link hippobean=attachedDocument.thumbnails[1]/>-->


                                <div class="document-info__body">
                                    <div class="document-info__thumbnail  document-info__thumbnail--pdf">
                                        <#if filenameExtension == "PDF">
                                        <a class="document-info__thumbnail-link" href="<@hst.link hippobean=attachedDocument.document/>?inline=true">
                                            <img
                                                    alt="View this document"
                                                    class="document-info__thumbnail-image"
                                                    src="<@hst.link hippobean=attachedDocument.thumbnails[0]/>"
                                                    srcset="
                                                    <#list attachedDocument.thumbnails as thumbnail>
                                                        <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                                    </#list>"
                                                    sizes="(min-width: 768px) 165px, 107px" />
                                        </a>
                                        <#else>
                                        <a title="View this document" href="<@hst.link hippobean=attachedDocument.document/>?inline=true" class="file-icon--<#if attachedDocument.highlighted>large<#else>medium</#if>  file-icon  file-icon--${filenameExtension}"></a>
                                        </#if>
                                    </div>
                                </div>

                                <div class="document-info__text">

                                    <h3 class="document-info__title"><a class="no-icon" href="<@hst.link hippobean=attachedDocument.document/>?inline=true">${attachedDocument.title}</a></h3>

                                    <div class="document-info__file-details">
                                        <dl class="document-info__meta">
                                            <dt class="hidden">File type</dt>
                                            <dd><b><#if attachedDocument.pageCount?has_content && attachedDocument.pageCount gt 0>${attachedDocument.pageCount} page </#if>${filenameExtension}</b></dd>
                                            <#if attachedDocument.size??>
                                                <dt class="hidden">File size</dt>
                                                <#assign fileSize = (attachedDocument.size/1000)?string["0.0"] + 'kB'/>
                                                <#if (attachedDocument.size/1000) gte 1000>
                                                    <#assign fileSize = (attachedDocument.size/1000000)?string["0.0"] + 'MB'/>
                                                </#if>
                                                <dd>${fileSize}</dd>
                                            </#if>
                                        </dl>
                                    </div>

                                    <div class="document-info__download">
                                        <a href="<@hst.link hippobean=attachedDocument.document/>" class="button  <#if attachedDocument.highlighted>button--primary<#else>button--secondary  button--medium</#if>  button--no-margin">
                                            <span class="link-text">
                                                Download
                                            </span>
                                        </a>
                                    </div>
                                </div>
                            </div>

                        </#list>
                    </section>
                    </#if>

                    <#if document.epilogueContent?has_content>
                    <div id="epilogue">
                        <@hst.html hippohtml=document.epilogueContent/>
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


         --></div>

        </div>
    </div>
</article>

<div class="sticky-document-info  sticky-document-info--animate  sticky-document-info--visible-if-sticky">
    <div class="wrapper">
        <div class="grid"><!--
         --><div class="grid__item medium--four-twelfths large--three-twelfths sticky-document-info__content-data">
                <div class=" sticky-document-info__cell">
                    <dl class="content-data">
                        <dt class="content-data__label">Published:</dt>
                        <dd class="content-data__value"><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMM yyyy"/></dd>
                    </dl>
                </div>
            </div><!--
             --><div class="grid__item medium--eight-twelfths large--nine-twelfths">
                    <div class="sticky-document-info__cell">
                    <div class="sticky-document-info__title">${document.title}</div>
                </div>
            </div><!--
     --></div>
    </div>
</div>
</#if>

<@hst.headContribution category="footerScripts">
<script src="<@hst.webfile path="/assets/scripts/publication.js"/>" type="text/javascript"></script>
</@hst.headContribution>

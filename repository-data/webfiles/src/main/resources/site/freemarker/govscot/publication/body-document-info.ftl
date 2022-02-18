<#if attachedDocument.document??>
<div class="gov_document-info <#if attachedDocument.highlighted || (isLimelitItem)!false>gov_document-info--highlight</#if>">

    <#assign filenameExtension = attachedDocument.document.filename?keep_after_last(".")?upper_case/>
    <#assign filenameWithoutExtension = attachedDocument.document.filename?keep_before_last(".")/>

    <@hst.link var="documentdownload" hippobean=attachedDocument.document>
        <@hst.param name="forceDownload" value="true"/>
    </@hst.link>

    <@hst.link var="documentinline" hippobean=attachedDocument.document>
    </@hst.link>

    <div class="gov_document-info__thumbnail">
        <#if filenameExtension == "PDF">
            <a tabindex="-1"href="${documentinline}">
                <#if ((useCoverPage)!false) && document.coverimage?has_content>
                    <img
                        alt="View this document"
                        src="<@hst.link hippobean=document.coverimage.smallcover/>"
                        srcset="<@hst.link hippobean=document.coverimage.smallcover/> 107w,
                                <@hst.link hippobean=document.coverimage.mediumcover/> 165w,
                                <@hst.link hippobean=document.coverimage.largecover/> 214w,
                                <@hst.link hippobean=document.coverimage.xlargecover/> 330w"
                        sizes="(min-width: 768px) 165px, 107px" />
                <#else>
                    <img
                        alt="View this document"
                        src="<@hst.link hippobean=attachedDocument.thumbnails[0]/>"
                        srcset="
                                <#list attachedDocument.thumbnails as thumbnail>
                                    <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                </#list>"
                        sizes="(min-width: 768px) 165px, 107px" />
                </#if>
            </a>
        <#else>
            <a aria-hidden="true" href="<#if filenameExtension == "CSV">${documentdownload}<#else>${documentinline}</#if>" class="gov_file-icon  gov_file-icon--${filenameExtension!''}">
                <svg class="gov_file-icon__label" viewBox="0 0 210 297">
                    <text x="50%" y="55%" text-anchor="middle" dominant-baseline="middle" font-size="3em">${filenameExtension!''}</text>
                </svg>
                <#include '../common/file-icon-image.ftl'/>
            </a>
        </#if>
    </div>

    <div class="gov_document-info__text">
        <h3 class="gov_document-info__title"><a href="<#if filenameExtension == "CSV">${documentdownload}<#else>${documentinline}</#if>">${attachedDocument.title}</a></h3>

        <div class="gov_document-info__file-details">
            <dl class="ds_metadata  ds_metadata--inline">
                <div class="ds_metadata__item">
                    <dt class="ds_metadata__key  visually-hidden">File type</dt>
                    <dd class="ds_metadata__value"><b><#if attachedDocument.pageCount?has_content && attachedDocument.pageCount gt 0>${attachedDocument.pageCount} page </#if>${filenameExtension}</b></dd>
                </div>

                <#assign fileSize>
                <@formatFileSize document=attachedDocument/>
                </#assign>
                <#if fileSize??>
                    <div class="ds_metadata__item">
                        <dt class="ds_metadata__key  visually-hidden">File size</dt>
                        <dd class="ds_metadata__value">${fileSize}</dd>
                    </div>
                </#if>
            </dl>
        </div>

        <!--noindex-->
        <div class="gov_document-info__download">
            <a data-title="${attachedDocument.title}" href="${documentdownload}" class="ds_button  <#if attachedDocument.highlighted || (isLimelitItem)!false><#else>ds_button--secondary</#if>  ds_no-margin">
                Download
            </a>
        </div>
        <!--endnoindex-->
    </div>
</div>
</#if>

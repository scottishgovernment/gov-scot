<#if attachedDocument.document??>
<div class="document-info <#if attachedDocument.highlighted || (isLimelitItem)!false>document-info--limelight</#if>">

    <#assign filenameExtension = attachedDocument.document.filename?keep_after_last(".")?upper_case/>
    <#assign filenameWithoutExtension = attachedDocument.document.filename?keep_before_last(".")/>

    <@hst.link var="documentdownload" hippobean=attachedDocument.document>
        <@hst.param name="forceDownload" value="true"/>
    </@hst.link>

    <@hst.link var="documentinline" hippobean=attachedDocument.document>
    </@hst.link>

    <div class="document-info__body">
        <div class="document-info__thumbnail  document-info__thumbnail--pdf">
        <#if filenameExtension == "PDF">
            <a class="document-info__thumbnail-link" href="${documentinline}">
                <#if ((useCoverPage)!false) && document.coverimage?has_content>
                    <img
                        alt="View this document"
                        class="document-info__thumbnail-image"
                        src="<@hst.link hippobean=document.coverimage.smallcover/>"
                        srcset="<@hst.link hippobean=document.coverimage.smallcover/> 107w,
                                <@hst.link hippobean=document.coverimage.mediumcover/> 165w,
                                <@hst.link hippobean=document.coverimage.largecover/> 214w,
                                <@hst.link hippobean=document.coverimage.xlargecover/> 330w"
                        sizes="(min-width: 768px) 165px, 107px" />
                <#else>
                    <img
                        alt="View this document"
                        class="document-info__thumbnail-image"
                        src="<@hst.link hippobean=attachedDocument.thumbnails[0]/>"
                        srcset="
                                <#list attachedDocument.thumbnails as thumbnail>
                                    <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                </#list>"
                        sizes="(min-width: 768px) 165px, 107px" />
                </#if>
            </a>
        <#else>
            <a title="View this document" href="${documentinline}" class="file-icon--<#if attachedDocument.highlighted || (isLimelitItem)!false>large<#else>medium</#if>  file-icon  file-icon--${filenameExtension}"></a>
        </#if>
        </div>
    </div>

    <div class="document-info__text">

        <h3 class="document-info__title"><a class="no-icon" href="${documentinline}">${attachedDocument.title}</a></h3>

        <div class="document-info__file-details">
            <dl class="document-info__meta">
                <dt class="hidden">File type</dt>
                <dd><b><#if attachedDocument.pageCount?has_content && attachedDocument.pageCount gt 0>${attachedDocument.pageCount} page </#if>${filenameExtension}</b></dd>
                <dd><@formatFileSize document=attachedDocument/></dd>
            </dl>
        </div>

        <div class="document-info__download">
            <a href="${documentdownload}" class="button  <#if attachedDocument.highlighted || (isLimelitItem)!false>button--primary<#else>button--secondary  button--medium</#if>  button--no-margin">
                <span class="link-text">
                    Download
                </span>
            </a>
        </div>
    </div>
</div>
</#if>

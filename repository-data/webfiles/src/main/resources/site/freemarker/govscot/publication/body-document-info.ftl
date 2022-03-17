<#ftl output_format="HTML">
<#if attachedDocument.document??>
<div class="gov_document-info  <#if attachedDocument.highlighted || (isHighlightedItem)!false>gov_document-info--highlight</#if>  <#if isTargetedItem!false>gov_document-info--targeted</#if>">

    <#assign filenameExtension = attachedDocument.document.filename?keep_after_last(".")?upper_case/>
    <#assign filenameWithoutExtension = attachedDocument.document.filename?keep_before_last(".")/>

    <@hst.link var="documentdownload" hippobean=attachedDocument.document>
        <@hst.param name="forceDownload" value="true"/>
    </@hst.link>

    <@hst.link var="documentinline" hippobean=attachedDocument.document>
    </@hst.link>

    <div class="gov_document-info__icon">
        <#if filenameExtension == "PDF">
            <a data-title="${attachedDocument.title}" class="gov_document-info__thumbnail-link" href="${documentinline}">
                <#if ((useCoverPage)!false) && document.coverimage?has_content>
                    <img
                        alt="View this document"
                        class="gov_document-info__thumbnail-image"
                        src="<@hst.link hippobean=document.coverimage.smallcover/>"
                        srcset="<@hst.link hippobean=document.coverimage.smallcover/> 107w,
                                <@hst.link hippobean=document.coverimage.mediumcover/> 165w,
                                <@hst.link hippobean=document.coverimage.largecover/> 214w,
                                <@hst.link hippobean=document.coverimage.xlargecover/> 330w"
                        sizes="(min-width: 768px) 165px, 107px" />
                <#else>
                    <img
                        alt="View this document"
                        class="gov_document-info__thumbnail-image"
                        src="<@hst.link hippobean=attachedDocument.thumbnails[0]/>"
                        srcset="
                                <#list attachedDocument.thumbnails as thumbnail>
                                    <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                </#list>"
                        sizes="(min-width: 768px) 165px, 107px" />
                </#if>
            </a>
        <#else>
            <a data-title="${attachedDocument.title}" title="View this document" href="<#if filenameExtension == "CSV">${documentdownload}<#else>${documentinline}</#if>" class="gov_document-info__thumbnail-link  file-icon--<#if attachedDocument.highlighted || (isHighlightedItem)!false>large<#else>medium</#if>  file-icon  file-icon--${filenameExtension}"></a>
        </#if>
    </div>

    <div class="gov_document-info__info">
        <h3 class="gov_document-info__title"><a class="no-icon" href="<#if filenameExtension == "CSV">${documentdownload}<#else>${documentinline}</#if>">${attachedDocument.title}</a></h3>

        <div class="gov_document-info__file-details">
            <dl>
                <dt class="hidden">File type</dt>
                <dd><b><#if attachedDocument.pageCount?has_content && attachedDocument.pageCount gt 0>${attachedDocument.pageCount} page </#if>${filenameExtension}</b></dd>
                <dd><@formatFileSize document=attachedDocument/></dd>
            </dl>
        </div>
    </div>

    <!--noindex-->
    <div class="gov_document-info__download">
        <a data-title="${attachedDocument.title}" href="${documentdownload}" class="button  <#if attachedDocument.highlighted>button--primary<#elseif isHighlightedItem!false>button--primary<#elseif isTargetedItem!false>button--primary<#else>button--secondary  button--medium</#if>  button--no-margin">
            <span class="link-text">
                Download
            </span>
        </a>
    </div>
    <!--endnoindex-->
</div>
</#if>

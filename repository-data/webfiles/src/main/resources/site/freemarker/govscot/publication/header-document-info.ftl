<div class="document-info  document-info--old-style">

    <div class="document-info__body">
        <div class="document-info__thumbnail">

            <#assign mainDocument = documents[0]/>
            <#assign filenameExtension = mainDocument.document.filename?keep_after_last(".")?upper_case/>

            <@hst.link var="documentdownload" hippobean=mainDocument.document>
                <@hst.param name="forceDownload" value="true"/>
            </@hst.link>

            <@hst.link var="documentinline" hippobean=mainDocument.document>
            </@hst.link>

            <#if filenameExtension == "PDF">
            <a data-title="${mainDocument.title}" title="View this document" class="document-info__thumbnail-link" href="${documentinline}">
                <img
                        alt="View this document"
                        class="document-info__thumbnail-image"
                        src="<@hst.link hippobean=mainDocument.thumbnails[0]/>"
                        srcset="
                            <#list mainDocument.thumbnails as thumbnail>
                                <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                            </#list>"
                        sizes="(min-width: 768px) 165px, 107px" />
            </a>
            <#else>
                <a data-title="${mainDocument.title}" title="View this document" href="<#if filenameExtension == "CSV">${documentdownload}<#else>${documentinline}</#if>" class="file-icon--large file-icon file-icon--${filenameExtension}"></a>
            </#if>

            <a data-title="${mainDocument.title}" href="${documentdownload}" class="button button--secondary document-info__thumbnail-button no-icon button--no-margin">
                <span class="link-text">
                    Download
                </span>
            </a>
        </div>
    </div>

    <div class="document-info__text">

        <div class="document-info__file-details">
            <#if mainDocument.pageCount?has_content && mainDocument.pageCount gt 0>
                <p class="document-info__page-count">${mainDocument.pageCount} page ${filenameExtension}</p>
            </#if>
            <p class="document-info__file-size"><@formatFileSize document=mainDocument/></p>
        </div>
    </div>

    <div class="document-info__footer">
        <div class="document-info__cell document-info__download-wrapper">
            <a data-title="${mainDocument.title}" href="${documentdownload}" class="button button--primary button--large no-icon  button--no-margin">
                <span class="link-text">
                    Download
                </span>
            </a>
        </div>

        <#if mainDocument.accessible != true>
            <div class="document-info__disclaimer document-info__cell">
                <div><span class="document-info__inline-title">Accessibility:</span> This document may not be fully accessible. <#if requestAlternative??><a href="/contact/">Request this document in an alternative format</a>.</#if></div>
            </div>
        </#if>
    </div>
</div>

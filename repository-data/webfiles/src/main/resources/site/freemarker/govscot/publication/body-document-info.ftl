<#ftl output_format="HTML">
<#if attachedDocument.document??>
    <#assign filenameExtension = attachedDocument.document.filename?keep_after_last(".")?lower_case/>
    <#assign filenameWithoutExtension = attachedDocument.document.filename?keep_before_last(".")/>

    <@hst.link var="documentinline" hippobean=attachedDocument.document>
    </@hst.link>

    <#if docindex??>
    <#else>
        <#assign docindex = 0 />
    </#if>

    <#switch filenameExtension>
        <#case "csv">
            <#assign fileDescription = "CSV file" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/csv.svg' />
            <#break>
        <#case "xls">
        <#case "xlsx">
        <#case "xlsm">
            <#assign fileDescription = "Excel document" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/excel.svg' />
            <#break>
        <#case "kml">
        <#case "kmz">
            <#assign fileDescription = "${filenameExtension} data" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/geodata.svg' />
            <#break>
        <#case "gif">
        <#case "jpg">
        <#case "jpeg">
        <#case "png">
        <#case "svg">
            <#assign fileDescription = "Image" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/image.svg' />
            <#break>
        <#case "pdf">
            <#assign fileDescription = "PDF" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/pdf.svg' />
            <#break>
        <#case "ppt">
        <#case "pptx">
        <#case "pps">
        <#case "ppsx">
            <#assign fileDescription = "Powerpoint document" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/ppt.svg' />
            <#break>
        <#case "rtf">
            <#assign fileDescription = "Rich text file" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/rtf.svg' />
            <#break>
        <#case "txt">
            <#assign fileDescription = "Text file" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/txt.svg' />
            <#break>
        <#case "doc">
        <#case "docx">
            <#assign fileDescription = "Word document" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/word.svg' />
            <#break>
        <#case "xml">
        <#case "xsd">
            <#assign fileDescription = "${filenameExtension} file" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/xml.svg' />
            <#break>
        <#default>
            <#assign fileDescription = "${filenameExtension} file" />
            <#assign fileThumbnailPath = '/assets/images/documents/svg/generic.svg' />
    </#switch>

    <div class="ds_file-download  <#if attachedDocument.highlighted || (isLimelitItem)!false>ds_file-download--highlighted</#if>">
        <!--noindex-->
        <div class="ds_file-download__thumbnail">
            <#if filenameExtension == "pdf" && attachedDocument.thumbnails[0]??>
                <a data-button="document-cover" class="ds_file-download__thumbnail-link" aria-hidden="true" tabindex="-1" href="${documentinline}">
                    <img
                        class="ds_file-download__thumbnail-image"
                        alt="View this document"
                        src="<@hst.link hippobean=attachedDocument.thumbnails[0]/>"
                        srcset="
                                <#list attachedDocument.thumbnails as thumbnail>
                                    <@hst.link hippobean=thumbnail/> ${thumbnail.filename?keep_before_last(".")?keep_after_last("_")}w<#sep>, </#sep>
                                </#list>"
                        sizes="(min-width: 768px) 104px, 72px" />
                </a>
            <#else>
                <a class="ds_file-download__thumbnail-link" aria-hidden="true" href="${documentinline}" tabindex="-1">
                    <img width="104" height="152" loading="lazy" class="ds_file-download__thumbnail-image" src="<@hst.link path=fileThumbnailPath />" alt=""/>
                </a>
            </#if>
        </div>
        <!--endnoindex-->

        <div class="ds_file-download__content">
            <a href="${documentinline}" class="ds_file-download__title" aria-describedby="file-download-${docindex}">${attachedDocument.title}</a>

            <div id="file-download-${docindex}" class="ds_file-download__details">
                <dl class="ds_metadata  ds_metadata--inline">
                    <div class="ds_metadata__item">
                        <dt class="ds_metadata__key  visually-hidden">File type</dt>
                        <dd class="ds_metadata__value"><#if attachedDocument.pageCount?has_content && attachedDocument.pageCount gt 0>${attachedDocument.pageCount} page </#if>${fileDescription}</b></dd>
                    </div>

                    <#assign fileSize>
                        <@formatFileSize document=attachedDocument/>
                    </#assign>
                    <#if fileSize?has_content>
                        <div class="ds_metadata__item">
                            <dt class="ds_metadata__key  visually-hidden">File size</dt>
                            <dd class="ds_metadata__value">${fileSize}</dd>
                        </div>
                    </#if>
                </dl>
            </div>
        </div>
    </div>
</#if>

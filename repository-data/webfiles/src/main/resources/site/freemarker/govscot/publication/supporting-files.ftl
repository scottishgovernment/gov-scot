<#if documents?size gt 1>
<div class="supporting-files">
    <h3 class="supporting-files__title">Supporting files</h3>

    <ul class="supporting-files__list">

        <#list documents as attachedDocument>
            <#assign supportingFilenameExtension = attachedDocument.document.filename?keep_after_last(".")?upper_case/>

            <#if attachedDocument?index gt 0>
                <li class="supporting-files__item">

                    <#if attachedDocument.document.filename??>

                        <@hst.link var="documentdownload" hippobean=attachedDocument.document>
                            <@hst.param name="forceDownload" value="true"/>
                        </@hst.link>

                        <div class="supporting-file">
                            <a href="${documentdownload}" class="supporting-file__icon file-icon file-icon--${supportingFilenameExtension}"></a>

                            <span class="supporting-file__link-wrap">
                                <a class="supporting-file__link" href="${documentdownload}">
                                    ${attachedDocument.title} (<b><abbr>${supportingFilenameExtension}</abbr> <@formatFileSize document=attachedDocument/></b>)
                                </a>
                            </span>
                        </div>
                    </#if>
                </li>
            </#if>
        </#list>
    </ul>
</div>
</#if>
<#if documents?size gt 1>
<div class="supporting-files">
    <h3 class="supporting-files__title">Supporting files</h3>

    <ul class="supporting-files__list">

        <#list documents as attachedDocument>
            <#assign supportingFilenameExtension = attachedDocument.document.filename?keep_after_last(".")?upper_case/>

            <@hst.link var="documentdownload" hippobean=attachedDocument.document>
                <@hst.param name="forceDownload" value="true"/>
            </@hst.link>

            <#if attachedDocument?index gt 0>
                <li class="supporting-files__item">

                    <#if attachedDocument.document.filename??>
                        <div class="supporting-file">
                            <#if supportingFilenameExtension == 'CSV'>
                                <a href="${documentdownload}" class="supporting-file__icon gov_file-icon file-icon--${supportingFilenameExtension}"></a>
                            <#else>
                                <a href="<@hst.link hippobean=attachedDocument.document/>?inline-true" class="supporting-file__icon gov_file-icon gov_file-icon--${supportingFilenameExtension}"></a>
                            </#if>

                            <span class="supporting-file__link-wrap">
                                <a class="supporting-file__link" href="<@hst.link hippobean=attachedDocument.document/>">
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

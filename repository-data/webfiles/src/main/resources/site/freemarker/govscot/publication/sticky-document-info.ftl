<div class="sticky-document-info  sticky-document-info--animate  <#if !isMultiPagePublication>sticky-document-info--visible-if-sticky</#if>">
    <div class="wrapper">
        <div class="grid"><!--
            <#if isMultiPagePublication>
                --><div class="grid__item  eight-twelfths  medium--five-twelfths large--seven-twelfths">
                    <div class="sticky-document-info__cell  sticky-document-info--visible-if-sticky">
                        <div class="sticky-document-info__title">
                            ${document.title}
                        </div>
                        <div class="sticky-document-info__subtitle">
                            ${currentPage.title}
                        </div>
                    </div>
                </div><!--
                <#if hasSupportingDocs?? && hasSupportingDocs>
                    --><div class="grid__item  hidden-xsmall  push--medium--four-twelfths  push--large--three-twelfths  medium--three-twelfths xlarge--two-twelfths  sticky-document-info__panel-container">
                        <div class="sticky-document-info__cell  sticky-document-info--visible-if-sticky">
                            <a class="small" href="${baseurl + 'documents/'}">Supporting documents</a>
                        </div>
                    </div><!--
                </#if>
                --><div class="grid__item  four-twelfths hidden-medium hidden-large hidden-xlarge">
                    <div class="sticky-document-info__cell sticky-document-info__cell--text-right">
                        <button class="button button--primary  button--xsmall js-mobile-toc-trigger-open" title="Show table of contents">Contents</button>
                        <button class="button button--clear  button--xsmall  button--cancel js-mobile-toc-trigger-close" title="Close table of contents">Close</button>
                    </div>
                </div><!--
            <#else>
             --><div class="grid__item medium--four-twelfths large--three-twelfths sticky-document-info__content-data">
                    <#if document.publicationDate??>
                        <div class=" sticky-document-info__cell">
                            <dl class="content-data">
                                <dt class="content-data__label">Published:</dt>
                                <dd class="content-data__value"><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMM yyyy"/></dd>
                            </dl>
                        </div>
                    </#if>
                </div><!--
             --><div class="grid__item medium--eight-twelfths large--nine-twelfths">
                    <div class="sticky-document-info__cell">
                        <div class="sticky-document-info__title">${document.title}</div>
                    </div>
                </div><!--
            </#if>
     --></div>
    </div>
</div>

<div class="sticky-document-info  sticky-document-info--animate  <#if !isMultiPagePublication>sticky-document-info--visible-if-sticky</#if>">
    <div class="wrapper">
        <div class="grid"><!--
            <#if isMultiPagePublication>
              --><div class="grid__item medium--four-twelfths  large--three-twelfths hidden-xsmall">
                    <div class="sticky-document-info__cell">
                        <div class="sticky-document-info__label">Contents</div>
                    </div>
                </div><!--
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
             --><div class="grid__item  hidden-xsmall  medium--three-twelfths large--two-twelfths  sticky-document-info__panel-container">
                    <#if documents??>
                        <button class="sticky-document-info__trigger">Supporting files</button>

                        <div class="sticky-document-info__panel ">

                            <div class="primary-doc">
                                <#if documents?size gt 0>
                                    <#include 'header-document-info.ftl'/>
                                </#if>
                            </div>
                            <div>
                                <#include 'supporting-files.ftl'/>
                            </div>
                        </div>
                    </#if>
                </div><!--
             --><div class="grid__item  four-twelfths hidden-medium hidden-large hidden-xlarge">
                    <div class="sticky-document-info__cell sticky-document-info__cell--text-right">
                        <button class="button button--primary  button--xsmall js-mobile-toc-trigger-open" title="Show table of contents">Contents</button>
                        <button class="button button--clear  button--xsmall  button--cancel js-mobile-toc-trigger-close" title="Close table of contents">Close</button>
                    </div>
                </div><!--
            <#else>
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
            </#if>
     --></div>
    </div>
</div>
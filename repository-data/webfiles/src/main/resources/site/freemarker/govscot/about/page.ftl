<#include "../../include/imports.ftl">







<script>
$(function () {
    let navTopOffset = 52;

    $('.document-nav').css({top: navTopOffset});
});
</script>



<header class="article-header has-icon has-icon--guide">
                <p class="article-header__label">Publication - Advice and guidance</p>
                <h1 class="article-header__title">Building standards technical handbook 2017: domestic</h1>
            </header>

            <p class="leader">
                Guidance on how to comply with Scottish building regulations.
            </p>



<nav class="document-nav">
    <a href="previous" title="Previous page">Previous</a>
    <a href="menu" title="Menu">Menu</a>
    <a href="next" title="Next page">Next</a>
</nav>


            <h2>Contents</h2>

            <div class="expandable  contents-expandable">
                <div class="expandable-item  expandable-item--open">
                    <a class="expandable-item__header  js-toggle-expand" role="tab" id="heading1" href="#panel1" data-toggle="collapse" aria-expanded="false" aria-controls="panel1">
                        <h3 class="expandable-item__title  gamma"><span class="link-text">0.0 General</span></h3>
                        <span class="expandable-item__icon"></span>
                    </a>
    
                    <div style="display: block;" id="panel1" class="expandable-item__body" role="tabpanel" aria-expanded="false" aria-labelledby="heading1">
                        <ul class="contents-list">
                            <li class="contents-list__item">
                                <a class="contents-list__link" href="#">Page 1</a>
                            </li>
                            <li class="contents-list__item">
                                <a class="contents-list__link" href="#">A really freaking long page title that will almost surely wrap on to multiple lines.</a>
                            </li>
                            <li class="contents-list__item">
                                <a class="contents-list__link" href="#">Page 3</a>
                            </li>

                        </ul>
                    </div>
                </div>
                <!-- /end .expandable-item -->

                <div class="expandable-item">
                    <a class="expandable-item__header  js-toggle-expand" role="tab" id="heading2" href="#panel2" data-toggle="collapse" aria-expanded="false" aria-controls="panel1">
                        <h3 class="expandable-item__title  gamma"><span class="link-text">1.0 Structure</span></h3>
                        <span class="expandable-item__icon"></span>
                    </a>
    
                    <div id="panel2" class="expandable-item__body" role="tabpanel" aria-expanded="false" aria-labelledby="heading2">
                        <ul>
                            <li>Page 1</li>
                            <li>Page 2</li>
                            <li>etc</li>
                        </ul>
                    </div>
                </div>
                <!-- /end .expandable-item -->

                <div class="expandable-item">
                    <a class="expandable-item__header  js-toggle-expand" role="tab" id="heading3" href="#panel3" data-toggle="collapse" aria-expanded="false" aria-controls="panel1">
                        <h3 class="expandable-item__title  gamma"><span class="link-text">2.0 Fire</span></h3>
                        <span class="expandable-item__icon"></span>
                    </a>
    
                    <div id="panel3" class="expandable-item__body" role="tabpanel" aria-expanded="false" aria-labelledby="heading3">
                        <ul>
                            <li>Page 1</li>
                            <li>Page 2</li>
                            <li>etc</li>
                        </ul>
                    </div>
                </div>
                <!-- /end .expandable-item -->
            </div>


            <div class="expandable">
                <div class="expandable-item">
                    <a class="expandable-item__header  js-toggle-expand" role="tab" id="headingA" href="#panelA" data-toggle="collapse" aria-expanded="false" aria-controls="panel1">
                        <h3 class="expandable-item__title  gamma"><span class="link-text">Appendix A</span></h3>
                        <span class="expandable-item__icon"></span>
                    </a>
    
                    <div id="panelA" class="expandable-item__body" role="tabpanel" aria-expanded="false" aria-labelledby="headingA">
                        <ul>
                            <li>Page 1</li>
                            <li>Page 2</li>
                            <li>etc</li>
                        </ul>
                    </div>
                </div>
                <!-- /end .expandable-item -->
            </div>
<hr/><hr/><hr /><hr/><hr/><hr /><hr/><hr/><hr /><hr/><hr/><hr /><hr/><hr/><hr /><hr/><hr/><hr /><hr/><hr/><hr />
















<#if document??>
    <article id="page-content" class="layout--about">
    <@hst.manageContent hippobean=document/>

        <div class="grid"><!--
         --><div class="grid__item medium--nine-twelfths large--seven-twelfths push--medium--three-twelfths">
                <@hst.include ref="content"/>
            </div><!--

         --><div class="grid__item medium--three-twelfths pull--medium--nine-twelfths pull--large--seven-twelfths">
                <@hst.include ref="side-menu"/>
            </div><!--
     --></div>
    </article>

    <div class="grid"><!--
        --><div class="grid__item push--medium--three-twelfths medium--nine-twelfths large--seven-twelfths">
            <#include '../common/feedback-wrapper.ftl'>
        </div><!--
    --></div>

<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

<#if document??>
    <@hst.headContribution category="pageTitle"><title>${document.title} - gov.scot</title></@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "../common/canonical.ftl" />
</#if>

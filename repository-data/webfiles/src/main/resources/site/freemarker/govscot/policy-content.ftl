<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content">
    <@hst.cmseditlink hippobean=document/>
        <header class="article-header">
            <p class="article-header__label">Policy</p>
            <h1 class="article-header__title">${document.title?html}</h1>
        
            <section class="content-data">
            <#-- These sections to be pulled out into content-data component -->
                <div class="content-data__expandable">
                    <button class="expand  expand--mobile-only  content-data__toggle" data-target-selector="#expandable-content-data" title="Show details">
                        <span class="hit-target">
                            <span class="expand__icon"></span>
                        </span>
                    </button>
                    <dl class="content-data__list" id="expandable-content-data">
                        <#if document.responsibleRole??>
                            <dt class="content-data__label">From:</dt>

                            <dd class="content-data__value">
                                    <@hst.link var="link" hippobean=document.responsibleRole/>
                                    <a href="${link}">${document.responsibleRole.title}</a><!--
                             --><#if document.secondaryResponsibleRole?first??><!--
                                 -->, <!--
                                 --><a href="#secondary-responsible-roles" class="content-data__expand js-display-toggle">
                                 &#43;${document.secondaryResponsibleRole?size}&nbsp;more&nbsp;&hellip;</a>

                                    <#list document.secondaryResponsibleRole as secondaryRole>
                                        <span id="secondary-responsible-roles" class="content-data__additional">
                                            <@hst.link var="link" hippobean=secondaryRole/>
                                            <a href="${link}">${secondaryRole.title}</a><#sep>, </#sep>
                                        </span>
                                    </#list>
                                </#if>
                            </dd>
                        </#if>
                        <#if document.responsibleDirectorate??>
                            <dt class="content-data__label">Directorate:</dt>

                            <dd class="content-data__value">
                                    <@hst.link var="link" hippobean=document.responsibleDirectorate/>
                                    <a href="${link}">${document.responsibleDirectorate.title}</a><!--
                             --><#if document.secondaryResponsibleDirectorate?first??><!--
                                 -->, <!--
                                 --><a href="#secondary-responsible-directorates" class="content-data__expand js-display-toggle">
                                 &#43;${document.secondaryResponsibleDirectorate?size}&nbsp;more&nbsp;&hellip;</a>

                                    <#list document.secondaryResponsibleDirectorate as secondaryDirectorate>
                                        <span id="secondary-responsible-directorates" class="content-data__additional">
                                            <@hst.link var="link" hippobean=secondaryDirectorate/>
                                            <a href="${link}">${secondaryDirectorate.title}</a><#sep>, </#sep>
                                        </span>
                                    </#list>
                                </#if>
                            </dd>
                        </#if>
                        <#if document.topics?first??>
                            <dt class="content-data__label">Part of:</dt>

                            <dd class="content-data__value">
                                <#list document.topics as topic>
                                    <@hst.link var="link" hippobean=topic/>
                                    <a href="${link}">${topic.title}</a><#sep>, </sep>
                                </#list>
                            </dd>
                        </#if>
                    </dl>
                </div>
            </section>
        </header>

        <div class="grid">
            <div class="grid__item medium--four-twelfths large--three-twelfths">
                <@hst.include ref="menu"/>
                <div class="page-group__policy-action-count visible-xsmall">
                    <span class="page-group__policy-action-count__label">Overview</span>
                </div>
            </div>

            <div class="grid__item medium--nine-twelfths large--seven-twelfths">
                <div class="body-content">
                    <div class="page-group__content body-content inner-shadow-top inner-shadow-top--no-desktop">
                        ${document.content.content}

                        <h3>Actions</h3>
                        ${document.actions}

                        <h3>Background</h3>
                        ${document.background}

                        <#if document.billsAndLegislation != ''>
                            <h3>Bills and legislation</h3>
                            ${document.billsAndLegislation}
                        </#if>

                        <h3>Contact</h3>
                        ${document.contact}
                    </div>

                    <nav class="multipage-nav visible-xsmall">
                        <div class="grid"><!--
                         --><div class="grid__item small--six-twelfths push--small--six-twelfths">
                                <#-- {{#relativePolicyPage . 1}} -->
                                    <div class="multipage-nav__container">
                                        <a class="multipage-nav__link multipage-nav__link--next" href="{{url}}">
                                        Title <span class="multipage-nav__icon multipage-nav__icon--right fa fa-chevron-right fa-2x"></span></a>
                                    </div>
                                <#-- {{/relativePolicyPage}} -->
                            </div><!--

                         --><div class="grid__item small--six-twelfths pull--small--six-twelfths">
                                <#-- {{#relativePolicyPage . -1}} -->
                                    <div class="multipage-nav__container">
                                        <a class="multipage-nav__link multipage-nav__link--previous" href="{{url}}"><span class="fa fa-2x fa-chevron-left multipage-nav__icon multipage-nav__icon--left"></span>
                                        Title </a>
                                    </div>
                                <#-- {{/relativePolicyPage}} -->
                            </div><!--
                     --></div>

                    </nav>
                </div>
            </div>
    </article>

<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
  <div>
    <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
  </div>
</#if>

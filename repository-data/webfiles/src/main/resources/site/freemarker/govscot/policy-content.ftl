<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content">
    <@hst.cmseditlink hippobean=document/>
        <header class="article-header">
            <p class="article-header__label">Policy</p>
            <h1 class="article-header__title">${policy.title?html}</h1>
        
            <section class="content-data">
            <#-- These sections to be pulled out into content-data component and passed in the 'policy' variable -->
                <div class="content-data__expandable">
                    <button class="expand  expand--mobile-only  content-data__toggle" data-target-selector="#expandable-content-data" title="Show details">
                        <span class="hit-target">
                            <span class="expand__icon"></span>
                        </span>
                    </button>
                    <dl class="content-data__list" id="expandable-content-data">
                        <#if policy.responsibleRole??>
                            <dt class="content-data__label">From:</dt>

                            <dd class="content-data__value">
                                    <@hst.link var="link" hippobean=policy.responsibleRole/>
                                    <a href="${link}">${policy.responsibleRole.title}</a><!--
                             --><#if policy.secondaryResponsibleRole?first??><!--
                                 -->, <!--
                                 --><a href="#secondary-responsible-roles" class="content-data__expand js-display-toggle">
                                 &#43;${policy.secondaryResponsibleRole?size}&nbsp;more&nbsp;&hellip;</a>

                                    <#list policy.secondaryResponsibleRole as secondaryRole>
                                        <span id="secondary-responsible-roles" class="content-data__additional">
                                            <@hst.link var="link" hippobean=secondaryRole/>
                                            <a href="${link}">${secondaryRole.title}</a><#sep>, </#sep>
                                        </span>
                                    </#list>
                                </#if>
                            </dd>
                        </#if>
                        <#if policy.responsibleDirectorate??>
                            <dt class="content-data__label">Directorate:</dt>

                            <dd class="content-data__value">
                                    <@hst.link var="link" hippobean=policy.responsibleDirectorate/>
                                    <a href="${link}">${policy.responsibleDirectorate.title}</a><!--
                             --><#if policy.secondaryResponsibleDirectorate?first??><!--
                                 -->, <!--
                                 --><a href="#secondary-responsible-directorates" class="content-data__expand js-display-toggle">
                                 &#43;${policy.secondaryResponsibleDirectorate?size}&nbsp;more&nbsp;&hellip;</a>

                                    <#list policy.secondaryResponsibleDirectorate as secondaryDirectorate>
                                        <span id="secondary-responsible-directorates" class="content-data__additional">
                                            <@hst.link var="link" hippobean=secondaryDirectorate/>
                                            <a href="${link}">${secondaryDirectorate.title}</a><#sep>, </#sep>
                                        </span>
                                    </#list>
                                </#if>
                            </dd>
                        </#if>
                        <#if policy.topics?first??>
                            <dt class="content-data__label">Part of:</dt>

                            <dd class="content-data__value">
                                <#list policy.topics as topic>
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
                    <#if document == policy>
                        <span class="page-group__policy-action-count__label">Overview</span>
                    <#else>
                        <span class="page-group__policy-action-count__label">Policy actions</span>
                        &nbsp;${policyDetails?seq_index_of(document) + 1} of ${policyDetails?size}
                    </#if>
                </div>
            </div>

            <div class="grid__item medium--nine-twelfths large--seven-twelfths">
                <div class="body-content">
                    <div class="page-group__content body-content inner-shadow-top inner-shadow-top--no-desktop">
                        <#if document != policy>
                            <h2>${document.title}</h2>
                        </#if>

                        ${document.content.content}

                        <#if document.actions != ''>
                            <h3>Actions</h3>
                            ${document.actions}
                        </#if>

                        <#if document.background != ''>
                            <h3>Background</h3>
                            ${document.background}
                        </#if>

                        <#if document.billsAndLegislation != ''>
                            <h3>Bills and legislation</h3>
                            ${document.billsAndLegislation}
                        </#if>

                        <#if document.contact != ''>
                            <h3>Contact</h3>
                            ${document.contact}
                        </#if>
                    </div>

                    <nav class="multipage-nav visible-xsmall">
                        <div class="grid"><!--
                         --><div class="grid__item small--six-twelfths push--small--six-twelfths">
                                <#if next??>
                                    <div class="multipage-nav__container">
                                        <@hst.link var="link" hippobean=next/>
                                        <a class="multipage-nav__link multipage-nav__link--next" href="${link}">
                                        ${next.title} <span class="multipage-nav__icon multipage-nav__icon--right fa fa-chevron-right fa-2x"></span></a>
                                    </div>
                                </#if>
                            </div><!--

                         --><div class="grid__item small--six-twelfths pull--small--six-twelfths">
                                <#if prev??>
                                    <div class="multipage-nav__container">
                                        <@hst.link var="link" hippobean=prev/>
                                        <a class="multipage-nav__link multipage-nav__link--previous" href="${link}"><span class="fa fa-2x fa-chevron-left multipage-nav__icon multipage-nav__icon--left"></span>
                                        ${prev.title} </a>
                                    </div>
                                </#if>
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

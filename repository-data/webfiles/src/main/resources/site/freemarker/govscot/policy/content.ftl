<#include "../../include/imports.ftl">

<div class="body-content">
    <div class="page-group__content body-content inner-shadow-top inner-shadow-top--no-desktop">
    <#if document != index>
        <h2>${document.title}</h2>
    </#if>

    <@hst.html hippohtml=document.content/>

    <@hst.html hippohtml=document.actions var="actions"/>
    <#if actions?has_content>
        <h3>Actions</h3>
        ${actions}
    </#if>

    <@hst.html hippohtml=document.background var="background"/>
    <#if background?has_content>
        <h3>Background</h3>
        ${background}
    </#if>

    <@hst.html hippohtml=document.billsAndLegislation var="billsAndLegislation"/>
    <#if billsAndLegislation?has_content>
        <h3>Bills and legislation</h3>
        ${billsAndLegislation}
    </#if>

    <@hst.html hippohtml=document.contact var="contact"/>
    <#if contact?has_content>
        <h3>Contact</h3>
        ${contact}
    </#if>
    </div>

    <nav class="multipage-nav visible-xsmall">
        <div class="grid"><!--
               -->
            <div class="grid__item small--six-twelfths push--small--six-twelfths">
            <#if next??>
                <div class="multipage-nav__container">
                    <@hst.link var="link" hippobean=next/>
                    <a class="multipage-nav__link multipage-nav__link--next" href="${link}">
                    ${next.title} <span
                            class="multipage-nav__icon multipage-nav__icon--right fa fa-chevron-right fa-2x"></span></a>
                </div>
            </#if>
            </div><!--

                         -->
            <div class="grid__item small--six-twelfths pull--small--six-twelfths">
            <#if prev??>
                <div class="multipage-nav__container">
                    <@hst.link var="link" hippobean=prev/>
                    <a class="multipage-nav__link multipage-nav__link--previous" href="${link}"><span
                            class="fa fa-2x fa-chevron-left multipage-nav__icon multipage-nav__icon--left"></span>
                    ${prev.title} </a>
                </div>
            </#if>
            </div><!--
     --></div>
    </nav>
</div>
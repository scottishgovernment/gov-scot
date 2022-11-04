<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<#include "../common/macros/lang-attributes.ftl">

<article <@lang document /> class="body-content">
    <div class="body-content">
    <#if document != index>
        <h2>${document.title}</h2>
    </#if>

    <@hst.html hippohtml=document.content/>

    <@hst.html hippohtml=document.actions var="actions"/>
    <#if actions?has_content>
        <h3 <@revertlang document />>Actions</h3>
        ${actions?no_esc}
    </#if>

    <@hst.html hippohtml=document.background var="background"/>
    <#if background?has_content>
        <h3 <@revertlang document />>Background</h3>
        ${background?no_esc}
    </#if>

    <@hst.html hippohtml=document.billsAndLegislation var="billsAndLegislation"/>
    <#if billsAndLegislation?has_content>
        <h3 <@revertlang document />>Bills and legislation</h3>
        ${billsAndLegislation?no_esc}
    </#if>

    <@hst.html hippohtml=document.contact var="contact"/>
    <#if contact?has_content>
        <h3 <@revertlang document />>Contact</h3>
        ${contact?no_esc}
    </#if>
    </div>


    <!--noindex-->
    <nav class="ds_sequential-nav" aria-label="Article navigation">
        <#if prev??>
            <@hst.link var="link" hippobean=prev/>
            <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
                <a <@langcompare prev document /> title="Previous section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                    <span class="ds_sequential-nav__text" data-label="previous">
                        ${prev.title}
                    </span>
                </a>
            </div>
        </#if>

        <#if next??>
            <@hst.link var="link" hippobean=next/>
            <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
                <a <@langcompare next document /> title="Next section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                    <span class="ds_sequential-nav__text" data-label="next">
                        ${next.title}
                    </span>
                </a>
            </div>
        </#if>
    </nav>
    <!--endnoindex-->
</article>

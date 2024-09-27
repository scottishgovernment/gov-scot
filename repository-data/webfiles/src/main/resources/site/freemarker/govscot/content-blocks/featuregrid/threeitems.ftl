<#ftl output_format="HTML">
<#include "../../../include/imports.ftl">
<#include "../../../include/cms-placeholders.ftl">

<#assign items = []>
<#if document1??>
    <#assign items = items + [document1]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>
<#if document2??>
    <#assign items = items + [document2]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>
<#if document3??>
    <#assign items = items + [document3]>
<#elseif editMode>
    <#assign items = items + ['']>
</#if>

<#if items?size != 0>
<div class="ds_cb  ds_cb--feature-grid  ds_cb--feature-grid-3-items
<#if fullwidth>  ds_cb--fullwidth</#if>
<#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
<#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
<#if neutrallinks>  ds_cb--neutral-links</#if>
<#if removebottompadding>  ds_!_padding-bottom--0</#if>
">
    <div class="ds_wrapper">
        <div class="ds_cb__inner">

        <#list items as item>

        <#if item != ''>
        <!-- set link where internal link has priority over external link -->
        <#assign link>
        <#if item.link??>
            <@hst.link hippobean=item.link/>
        <#elseif item.externalLink?has_content>
            ${item.externalLink}
        </#if>
        </#assign>

                <div class="ds_cb--feature-grid__item">
                    <#if showimages>
                        <div class="ds_cb--feature-grid__item-media  <#if smallvariant>ds_cb--feature-grid__item-media--small-mobile</#if>">
                            <div class="ds_aspect-box">
                            <#if item.image??>
                            <#if link?has_content>
                                <a href="${link}" tabindex="-1">
                            </#if>
                                <#if item.image.xlargefourcolumns??>
                                    <img class="ds_aspect-box__inner" alt="${item.alt}" src="<@hst.link hippobean=item.image.xlargefourcolumns />"
                                            width="${item.image.xlargefourcolumns.width?c}"
                                            height="${item.image.xlargefourcolumns.height?c}"
                                            loading="lazy"
                                            srcset="
                                            <@hst.link hippobean=item.image.smallcolumns/> 360w,
                                            <@hst.link hippobean=item.image.smallcolumnsdoubled/> 720w,
                                            <@hst.link hippobean=item.image.mediumfourcolumns/> 224w,
                                            <@hst.link hippobean=item.image.mediumfourcolumnsdoubled/> 448w,
                                            <@hst.link hippobean=item.image.largefourcolumns/> 288w,
                                            <@hst.link hippobean=item.image.largefourcolumnsdoubled/> 576w,
                                            <@hst.link hippobean=item.image.xlargefourcolumns/> 352w,
                                            <@hst.link hippobean=item.image.xlargefourcolumnsdoubled/> 704w"
                                            sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, <#if smallvariant>360px<#else>100vw</#if>"
                                            >
                                <#else>
                                    <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=item.image />" alt="${item.alt}"/>
                                </#if>
                            <#if link?has_content>
                                </a>
                            </#if>
                            </#if>
                            </div>
                        </div>
                    </#if>
                    <#if item.title??>
                        <${weight} class="ds_cb--feature-grid__item-title">
                            <#if link?has_content>
                                <a href="${link}">${item.title}</a>
                            <#else>
                                ${item.title}
                            </#if>
                        </${weight}>
                    </#if>
                    <@hst.html var="htmlcontent" hippohtml=item.content/>
                    <#if htmlcontent?has_content>
                    <div class="ds_cb--feature-grid__item-summary">
                        <@hst.html hippohtml=item.content/>
                    </div>
                    </#if>

                    <@hst.manageContent hippobean=item documentTemplateQuery="new-featuregriditem-document" parameterName="document${item?index + 1}"/>
                </div>
            <#elseif editMode>
                <div class="ds_cb--feature-grid__item  cms-blank">
                    <#if showimages>
                    <div class="ds_cb--feature-grid__item-media  <#if smallvariant>ds_cb--feature-grid__item-media--small-mobile</#if>">
                        <@placeholderimage/>
                    </div>
                    </#if>
                    <${weight} class="ds_cb--feature-grid__item-title">
                        <@placeholdertext lines=2/>
                    </${weight}>
                    <div class="ds_cb--feature-grid__item-summary">
                        <@placeholdertext lines=4/>
                    </div>

                    <@hst.manageContent documentTemplateQuery="new-featuregriditem-document" parameterName="document${item?index + 1}"/>
                </div>
            </#if>

        </#list>
        </div>
    </div>
</div>
</#if>

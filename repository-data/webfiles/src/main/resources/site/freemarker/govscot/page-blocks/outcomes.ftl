<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<#if hstRequestContext.preview><div class="cms-display-grid"></#if>
<#if outcomes?has_content>
<div class="ds_pb  ds_pb--browse
<#if backgroundcolor?has_content>
<#switch backgroundcolor?lower_case>
  <#case 'secondary'>
  ds_pb--background-secondary
  <#break>
  <#case 'tertiary'>
  ds_pb--background-tertiary
  <#break>
  <#case 'theme'>
  ds_pb__theme--background-secondary
  <#break>
</#switch>
</#if>">
    <div class="ds_wrapper">
        <!--noindex-->
        <div class="ds_card-grid  ds_card-grid--min-height  ds_card-grid--narrow  ds_card-grid--medium-2">
        <#list outcomes as outcome>
            <div class="ds_card  ds_card--navigation">
                <#if outcome.image??>
                    <div class="ds_card__media">
                        <div class="ds_aspect-box">
                            <#if outcome.image.xlargefourcolumns??>
                                <img class="ds_aspect-box__inner" alt="${outcome.alt!""}" src="<@hst.link hippobean=outcome.image.xlargefourcolumns/>"
                                    width="${outcome.image.xlargefourcolumns.width?c}"
                                    height="${outcome.image.xlargefourcolumns.height?c}"
                                    loading="lazy"
                                    srcset="
                                    <@hst.link hippobean=outcome.image.smallcolumns/> 360w,
                                    <@hst.link hippobean=outcome.image.smallcolumnsdoubled/> 720w,
                                    <@hst.link hippobean=outcome.image.mediumfourcolumns/> 224w,
                                    <@hst.link hippobean=outcome.image.mediumfourcolumnsdoubled/> 448w,
                                    <@hst.link hippobean=outcome.image.largefourcolumns/> 288w,
                                    <@hst.link hippobean=outcome.image.largefourcolumnsdoubled/> 576w,
                                    <@hst.link hippobean=outcome.image.xlargefourcolumns/> 352w,
                                    <@hst.link hippobean=outcome.image.xlargefourcolumnsdoubled/> 704w"
                                    sizes="(min-width:1200px) 352px, (min-width:992px) 288px, (min-width: 768px) 224px, 100vw">
                            <#else>
                                <img loading="lazy" class="ds_aspect-box__inner" src="<@hst.link hippobean=outcome.image/>" alt="${outcome.alt!""}"/>
                            </#if>
                        </div>
                    </div>
                </#if>
                <div class="ds_card__content">
                    <div class="ds_card__content-header">
                        <h2 class="ds_card__title">
                            <#assign outcomeHref><@hst.link hippobean=outcome/></#assign>
                            <a class="ds_card__link  ds_card__link--cover" href="${outcomeHref}">${outcome.title}</a>
                        </h2>
                    </div>
                    <#if outcome.summary?has_content>
                    <div class="ds_card__content-main">${outcome.summary}</div>
                    </#if>
                    <div class="ds_card__content-footer">
                        <#assign statusCounts = (outcomeStatusCounts[outcome.parentBean.name])!{}>
                        <#if statusCounts?has_content>
                            <div class="gov_number-status-tag__grid">
                                <#list ["improving", "stable", "worsening"] as status>
                                    <#assign statusLabel = (status == "stable")?then("maintaining", status)>
                                    <div class="gov_number-status-tag">
                                        <span class="ds_tag  ds_tag--<#if status == "improving">green<#elseif status == "worsening">orange<#else>grey</#if>">${(statusCounts[status])!0}</span>
                                        <span class="gov_number-status-tag__label">${statusLabel?cap_first}</span>
                                    </div>
                                </#list>
                            </div>
                        </#if>
                    </div>
                </div>
            </div>
        </#list>
        </div>
        <!--endnoindex-->
    </div>
</div>
</#if>
<#if hstRequestContext.preview></div></#if>

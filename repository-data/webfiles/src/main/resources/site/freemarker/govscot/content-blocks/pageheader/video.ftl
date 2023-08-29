<#ftl output_format="HTML">
<#include "../../../include/imports.ftl">
<#include "../../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Pageheading" -->
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document?? || editMode>
<header>
    <div class="ds_cb  ds_cb--page-title
    <#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>  ds_cb--fullwidth</#if>
    <#if neutrallinks>  ds_cb--neutral-links</#if>
    <#if widetext>  ds_cb--page-title--wide</#if>
    ">
        <div class="ds_wrapper">
            <div class="ds_cb__inner">
            <#if document??>
                <div class="ds_cb__text  ds_cb__content<#if verticalalign??>  ds_cb__text--${verticalalign}</#if>">
                    <div class="ds_page-header">
                        <h1 class="ds_page-header__title<#if lightheader>  ds_page-header__title--light</#if>">${document.title}</h1>
                    </div>
                    <@hst.html hippohtml=document.description/>
                    <#if document.cta??>
                        <#if document.link??>
                            <a href="<@hst.link hippobean=document.link/>" class="ds_button ds_button--has-icon">${document.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                                <use href="${iconspath}#chevron_right"></use>
                            </svg></a>
                        <#elseif document.externalLink?has_content>
                            <a href="${document.externalLink}" class="ds_button ds_button--has-icon">${document.cta}<svg class="ds_icon" aria-hidden="true" role="img">
                                <use href="${iconspath}#chevron_right"></use>
                            </svg></a>
                        </#if>
                    </#if>
                </div>
                <#if document.videoImage??>
                    <div class="ds_cb__poster<#if medianomargin>  ds_cb__poster--no-margin</#if><#if mediaaligndesktop??>  ds_cb__poster--${mediaaligndesktop}</#if><#if mediaalignmobile??>  ds_cb__poster--${mediaalignmobile}-mobile</#if>">
                        <a target="_blank" class="ds_cb__poster__link" href="${document.videoUrl}">
                            <#if document.videoImage.xlargesixcolumns??>
                                <img alt="${document.videoAlt}" src="<@hst.link hippobean=document.videoImage.xlargesixcolumns />"
                                    class="ds_cb__poster-video"
                                    width="${document.videoImage.xlargesixcolumns.width?c}"
                                    height="${document.videoImage.xlargesixcolumns.height?c}"
                                    srcset="
                                    <@hst.link hippobean=document.videoImage.smallcolumns/> 360w,
                                    <@hst.link hippobean=document.videoImage.smallcolumnsdoubled/> 720w,
                                    <@hst.link hippobean=document.videoImage.mediumsixcolumns/> 352w,
                                    <@hst.link hippobean=document.videoImage.mediumsixcolumnsdoubled/> 704w,
                                    <@hst.link hippobean=document.videoImage.largesixcolumns/> 448w,
                                    <@hst.link hippobean=document.videoImage.largesixcolumnsdoubled/> 896w,
                                    <@hst.link hippobean=document.videoImage.xlargesixcolumns/> 544w,
                                    <@hst.link hippobean=document.videoImage.xlargesixcolumnsdoubled/> 1088w"
                                    sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, 100vw"
                                    >
                            <#else>
                                <img class="ds_cb__poster-video" src="<@hst.link hippobean=document.videoImage />" alt="" width="${document.videoImage.original.width?c}" height="${document.videoImage.original.height?c}">
                            </#if>

                            <svg class="ds_cb__poster__overlay" xmlns="http://www.w3.org/2000/svg" xml:space="preserve" viewBox="0 0 160 90">
                                <circle class="background" cx="82" cy="45.5" r="16" fill="#0065db"/>
                                <path class="foreground" fill="#fff" d="M76 45.3v-9.2l8 4.6 8 4.6-8 4.7-8 4.6z"/>
                            </svg>
                        </a>
                    </div>
                <#elseif document.videoUrl??>
                    <a target="_blank" class="ds_cb__poster__link" href="${document.videoUrl}">
                        Watch this video
                    </a>
                </#if>
            <@hst.manageContent hippobean=document documentTemplateQuery="new-pageheading-document" parameterName="document"/>

            <#elseif editMode>
                <div class="ds_cb__text  ds_cb__content">
                    <div class="ds_page-header">
                        <h1 class="ds_page-header__title"><@placeholdertext lines=2/></h1>
                    </div>

                    <@placeholdertext lines=4/>
                </div>

                <div class="ds_cb__poster">
                    <@placeholdervideo/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-pageheading-document" parameterName="document"/>
            </#if>
            </div>
        </div>
    </div>
<#if document??>
    <@hst.html var="htmlaside" hippohtml=document.aside/>
    <#if htmlaside?has_content>
    <aside class="ds_cb  ds_cb--page-title__aside
    <#if asidebackgroundcolor?? && asidebackgroundcolor?length gt 0>  ds_cb--bg-${asidebackgroundcolor}</#if>
    <#if fullwidth>  ds_cb--fullwidth</#if>
    <#if neutrallinks>  ds_cb--neutral-links</#if>
    ">
        <div class="ds_wrapper">
            <#if document.asideIcon?? && !hideasideicon>
            <span class="ds_cb--page-title__aside-icon" aria-hidden="true">
                <img src="<@hst.link hippobean=document.asideIcon.thumbnail />" loading="lazy" alt="">
            </span>
            </#if>
            <@hst.html hippohtml=document.aside/>
        </div>
    </aside>
    </#if>
</#if>
</header>
</#if>

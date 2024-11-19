<#ftl output_format="HTML">
<#include "../../../include/imports.ftl">
<#include "../../../include/cms-placeholders.ftl">
<#-- @ftlvariable name="document" type="scot.gov.www.beans.Pageheading" -->
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document?? || editMode>
<header>
    <div class="ds_cb  ds_cb--page-title  ds_cb--page-title--video
    <#if backgroundcolor?? && backgroundcolor?length gt 0>  ds_cb--bg-${backgroundcolor}</#if>
    <#if foregroundcolor?? && foregroundcolor?length gt 0>  ds_cb--fg-${foregroundcolor}</#if>
    <#if fullwidth>  ds_cb--fullwidth</#if>
    <#if neutrallinks>  ds_cb--neutral-links</#if>
    <#if widetext>  ds_cb--page-title--wide</#if>
    ">
        <div class="ds_wrapper">
            <div class="ds_cb__inner">
            <#if document??>
                <div class="ds_cb__text  ds_cb__content<#if verticalalign??>  ds_cb__text--${verticalalign}</#if> <#if mediaalignmobile??>  ds_cb__text--${mediaalignmobile}-mobile-spacing</#if>">
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
                <#if document.image??>
                    <div class="ds_cb__poster<#if medianomargin>  ds_cb__poster--no-margin</#if><#if mediaaligndesktop??>  ds_cb__poster--${mediaaligndesktop}</#if><#if mediaalignmobile??>  ds_cb__poster--${mediaalignmobile}-mobile</#if>">
                        <#if document.videoUrl?has_content>
                        <a target="_blank" class="ds_cb__poster__link" href="${document.videoUrl}">
                        </#if>
                        <picture>
                        <#if mediaalignmobile == "hidden">
                            <source media="(max-width: 767px)" sizes="1px" srcset="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7 1w" />
                        </#if>
                        <#if document.image.xlargesixcolumns??>
                            <img alt="${document.alt}" src="<@hst.link hippobean=document.image.xlargesixcolumns />"
                                class="ds_cb__poster-video"
                                width="${document.image.xlargesixcolumns.width?c}"
                                height="${document.image.xlargesixcolumns.height?c}"
                                srcset="
                                <@hst.link hippobean=document.image.smallcolumns/> 360w,
                                <@hst.link hippobean=document.image.smallcolumnsdoubled/> 720w,
                                <@hst.link hippobean=document.image.mediumsixcolumns/> 352w,
                                <@hst.link hippobean=document.image.mediumsixcolumnsdoubled/> 704w,
                                <@hst.link hippobean=document.image.largesixcolumns/> 448w,
                                <@hst.link hippobean=document.image.largesixcolumnsdoubled/> 896w,
                                <@hst.link hippobean=document.image.xlargesixcolumns/> 544w,
                                <@hst.link hippobean=document.image.xlargesixcolumnsdoubled/> 1088w"
                                sizes="(min-width:1200px) 544px, (min-width:992px) 448px, (min-width: 768px) 352px, 100vw"
                                >
                        <#else>
                            <img class="ds_cb__poster-video" src="<@hst.link hippobean=document.image />" alt="${document.alt}" width="${document.image.original.width?c}" height="${document.image.original.height?c}">
                        </#if>
                        </picture>
                        <#if document.videoUrl?has_content>
                            <svg class="ds_cb__poster__overlay" xmlns="http://www.w3.org/2000/svg" width="104" height="104" viewBox="0 0 104 104" fill="none">
                                <path class="background" d="M104 52C104 80.7188 80.7188 104 52 104C23.2812 104 0 80.7188 0 52C0 23.2812 23.2812 0 52 0C80.7188 0 104 23.2812 104 52Z" fill="#0065BD"/>
                                <path class="border" fill-rule="evenodd" clip-rule="evenodd" d="M52 100C78.5097 100 100 78.5097 100 52C100 25.4903 78.5097 4 52 4C25.4903 4 4 25.4903 4 52C4 78.5097 25.4903 100 52 100ZM52 104C80.7188 104 104 80.7188 104 52C104 23.2812 80.7188 0 52 0C23.2812 0 0 23.2812 0 52C0 80.7188 23.2812 104 52 104Z" fill="white"/>
                                <path class="foreground" d="M74 52L41 76.2487L41 27.7513L74 52Z" fill="white"/>
                            </svg>
                        </a>
                        </#if>
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

                    <@placeholdertext lines=8/>
                </div>

                <div class="ds_cb__poster">
                    <@placeholdervideo/>
                </div>

                <@hst.manageContent documentTemplateQuery="new-pageheading-document" parameterName="document"/>
            </#if>
            </div>
        </div>
    </div>
</header>
</#if>

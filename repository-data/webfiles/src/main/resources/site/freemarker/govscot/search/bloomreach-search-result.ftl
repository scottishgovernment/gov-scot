<@hst.link var="link" hippobean=item/>

<li class="ds_search-result">
<#if ((hst.isBeanType(item, "scot.gov.www.beans.Role") && item.incumbent??) || hst.isBeanType(item, "scot.gov.www.beans.Person")) && item.image??>
    <h3 class="ds_search-result__title">
        <a class="ds_search-result__link" href="${link}">${item.roleTitle}</a>
    </h3>

    <div class="ds_search-result__has-media">
        <div class="ds_search-result__media-wrapper">
            <div class="ds_search-result__media  ds_aspect-box  ds_aspect-box--square">
                <a class="ds_search-result__media-link" href="${link}" tabindex="-1">
                    <img alt=""
                         aria-hidden="true"
                         class="ds_aspect-box__inner"
                         width="${item.image.largetwocolumnsdoubledsquare.width?c}"
                         height="${item.image.largetwocolumnsdoubledsquare.height?c}"
                         loading="lazy"
                         src="<@hst.link hippobean=item.image.largetwocolumnssquare />"
                         srcset="
                                                <@hst.link hippobean=item.image.mediumtwocolumnssquare/> 96w,
                                                <@hst.link hippobean=item.image.largetwocolumnssquare/> 128w,
                                                <@hst.link hippobean=item.image.mediumtwocolumnsdoubledsquare/> 192w,
                                                <@hst.link hippobean=item.image.largetwocolumnsdoubledsquare/> 256w"
                         sizes="(min-width:480px) 128px, 96px">
                </a>
            </div>
        </div>
        <div>
            <#if item.name??>
                <h4 class="ds_search-result__sub-title">${item.name}</h4>
            </#if>
            <#if item.summary??>
                <p class="ds_search-result__summary">${item.summary}</p>
            </#if>
        </div>
    </div>
<#else>
    <h3 class="ds_search-result__title">
        <a class="ds_search-result__link" href="${link}">${item.title}</a>
    </h3>
    <#if item.summary??>
    <p class="ds_search-result__summary">
    ${item.summary}
    </p>
    </#if>

    <#if item.publicationDate?? || item.label?has_content>
    <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
        <#if item.label?has_content>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Type</dt>
                <dd class="ds_metadata__value">${item.label?cap_first}</dd>
            </div>
        </#if>

        <#if item.publicationDate??>
            <#assign dateFormat = "dd MMMM yyyy">
            <#assign displayDate = (item.displayDate.time)!(item.publicationDate.time)>
            <#if hst.isBeanType(item, "scot.gov.www.beans.News")>
                <#assign dateFormat = "dd MMMM yyyy HH:mm">
            </#if>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Publication date</dt>
                <dd class="ds_metadata__value"><@fmt.formatDate value=displayDate type="both" pattern=dateFormat /></dd>
            </div>
        </#if>
    </dl>
    </#if>

    <#if item.collections?has_content || item.parent??>
    <dl class="ds_search-result__context">
        <dt class="ds_search-result__context-key">Part of:</dt>
        <#if item.collections?has_content>
            <#list item.collections as collection>
                <@hst.link var="link" hippobean=collection/>
                <dd class="ds_search-result__context-value">
                    <a data-navigation="collections-${collection?index + 1}" href="${link}">${collection.title}</a>
                </dd>
            </#list>
        </#if>
        <#if item.parent??>
            <dd class="ds_search-result__context-value">
                <a href="<@hst.link hippobean=item.parent/>">${item.parent.title}</a>
            </dd>
        </#if>
    </dl>
    </#if>
</#if>

</li>

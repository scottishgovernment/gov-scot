<li class="ds_search-result">
    <h3 class="ds_search-result__title">
        <a class="ds_search-result__link" href="${result.liveUrl}">
        <#if (result.listMetadata["dcTitle"]?first)?has_content>
            ${result.listMetadata["dcTitle"]?first!}
        <#else>
            ${result.listMetadata["t"]?first!}
        </#if>
        </a>
    </h3>

<#if result.listMetadata["f"]?first = 'Role'
|| result.listMetadata["f"]?first = 'Person'
|| result.listMetadata["f"]?first = 'Featured role'>
    <#if result.listMetadata["image"]!?has_content>

    <div class="ds_search-result__has-media">
        <div class="ds_search-result__media-wrapper">
            <div class="ds_search-result__media  ds_aspect-box  ds_aspect-box--square">
                <a class="ds_search-result__media-link" href="${result.liveUrl}" tabindex="-1">
                    <img alt=""
                            aria-hidden="true"
                            class="ds_aspect-box__inner"
                            width="96"
                            height="96"
                            loading="lazy"
                            srcset="${result.listMetadata['image']?first}/govscot%3Amediumtwocolumnssquare 96w,
                                    ${result.listMetadata['image']?first}/govscot%3Alargetwocolumnssquare 128w,
                                    ${result.listMetadata['image']?first}/govscot%3Amediumtwocolumnsdoubledsquare 192w,
                                    ${result.listMetadata['image']?first}/govscot%3Alargetwocolumnsdoubledsquare 256w"
                            sizes="(min-width:480px) 128px, 96px"
                            src="${result.listMetadata['image']?first}/govscot%3Amediumtwocolumnssquare">
                </a>
            </div>
        </div>
        <div>
            <h4 class="ds_search-result__sub-title">
                <#if result.listMetadata["f"]?first = 'Role' || result.listMetadata["f"]?first = 'Featured role'>
                    ${result.listMetadata['personName']?first}
                <#else>
                    ${result.listMetadata['personRole']?first}
                </#if>
            </h4>
            <p class="ds_search-result__summary"><@highlightSearchTerm result.listMetadata["c"]?first /></p>
        </div>
    </div>
    </#if>
<#else>
    <p class="ds_search-result__summary">
        <@highlightSearchTerm result.listMetadata["c"]?first />
    </p>

    <#if result.listMetadata["f"]?first = 'Publication'>
        <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
            <#if result.listMetadata["publicationType"]!?has_content>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Publication type</dt>
                <dd class="ds_metadata__value">
                ${result.listMetadata["publicationType"]?first!}
                </dd>
            </div>
            </#if>
            <#if (result.listMetadata["d"]?first)!?has_content>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Date</dt>
                <dd class="ds_metadata__value">
                ${result.listMetadata.displayDate}
                </dd>
            </div>
            </#if>
        </dl>
    <#elseif result.listMetadata["f"]?first = 'News'
    || result.listMetadata["f"]?first = 'Policy'
    || result.listMetadata["f"]?first = 'Collection'>
        <dl class="ds_search-result__metadata  ds_metadata  ds_metadata--inline">
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Format</dt>
                <dd class="ds_metadata__value">
                ${result.listMetadata["f"]?first!}
                </dd>
            </div>
            <#if (result.listMetadata["d"]?first)!?has_content
            && result.listMetadata["f"]?first = 'News'>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">Date</dt>
                <dd class="ds_metadata__value">
                ${result.listMetadata.displayDateTime}
                </dd>
            </div>
            </#if>
        </dl>
    </#if>
    <#if (result.listMetadata["titleSeriesLink"]?first)!?has_content>
        <dl class="ds_search-result__context">
            <dt class="ds_search-result__context-key">Part of:</dt>
            <dd class="ds_search-result__context-value">
                <a href="${(result.listMetadata["titleSeriesLink"]?first)!}">${(result.listMetadata["titleSeries"]?first)!}</a>
            </dd>
        </dl>
    <#elseif (result.listMetadata["publicationCollection"]?first)!?has_content>
        <dl class="ds_search-result__context">
            <dt class="ds_search-result__context-key">Part of:</dt>
            <#list result.listMetadata["publicationCollection"] as collection>
            <dd class="ds_search-result__context-value">
                <a href="${result.listMetadata["publicationCollectionLink"][collection?index]}">${collection!}</a>
            </dd>
            </#list>
        </dl>
    </#if>
</#if>
</li>

<#ftl output_format="HTML">

<#include "macros/lang-attributes.ftl">

<div <@revertlang document /> id="history">
    <dl class="ds_metadata">
        <#if document.publicationDate??>
            <div class="ds_metadata__item">
                <dt class="ds_metadata__key">First published</dt>
                <dd class="ds_metadata__value"><strong><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMMM yyyy"/></strong></dd>
            </div>
        </#if>

        <#assign latestUpdate = document.updateHistory[0].lastUpdated>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Last updated</dt>
            <dd class="ds_metadata__value">
                <strong><@fmt.formatDate value=latestUpdate.time type="both" pattern="d MMMM yyyy"/></strong>
                <span class="gov_toggle-link">
                    - <a href="#full-history" data-module="gov-toggle-link" aria-controls="full-history" data-toggled-text="hide all updates">show all updates</a>
                </span>
            </dd>
        </div>

        <div id="full-history" data-module="gov-update-history" class="ds_metadata__item  gov_toggle-link__target">
            <dt class="ds_metadata__key  visually-hidden">All updates</dt>
            <dd class="ds_metadata__value  gov_history-list">
                <ol class="ds_no-bullets">
                    <#list document.updateHistory as history>
                        <li class="gov_history-list__item">
                            <time datetime="<@fmt.formatDate value=history.lastUpdated.time type="both" pattern="yyyy-MM-dd'T'HH:mm:ssz"/>">
                                <@fmt.formatDate value=history.lastUpdated.time type="both" pattern="d MMMM yyyy"/>
                            </time>
                            <p class="gov_history-list__content">${history.updateText}</p>
                        </li>
                    </#list>
                </ol>
            </dd>
        </div>
    </dl>
</div>

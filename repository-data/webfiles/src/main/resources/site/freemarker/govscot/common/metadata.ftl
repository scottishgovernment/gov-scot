<dl class="ds_page-header__metadata  ds_metadata">
    <#if document.updateHistory?has_content>
        <#assign latestUpdate = document.updateHistory[0].lastUpdated>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Last updated</dt>
            <dd class="ds_metadata__value"><@fmt.formatDate value=latestUpdate.time type="both" pattern="d MMM yyyy"/> - <a href="#full-history">see all updates</a></dd>
        </div>
    <#elseif document.lastUpdatedDate??>
        <div class="ds_metadata__item">
            <dt class="ds_metadata__key">Last updated</dt>
            <dd class="ds_metadata__value"><@fmt.formatDate value=document.lastUpdatedDate.time type="both" pattern="d MMM yyyy"/></dd>
        </div>
    </#if>
</dl>

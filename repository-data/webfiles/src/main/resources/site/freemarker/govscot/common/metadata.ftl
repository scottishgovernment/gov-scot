<#if document.updateHistory?has_content>
<section class="content-data">
    <#assign latestUpdate = document.updateHistory[0].lastUpdated>
    <div class="content-data__list">
        <span class="content-data__label">Last updated:</span>
        <span class="content-data__value"><strong><@fmt.formatDate value=latestUpdate.time type="both" pattern="d MMM yyyy"/></strong> - <a href="#history">see all updates</a></span>
    </div>
</section>
</#if>

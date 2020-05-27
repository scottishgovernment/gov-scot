<div id="history" class="content-data__list">
    <span class="content-data__label">First published:</span>
    <span class="content-data__value"><strong><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMM yyyy"/></strong></span>
</div>
<div class="content-data__list">
    <#assign latestUpdate = document.updateHistory[0].lastUpdated>
    <span class="content-data__label">Last updated:</span>
    <#--This will need to written to do show all updates / hide all updates-->
    <span class="content-data__value"><strong><@fmt.formatDate value=latestUpdate.time type="both" pattern="d MMM yyyy"/></strong>  - <a href="#">hide all updates</a></span>
</div>
<div class="publication-info__history">
    <#list document.updateHistory as history>
        <div class="content-data__list">
            <span class="content-data__value"><strong><@fmt.formatDate value=history.lastUpdated.time type="both" pattern="d MMM yyyy"/></strong></span>
            <span><p>${history.updateText}</p></span>
        </div>
    </#list>
</div>

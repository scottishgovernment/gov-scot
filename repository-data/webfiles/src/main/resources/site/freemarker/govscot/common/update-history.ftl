<div class="js-show-hide" id="history">
    <div class="content-data__list">
        <span class="content-data__label">First published:</span>
        <span class="content-data__value"><strong><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMM yyyy"/></strong></span>

        <#assign latestUpdate = document.updateHistory[0].lastUpdated>
        <span class="content-data__label">Last updated:</span>
        <span class="content-data__value"><strong><@fmt.formatDate value=latestUpdate.time type="both" pattern="d MMM yyyy"/></strong> - <a class="js-trigger" href="#full-history" aria-controls="update-list" aria-expanded="false" data-toggled-text="hide all updates">show all updates</a></span>
    </div>

    <div class="publication-info__history  hidden  hidden--hard" id="full-history">
        <ol class="history-list">
            <#list document.updateHistory as history>
                <li class="history-list__item">
                    <time class="history-list__date" datetime="<@fmt.formatDate value=history.lastUpdated.time type="both" pattern="yyyy-MM-dd'T'HH:mm:ssz"/>">
                        <@fmt.formatDate value=history.lastUpdated.time type="both" pattern="d MMM yyyy"/>
                    </time>
                    <p>${history.updateText}</p>
                </li>
            </#list>
        </ol>
    </div>
</div>

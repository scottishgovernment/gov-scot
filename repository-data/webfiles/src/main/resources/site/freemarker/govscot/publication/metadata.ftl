<dl class="ds_page-header__metadata  ds_metadata">
    <#if document.publicationDate??>
        <div class="ds_metadata__item">
            <span class="ds_metadata__key">Published</span>
            <span class="ds_metadata__value"><strong><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMM yyyy"/></strong></span>
        </div>
    </#if>

    <#if document.updateHistory?has_content>
        <#assign latestUpdate = document.updateHistory[0].lastUpdated>
        <div class="ds_metadata__item">
            <span class="ds_metadata__key">Last updated</span>
            <span class="ds_metadata__value"><strong><@fmt.formatDate value=latestUpdate.time type="both" pattern="d MMM yyyy"/></strong> - <a href="#history">see all updates</a></span>
        </div>
    </#if>

    <#assign index=document/>
    <#assign metadataChildrenOnly = true/>
    <#include '../common/content-data.ftl'/>

    <#--! BEGIN 'minutes' format-specific fields-->
    <#if document.publicationType?lower_case! == "minutes">
        <#if document.officialdate?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Date of meeting</span>
                <span class="ds_metadata__value"><strong><@fmt.formatDate value=document.officialdate.time type="both" pattern="d MMM yyyy"/></strong></span>
            </div>
        </#if>

        <#if document.nextMeetingDate?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Date of next meeting</span>
                <span class="ds_metadata__value"><strong><@fmt.formatDate value=document.nextMeetingDate.time type="both" pattern="d MMM yyyy"/></strong></span>
            </div>
        </#if>

        <#if document.location?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Location:</span>
                <span class="ds_metadata__value"><strong>${document.location}</strong></span>
            </div>
        </#if>
    </#if>
    <#--! END 'minutes' format-specific fields-->

    <#--! BEGIN 'speech or statement' format-specific fields-->
    <#if document.publicationType?lower_case! == "speech-statement">
        <#if document.officialdate?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Date of speech</span>
                <span class="ds_metadata__value"><strong><@fmt.formatDate value=document.officialdate.time type="both" pattern="d MMM yyyy"/></strong></span>
            </div>
        </#if>

        <#if document.speechDeliveredBy?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Delivered by</span>
                <span class="ds_metadata__value"><strong>${document.speechDeliveredBy}</strong></span>
            </div>
        </#if>

        <#if document.location?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Location</span>
                <span class="ds_metadata__value"><strong>${document.location}</strong></span>
            </div>
        </#if>
    </#if>
    <#--! END 'speech or statement' format-specific fields-->

</section>

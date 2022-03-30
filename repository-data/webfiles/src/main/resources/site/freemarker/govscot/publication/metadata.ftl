<#ftl output_format="HTML">
<dl class="ds_page-header__metadata  ds_metadata">
    <#if document.publicationDate??>
        <div class="ds_metadata__item">
            <span class="ds_metadata__key">Published</span>
            <span class="ds_metadata__value"><strong id="sg-meta__publication-date"><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMMM yyyy"/></strong></span>
        </div>
    </#if>

    <#if document.updateHistory?has_content>
        <#assign latestUpdate = document.updateHistory[0].lastUpdated>
        <div class="ds_metadata__item">
            <span class="ds_metadata__key">Last updated</span>
            <span class="ds_metadata__value"><strong id="sg-meta__last-updated-date"><@fmt.formatDate value=latestUpdate.time type="both" pattern="d MMMM yyyy"/></strong> - <a href="#full-history">see all updates</a></span>
        </div>
    </#if>

    <#assign index=document/>
    <#assign metadataChildrenOnly = true/>
    <#include '../common/content-metadata.ftl'/>

    <#--! BEGIN 'minutes' format-specific fields-->
    <#if document.publicationType?has_content && document.publicationType?lower_case != "minutes">
        <#if document.officialdate?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Date of meeting</span>
                <span class="ds_metadata__value"><strong id="sg-meta__meeting-date"><@fmt.formatDate value=document.officialdate.time type="both" pattern="d MMMM yyyy"/></strong></span>
            </div>
        </#if>

        <#if document.nextMeetingDate?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Date of next meeting</span>
                <span class="ds_metadata__value"><strong><@fmt.formatDate value=document.nextMeetingDate.time type="both" pattern="d MMMM yyyy"/></strong></span>
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
    <#if document.publicationType?has_content && document.publicationType?lower_case != "speech-statement">
        <#if document.officialdate?has_content>
            <div class="ds_metadata__item">
                <span class="ds_metadata__key">Date of speech</span>
                <span class="ds_metadata__value"><strong><@fmt.formatDate value=document.officialdate.time type="both" pattern="d MMMM yyyy"/></strong></span>
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

    <#--! BEGIN 'FOI/EIR release' format-specific fields-->
    <#if document.foiNumber?has_content>
        <div class="ds_metadata__item">
            <span class="ds_metadata__key">FOI reference</span>
            <span class="ds_metadata__value"><span id="sg-meta__foi-number">${document.foiNumber}</span></span>
        </div>
    </#if>
    <#if document.dateReceived?has_content>
        <div class="ds_metadata__item">
            <span class="ds_metadata__key">Date received</span>
            <span class="ds_metadata__value"><span id="sg-meta__foi-received-date"><@fmt.formatDate value=document.dateReceived.time type="both" pattern="d MMMM yyyy"/></span></span>
        </div>
    </#if>
    <#if document.dateResponded?has_content>
        <div class="ds_metadata__item">
            <span class="ds_metadata__key">Date responded</span>
            <span class="ds_metadata__value"><span id="sg-meta__foi-responded-date"><@fmt.formatDate value=document.dateResponded.time type="both" pattern="d MMMM yyyy"/></span></span>
        </div>
    </#if>
    <#--! END 'FOI/EIR release' format-specific fields-->
</dl>

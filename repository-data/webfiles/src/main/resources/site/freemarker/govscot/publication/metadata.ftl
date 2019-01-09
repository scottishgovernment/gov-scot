<section class="content-data">
    <#if document.publicationDate??>
        <div class="content-data__list">
            <span class="content-data__label">Published:</span>
            <span class="content-data__value"><strong><@fmt.formatDate value=document.publicationDate.time type="both" pattern="d MMM yyyy"/></strong></span>
        </div>
    </#if>

    <#assign index=document/>
    <#include '../common/content-data.ftl'/>

    <#--! BEGIN 'minutes' format-specific fields-->
    <#if document.publicationType?lower_case == "minutes">
        <#if document.officialdate?has_content>
            <div class="content-data__list">
                <span class="content-data__label">Date of meeting:</span>
                <span class="content-data__value"><strong><@fmt.formatDate value=document.officialdate.time type="both" pattern="d MMM yyyy"/></strong></span>
            </div>
        </#if>

        <#if document.nextMeetingDate?has_content>
            <div class="content-data__list">
                <span class="content-data__label">Date of next meeting:</span>
                <span class="content-data__value"><strong><@fmt.formatDate value=document.nextMeetingDate.time type="both" pattern="d MMM yyyy"/></strong></span>
            </div>
        </#if>

        <#if document.location?has_content>
            <div class="content-data__list">
                <span class="content-data__label">Location:</span>
                <span class="content-data__value"><strong>${document.location}</strong></span>
            </div>
        </#if>
    </#if>
    <#--! END 'minutes' format-specific fields-->

    <#--! BEGIN 'speech or statement' format-specific fields-->
    <#if document.publicationType?lower_case == "speech---statement">
        <#if document.officialdate?has_content>
            <div class="content-data__list">
                <span class="content-data__label">Date of speech:</span>
                <span class="content-data__value"><strong><@fmt.formatDate value=document.officialdate.time type="both" pattern="d MMM yyyy"/></strong></span>
            </div>
        </#if>

        <#if document.speechDeliveredBy?has_content>
            <div class="content-data__list">
                <span class="content-data__label">Delivered by:</span>
                <span class="content-data__value"><strong>${document.speechDeliveredBy}</strong></span>
            </div>
        </#if>

        <#if document.location?has_content>
            <div class="content-data__list">
                <span class="content-data__label">Location:</span>
                <span class="content-data__value"><strong>${document.location}</strong></span>
            </div>
        </#if>
    </#if>
    <#--! END 'speech or statement' format-specific fields-->

</section>

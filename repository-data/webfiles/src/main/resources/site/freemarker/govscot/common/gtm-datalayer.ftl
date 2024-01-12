<#ftl output_format="HTML">

<#if document??>
    <#if !dateCreated??><#assign dateCreated = document.getSingleProperty('hippostdpubwf:creationDate')/></#if>
    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <#if !uuid??><#assign uuid = document.getSingleProperty('jcr:uuid')/></#if>
    <#if !reportingTags?? && document.getMultipleProperty('govscot:reportingTags')??>
        <#assign reportingTags = document.getMultipleProperty('govscot:reportingTags')/>
    </#if>

    <@hst.headContribution category="dataLayer">
    <script id="gtm-datalayer"
        src='<@hst.webfile path="assets/scripts/datalayer.js"/>'
        <#if document.responsibleRole??>data-role="<#if document.responsibleRole?is_sequence><#list document.responsibleRole as item>${item.title?js_string}<#sep>|</#sep></#list><#else>${document.responsibleRole.title?js_string}</#if>"</#if>
        <#if document.secondaryResponsibleRole?has_content>data-secondaryrole="<#list document.secondaryResponsibleRole as role>${role.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if document.responsibleDirectorate??>data-directorate="<#if document.responsibleDirectorate?is_sequence><#list document.responsibleDirectorate as item>${item.title?js_string}<#sep>|</#sep></#list><#else>${document.responsibleDirectorate.title?js_string}</#if>"</#if>
        <#if document.secondaryResponsibleDirectorate?has_content>data-secondarydirectorate="<#list document.secondaryResponsibleDirectorate as dir>${dir.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if document.topics?has_content>data-topics="<#list document.topics as topic>${topic.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if document.publicationDate??>data-publicationdate='<@fmt.formatDate value=document.publicationDate.time type="Date" pattern="dd/MM/yyyy" />'</#if>
        <#if reportingTags?has_content>data-reportingtags="<#list reportingTags as tag>${tag?js_string}<#sep>|</#sep></#list>"</#if>
        <#if policies?has_content>data-policies="<#list policies as policy>${policy?js_string}<#sep>|</#sep></#list>"</#if>
        <#if collections?has_content>data-collections="<#list collections as collection>${collection.title?js_string}<#sep>|</#sep></#list>"</#if>
        data-lastupdated='<@fmt.formatDate value=lastUpdated.time type="Date" pattern="dd/MM/yyyy" />'
        data-datecreated='<@fmt.formatDate value=dateCreated.time type="Date" pattern="dd/MM/yyyy" />'
        data-uuid="${uuid}"
        >
    </script>
    </@hst.headContribution>
</#if>

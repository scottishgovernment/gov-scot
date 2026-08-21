<#ftl output_format="HTML">

<#if document??>
    <#if !dateCreated??><#assign dateCreated = document.getSingleProperty('hippostdpubwf:creationDate')/></#if>
    <#if !lastUpdated??><#assign lastUpdated = document.getSingleProperty('hippostdpubwf:lastModificationDate')/></#if>
    <#if !uuid??><#assign uuid = document.getSingleProperty('jcr:uuid')/></#if>
    <#if !reportingTags?? && document.getMultipleProperty('govscot:reportingTags')??>
        <#assign reportingTags = document.getMultipleProperty('govscot:reportingTags')/>
    </#if>

    <#assign responsibleRoleItems = []>
    <#if document.responsibleRole??>
        <#if document.responsibleRole?is_sequence>
            <#list document.responsibleRole as item><#if item??><#assign responsibleRoleItems = responsibleRoleItems + [item]></#if></#list>
        <#elseif document.responsibleRole??>
            <#assign responsibleRoleItems = [document.responsibleRole]>
        </#if>
    </#if>
    <#assign secondaryResponsibleRoleItems = []>
    <#if document.secondaryResponsibleRole?has_content>
        <#list document.secondaryResponsibleRole as role><#if role??><#assign secondaryResponsibleRoleItems = secondaryResponsibleRoleItems + [role]></#if></#list>
    </#if>
    <#assign responsibleDirectorateItems = []>
    <#if document.responsibleDirectorate??>
        <#if document.responsibleDirectorate?is_sequence>
            <#list document.responsibleDirectorate as item><#if item??><#assign responsibleDirectorateItems = responsibleDirectorateItems + [item]></#if></#list>
        <#elseif document.responsibleDirectorate??>
            <#assign responsibleDirectorateItems = [document.responsibleDirectorate]>
        </#if>
    </#if>
    <#assign secondaryResponsibleDirectorateItems = []>
    <#if document.secondaryResponsibleDirectorate?has_content>
        <#list document.secondaryResponsibleDirectorate as dir><#if dir??><#assign secondaryResponsibleDirectorateItems = secondaryResponsibleDirectorateItems + [dir]></#if></#list>
    </#if>
    <#assign topicItems = []>
    <#if document.topics?has_content>
        <#list document.topics as topic><#if topic??><#assign topicItems = topicItems + [topic]></#if></#list>
    </#if>
    <#assign collectionItems = []>
    <#if collections?has_content>
        <#list collections as collection><#if collection??><#assign collectionItems = collectionItems + [collection]></#if></#list>
    </#if>

    <@hst.headContribution category="dataLayer">
    <script id="gtm-datalayer"
        src='<@hst.webfile path="assets/scripts/datalayer.js"/>'
        <#if responsibleRoleItems?has_content>data-role="<#list responsibleRoleItems as item>${item.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if secondaryResponsibleRoleItems?has_content>data-secondaryrole="<#list secondaryResponsibleRoleItems as role>${role.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if responsibleDirectorateItems?has_content>data-directorate="<#list responsibleDirectorateItems as item>${item.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if secondaryResponsibleDirectorateItems?has_content>data-secondarydirectorate="<#list secondaryResponsibleDirectorateItems as dir>${dir.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if topicItems?has_content>data-topics="<#list topicItems as topic>${topic.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if document.publicationDate??>data-publicationdate='<@fmt.formatDate value=document.publicationDate.time type="Date" pattern="dd/MM/yyyy" />'</#if>
        <#if reportingTags?has_content>data-reportingtags="<#list reportingTags as tag>${tag?js_string}<#sep>|</#sep></#list>"</#if>
        <#if policies?has_content>data-policies="<#list policies as policy>${policy?js_string}<#sep>|</#sep></#list>"</#if>
        <#if collectionItems?has_content>data-collections="<#list collectionItems as collection>${collection.title?js_string}<#sep>|</#sep></#list>"</#if>
        <#if document.displayDate??>
            data-lastupdated='<@fmt.formatDate value=document.displayDate.time type="Date" pattern="dd/MM/yyyy" />'
        <#else>
            data-lastupdated='<@fmt.formatDate value=lastUpdated.time type="Date" pattern="dd/MM/yyyy" />'
         </#if>


        data-datecreated='<@fmt.formatDate value=dateCreated.time type="Date" pattern="dd/MM/yyyy" />'
        data-uuid="${uuid}"
        >
    </script>
    </@hst.headContribution>
</#if>

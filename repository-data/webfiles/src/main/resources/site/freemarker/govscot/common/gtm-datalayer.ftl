<@hst.headContribution category="dataLayer">
<script id="datalayerPush">

    <#if !dateCreated??><#assign dateCreated = document.getProperty('hippostdpubwf:creationDate')/></#if>
    <#if !lastUpdated??><#assign lastUpdated = document.getProperty('hippostdpubwf:lastModificationDate')/></#if>
    <#if !uuid??><#assign uuid = document.getProperty('jcr:uuid')/></#if>

    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push({
        <#if document.responsibleRole??>
            'responsibleRole': '${document.responsibleRole.title}',
        </#if>
        <#if document.secondaryResponsibleRole?has_content>
            'secondaryResponsibleRole': [<#list document.secondaryResponsibleRole as role>'${role.title}'<#sep>, </#sep></#list>],
        </#if>
        <#if document.responsibleDirectorate??>
            'responsibleDirectorate': '${document.responsibleDirectorate.title}',
        </#if>
        <#if document.secondaryResponsibleDirectorate?has_content>
            'secondaryResponsibleDirectorate': [<#list document.secondaryResponsibleDirectorate as dir>'${dir.title}'<#sep>, </#sep></#list>],
        </#if>
        <#if document.topics?has_content>
            'topics': [<#list document.topics as topic>'${topic.title}'<#sep>, </#sep></#list>],
        </#if>
        <#if document.publicationDate??>
            'publicationDate': '<@fmt.formatDate value=document.publicationDate.time type="Date" pattern="dd/MM/yyyy" />',
        </#if>

        'policies': [<#list policies as policy>'${policy}'<#sep>, </#sep></#list>],
        'lastUpdated': '<@fmt.formatDate value=lastUpdated.time type="Date" pattern="dd/MM/yyyy" />',
        'dateCreated': '<@fmt.formatDate value=dateCreated.time type="Date" pattern="dd/MM/yyyy" />',
        'uuid': '${uuid}'
    })
</script>
</@hst.headContribution>


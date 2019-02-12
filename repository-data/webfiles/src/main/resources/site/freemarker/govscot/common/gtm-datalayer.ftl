<script id="datalayerPush">
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
            'secondaryResponsibleDirectorate' : [<#list document.secondaryResponsibleDirectorate as dir>'${dir.title}'<#sep>, </#sep></#list>],
        </#if>
        <#if document.topics?has_content>
            'topics' : [<#list document.topics as topic>'${topic.title}'<#sep>, </#sep></#list>],
        </#if>
        'lastUpdated': '<@fmt.formatDate value=document.getProperty('hippostdpubwf:lastModificationDate').time type="Date" pattern="dd/MM/yyyy" />',
        'dateCreated': '<@fmt.formatDate value=document.getProperty('hippostdpubwf:creationDate').time type="Date" pattern="dd/MM/yyyy" />',
        'uuid': '${document.getProperty('jcr:uuid')}'
    })
</script>


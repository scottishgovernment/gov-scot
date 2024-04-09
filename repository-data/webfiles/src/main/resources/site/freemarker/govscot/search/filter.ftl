<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="gov_filters" data-module="gov-filters">
    <h2>Publication types</h2>
    <ul>
        <#list publicationTypes as item>
            <li>${item.label}</li>
        </#list>
    </ul>

    <h2>Topics</h2>
    <ul>
        <#list topics as item>
            <li>${item.title}</li>
        </#list>
    </ul>
</div>

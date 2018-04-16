<#include "../../include/imports.ftl">

<#-- @ftlvariable name="publicationTypes" type="org.onehippo.forge.selection.hst.contentbean.ValueList" -->
<#-- @ftlvariable name="type" type="org.onehippo.forge.selection.hst.contentbean.ValueListItem" -->
<#-- @ftlvariable name="topic" type="scot.gov.www.beans.Topic" -->
<form id="filters" action="#" method="POST">
    <#if topics??>
        <h3>Topics</h3>
        <#list topics as topic>
            <input id="${topic.canonicalPath}" name="topics[]" class="fancy-checkbox checkbox-group__input" type="checkbox" value="${topic.title}">
            <label for="${topic.title}" class="checkbox-group__label fancy-checkbox">${topic.title}</label>
        </#list>
    </#if>

    <#if publicationTypes??>
        <h3>Publication Types</h3>
            <#list publicationTypes.items as type>
                <input id="${type.key}" name="types[]" class="fancy-checkbox checkbox-group__input" type="checkbox" value="${type.label}">
                <label for="${type.key}" class="checkbox-group__label fancy-checkbox">${type.label}</label>
            </#list>
    </#if>
</form>

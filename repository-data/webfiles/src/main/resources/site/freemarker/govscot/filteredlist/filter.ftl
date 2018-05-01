<#include "../../include/imports.ftl">

<#-- @ftlvariable name="publicationTypes" type="org.onehippo.taxonomy.api.Taxonomy" -->
<#-- @ftlvariable name="publicationType" type="org.onehippo.taxonomy.api.Category" -->
<#-- @ftlvariable name="category" type="org.onehippo.taxonomy.api.Category" -->
<#-- @ftlvariable name="locale" type="java.util.Locale" -->
<form id="filters" action="#" method="POST">
    <#if topics??>
        <button type="button" class="expandable-item__header js-toggle-expand" tabindex="0">
            <h4 class="expandable-item__title">Topics</h4>
            <span class="expandable-item__icon"></span>
        </button>
        <#list topics as topic>
            <input id="${topic.canonicalPath}" name="topics[]" class="fancy-checkbox checkbox-group__input" type="checkbox" value="${topic.title}">
            <label for="${topic.title}" class="checkbox-group__label fancy-checkbox">${topic.title}</label>
        </#list>
    </#if>

    <#if publicationTypes??>
        <button type="button" class="expandable-item__header js-toggle-expand" tabindex="0">
            <h4 class="expandable-item__title">Type</h4>
            <span class="expandable-item__icon"></span>
        </button>
            <div class="checkbox-group">
                <#list publicationTypes.categories as publicationType>
                    <h5 class="checkbox-group__title">${publicationType.getInfo(locale).name}</h5>
                    <#list publicationType.children as category>
                        <input id="${category.key}" name="publicationTypes[]" class="checkbox-group__input" type="checkbox" value="${category.key}"/>
                        <label for="${category.key}" class="checkbox-group__label fancy-checkbox">${category.getInfo(locale).name}</label>
                    </#list>

                </#list>
            </div>
    </#if>
</form>
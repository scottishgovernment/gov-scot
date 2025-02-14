<#ftl output_format="HTML">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#include "./macros/lang-attributes.ftl">

<#if collections?has_content || document.featuredLinks?has_content>

    <div class="ds_inset-text">
        <div class="ds_inset-text__text">
        <#if collections?has_content>
            <h2 class="ds_h3">Part of</h2>
            <ul class="ds_no-bullets">
                <#list collections as collection>
                <li>
                    <a class="sg-meta__collection" href="<@hst.link hippobean=collection/>">
                        ${collection.title}
                    </a>
                </li>
                </#list>
        </#if>
        <#if document.featuredLinks?has_content>
            <#if document.featuredLinks.label?has_content>
                <#if collections?has_content>
            </ul>
                </#if>
            <h2 class="ds_h3">${document.featuredLinks.label}</h2>
            <ul class="ds_no-bullets">
            <#elseif !collections?has_content>
            <h2 class="ds_h3">Part of</h2>
            <ul class="ds_no-bullets">
            </#if>
            <#list document.featuredLinks.link as link>
                <li>
                    <a href="<@hst.link hippobean=link.link/>">
                        <#if link.customLinkText?has_content>
                            ${link.customLinkText}
                        <#else>
                            ${link.link.title}
                        </#if>
                    </a>
                </li>
            </#list>
        </#if>
            </ul>
        </div>
    </div>

</#if>

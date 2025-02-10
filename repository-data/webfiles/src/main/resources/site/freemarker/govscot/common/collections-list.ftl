<#ftl output_format="HTML">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#include "./macros/lang-attributes.ftl">

<#if collections?has_content || document.featuredLinks?has_content>

    <aside class="ds_callout  ds_step-navigation-top" aria-labelledby="">

        <#if collections?has_content>
            <h3>Part of</h3>

            <ul class="ds_no-bullets">
                <#list collections as collection>
                    <li>
                        <a class="sg-meta__collection" href="<@hst.link hippobean=collection/>">
                            <b>${collection.title}</b>
                        </a>
                    </li>
                </#list>
            </ul>
        </#if>

        <#if document.featuredLinks?has_content>
            <#if document.featuredLinks.label?has_content>
                <h3>
                    ${document.featuredLinks.label}
                </h3>
            </#if>

            <ul class="ds_no-bullets">
                <#list document.featuredLinks.link as link>
                    <li>
                        <a href="<@hst.link hippobean=link.link/>">
                            <#if link.customLinkText?has_content>
                                <b>${link.customLinkText}</b>
                            <#else>
                                <b>${link.link.title}</b>
                            </#if>
                        </a>
                    </li>
                </#list>
            </ul>
        </#if>
    </aside>

</#if>

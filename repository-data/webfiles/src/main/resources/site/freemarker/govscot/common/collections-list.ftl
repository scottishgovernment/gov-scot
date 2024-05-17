<#ftl output_format="HTML">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#include "./macros/lang-attributes.ftl">

<#if collections?has_content>

    <aside class="ds_callout  ds_step-navigation-top" aria-labelledby="">
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
    </aside>

</#if>

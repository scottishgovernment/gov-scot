<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<!--noindex-->
<#-- @ftlvariable name="breadcrumb" type="org.onehippo.forge.breadcrumb.om.Breadcrumb" -->
<nav aria-label="Breadcrumb">
    <ol class="ds_breadcrumbs">
        <li class="ds_breadcrumbs__item">
            <a class="ds_breadcrumbs__link" href="<@hst.link path='/'/>">
                Home
            </a>
        </li>
        <#if breadcrumb?? && breadcrumb.items??>
            <#list breadcrumb.items as item>
                <li class="ds_breadcrumbs__item">
                    <@hst.link var="link" link=item.link/>
                    <a class="ds_breadcrumbs__link" href="${link}">
                        ${item.title}
                    </a>
                </li>
            </#list>
        </#if>
    </ol>
</nav>
<!--endnoindex-->

<@hst.headContribution category="schema">
<#if breadcrumbs??>
<script type="application/ld+json">
    {
        "@context": "http://schema.org",
        "@type": "BreadcrumbList",
        "itemListElement": [
            <#list breadcrumbs as item>
            <@hst.link var="link" link=item.link/>
            {
                "@type": "ListItem",
                "position": ${item?index + 1},
                "item": {
                    "@id": "${link}",
                    "name": "${item.title?json_string}"
                }
            },
            </#list>
        ]
    }
</script>
</#if>
</@hst.headContribution>

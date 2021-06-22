<#include "../../include/imports.ftl">

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
                        ${item.title?html}
                    </a>
                </li>
            </#list>
        </#if>
    </ol>
</nav>

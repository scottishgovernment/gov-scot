<#include "../../include/imports.ftl">

<#-- @ftlvariable name="breadcrumb" type="org.onehippo.forge.breadcrumb.om.Breadcrumb" -->

    <ol role="navigation" class="breadcrumbs">
        <li class="breadcrumbs__item" itemprop="url">
            <a href="/">Home</a>&nbsp;
        </li>
<#if breadcrumb?? && breadcrumb.items??>
        <#list breadcrumb.items as item>
            <li class="breadcrumbs__item" itemprop="url">
                <@hst.link var="link" link=item.link/>
                <a href="${link}">${item.title?html}</a>&nbsp;
            </li>
        </#list>
</#if>
    </ol>
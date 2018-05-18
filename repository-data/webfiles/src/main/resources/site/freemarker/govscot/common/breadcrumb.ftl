<#include "../../include/imports.ftl">

<#-- @ftlvariable name="breadcrumb" type="org.onehippo.forge.breadcrumb.om.Breadcrumb" -->

    <ol role="navigation" class="breadcrumbs">
        <li class="breadcrumbs__item" itemprop="url">
            <a class="breadcrumbs__link" href="<@hst.link path='/'/>" data-gtm="bread-0">
                <span class="breadcrumbs__title" itemprop="title">Home</span><!--
            --></a>
        </li>
        <#if breadcrumb?? && breadcrumb.items??>
            <#list breadcrumb.items as item>
                <li class="breadcrumbs__item" itemprop="url">
                    <@hst.link var="link" link=item.link/>
                    <a class="breadcrumbs__link" data-gtm="bread-1" href="${link}">
                        <span class="breadcrumbs__title" itemprop="title">${item.title?html}</span><!--
                 --></a>
                </li>
            </#list>
        </#if>
    </ol>
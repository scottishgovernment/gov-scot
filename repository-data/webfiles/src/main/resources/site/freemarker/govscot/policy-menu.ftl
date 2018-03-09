<#include "../include/imports.ftl">

<h3>Policy in Detail</h3>
<nav class="page-group">
    <!-- nav links -->
    <ul class="page-group__list">
        <#list policypages as policypage>
            <li class="page-group__item page-group__item--level-1">
                <@hst.link var="link" hippobean=policypage/>
                <a class="page-group__link page-group__link--level-1" href="${link}">
                    <span class="page-group__text">${policypage.title}</span>
                </a>
            </li>
        </#list>
    </ul>
</nav>

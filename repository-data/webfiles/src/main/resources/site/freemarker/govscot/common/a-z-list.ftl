<#ftl output_format="HTML">
<ul class="gov_az-list">
    <#list beansByLetter as letter>
        <li class="gov_az-list__item" id="gov_az-list__${letter.label}">
            <h2 class="gov_az-list__letter">${letter.label}</h2>

            <ul class="gov_az-list__list">
                <#list letter.beans as bean>
                    <li class="gov_az-list__list__item">
                        <@hst.link var="link" hippobean=bean/>
                        <a class="gov_az-list__link" href="${link}">${bean.title}<#if !bean.active><span class="visually-hidden"> (inactive)</span></#if></a>
                        <#if !bean.active><strong class="ds_tag  gov_tag--secondary  gov_tag--small" aria-hidden="true"> Inactive</strong></#if>
                    </li>
                </#list>
            </ul>
        </li>
    </#list>
</ul>

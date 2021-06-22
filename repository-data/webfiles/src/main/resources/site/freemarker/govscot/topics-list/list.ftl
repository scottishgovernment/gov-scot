<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<ul class="gov_az-list">
    <#list topicsByLetter as letter>
        <li class="gov_az-list__item" id="gov_az-list__${letter.letter}">
            <h2 class="gov_az-list__letter">${letter.letter}</h2>

            <ul class="gov_az-list__list">
                <#list letter.topics as bean>
                    <li class="gov_az-list__list__item">
                        <@hst.link var="link" hippobean=bean/>
                        <a class="gov_az-list__link" href="${link}">${bean.title}</a>
                    </li>
                </#list>
            </ul>
        </li>
    </#list>
</ul>

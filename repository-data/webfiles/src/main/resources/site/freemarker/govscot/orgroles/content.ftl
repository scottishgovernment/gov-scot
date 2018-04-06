<#include "../../include/imports.ftl">

<div class="body-content">
    <div class="page-group__content body-content inner-shadow-top inner-shadow-top--no-desktop">

        <h1>ORG ROLES PAGE</h1>
        <h1 class="article-header">${document.title}</h1>

        <div class="body-content  leader--first-para">
        <@hst.html hippohtml=document.content/>
        </div>

    <h2>Primary</h2>
    <#list primaryPeople as person>
        <p>
        ${person.title}

        <ul>
            <#list person.roles as role>
                <li>
                ${role.title}
                </li>
            </#list>
        </ul>
        </p>
    </#list>

    <h2>Secondary</h2>
    <#list secondaryPeople as person>
        <p>
            ${person.title}

            <ul>
            <#list person.roles as role>
                <li>
                    ${role.title}
                </li>
            </#list>
            </ul>
        </p>
    </#list>

    </div>

</div>
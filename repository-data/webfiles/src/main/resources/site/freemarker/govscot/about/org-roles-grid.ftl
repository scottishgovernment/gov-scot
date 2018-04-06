<div id="${orgName?lower_case?replace(' ', '-', 'r')}" class="person-grid">
    <div class="person-grid__intro">
        <h2 class="no-top-margin">${orgName}</h2>

        <#if orgDescription??>
            <p>${orgDescription}</p>
        </#if>
    </div>

    <div class="grid"><!--
        --><div class="grid__item medium--eight-ninths large--one-whole">
            <ul class="grid  person-grid__people overflow--large--two-twelfths overflow--xlarge--two-twelfths"><!--

                <#list orgRoles as orgRole>
                    <@hst.link var="link" hippobean=orgRole/>
                    --><li class="grid__item medium--six-twelfths large--four-twelfths">
<#--  <ul>
<#list orgRole?keys as prop>
<li>${prop} = ${orgRole[prop]}</li>
</#list>
</ul>  -->
                <div class=person>
                    <a class="person__link" href="${link}">
                        <div class="person__image-container">
                            <img class="person__image" src="/site/assets/images/people/placeholder.png" alt="" />
                        </div>
                    </a>

                    <div class="person__text-container">
                        <h3 class="person__name person__name--link">${orgRole.incumbent.title}</h3>

                        <p class="person__roles">
                            <#if orgRole.title??>
                                <a class="person__role-link" href="${link}">${orgRole.title}</a>
                            <#else>
                                <pre>ROLE DESCRIPTION</pre>${orgRole.incumbent.roleDescription}
                            </#if>
                        </p>

                        <@hst.html hippohtml=orgRole.responsibilities var="responsibilities"/>
                        <#if responsibilities?has_content>
                            <div class="person__responsibilities">
                                <button class="link expand  person__responsibilities-toggle"
                                    data-target-selector="#${orgRole.canonicalUUID}-responsibilities"
                                    title="Show responsibilities">
                                    <span class="expand__icon"></span>
                                </button>

                                <h4 class="person__responsibilities-title">Responsibilities</h4>

                                <div id="${orgRole.canonicalUUID}-responsibilities">
                                ${responsibilities}

                                <#--  <ul class="person__responsibilities-list no-bullets">
                                    {{#responsibilities}}
                                        <li>
                                            <a class="person__role-link" href="">{{title}}</a>
                                        </li>
                                    {{/responsibilities}}
                                </ul>  -->
                                </div>

                            </div>
                        </#if>
                    </div>
                </div>
            </li><!--
                </#list>
            --></ul>
        </div><!--
    --></div>

</div>

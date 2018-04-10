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

                <#list people as person>
                <#if person.roles??>
                    <@hst.link var="link" hippobean=person.roles[0]/>
                <#else>
                    <@hst.link var="link" hippobean=person/>
                </#if>
                    
                    --><li class="grid__item medium--six-twelfths large--four-twelfths">

                        <div class=person>
                            <a class="person__link" href="${link}">
                                <div class="person__image-container">
                                    <img class="person__image" src="/site/assets/images/people/placeholder.png" alt="" />
                                </div>
                            </a>

                            <div class="person__text-container">
                                <h3 class="person__name person__name--link">${person.title}</h3>

                                <p class="person__roles">
                                    <#if person.roles??>
                                        <#list person.roles as role>
                                            <@hst.link var="rolelink" hippobean=role/>
                                            <#if !role?is_first>and</#if> <a class="person__role-link" href="${rolelink}">${role.title}</a>
                                        </#list>
                                    <#else>
                                        ${person.roleTitle}
                                    </#if>
                                </p>

                                <#assign hasDirectorates=false/>
                                <#if person.roles??>
                                    <#list person.roles as role>
                                        <#list role.directorates as directorate>
                                            <#assign hasDirectorates=true/>
                                        </#list>
                                    </#list>
                                </#if>

                                <@hst.link var="documentlink" hippobean=document/>
                                <#if documentlink?contains("civil-service") && hasDirectorates>
                                    <div class="person__responsibilities">
                                        <button class="link expand  person__responsibilities-toggle"
                                            data-target-selector="#${person.canonicalUUID}-responsibilities"
                                            title="Show responsibilities">
                                            <span class="expand__icon"></span>
                                        </button>

                                        <h4 class="person__responsibilities-title">Responsibilities</h4>

                                        <div id="${person.canonicalUUID}-responsibilities">
                                            <#if person.roles??>
                                                <ul class="person__responsibilities-list no-bullets">
                                                    <#list person.roles as role>
                                                        <#list role.directorates as directorate>
                                                            <li>
                                                                <@hst.link var="directoratelink" hippobean=directorate/>
                                                                <a href="${directoratelink}">${directorate.title}</a>
                                                            </li>
                                                        </#list>
                                                        <#-- end role.directorates loop -->
                                                    </#list>
                                                    <#-- end person.roles loop -->
                                                </ul>
                                            </#if>
                                            <#-- end person.roles condition -->
                                        </div>
                                    </div>
                                </#if>
                                <#-- end url condition -->
                            </div>
                        </div>
                    </li><!--
                </#list>
                <#-- end people loop -->
            --></ul>
        </div><!--
    --></div>

</div>

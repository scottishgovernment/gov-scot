<#function strSlug string>
  <#local string = string?lower_case />
  <#local string = string?replace("[ \t\n\x0B\f\r]+", "-", "r") />
  <#local string = string?replace("[^-a-z0-9]+", "-", "r") />
  <#local string = string?replace("(-)+", "-", "r") />

  <#return string />
</#function>

<div id="${orgName?lower_case?replace(' ', '-', 'r')}">

    <h2 class="ds_no-margin--top">${orgName}</h2>

    <#if orgDescription??>
        <p>${orgDescription}</p>
    </#if>

    <div class="overflow--large--two-twelfths  overflow--xlarge--two-twelfths">
        <ul class="gov_person-grid">
            <#list people as person>
                <#if person.roles??>
                    <@hst.link var="link" hippobean=person.roles[0]/>
                <#else>
                    <@hst.link var="link" hippobean=person/>
                </#if>

                <li class="gov_person-grid__item">
                    <div class="gov_person  gov_person--flex">
                        <div class="gov_person__image-container">
                            <a class="gov_person__link" href="${link}">
                                <#if person.image?? && person.image.xlargethreecolumnssquare??>
                                    <img alt="${person.title}" class="gov_person__image"
                                        width="${person.image.xlargethreecolumnsdoubledsquare.width?c}"
                                        height="${person.image.xlargethreecolumnsdoubledsquare.height?c}"
                                        loading="lazy"
                                        src="<@hst.link hippobean=person.image.xlargethreecolumnssquare/>"
                                        srcset="<@hst.link hippobean=person.image.mediumfourcolumnssquare/> 224w,
                                            <@hst.link hippobean=person.image.mediumfourcolumnsdoubledsquare/> 448w,
                                            <@hst.link hippobean=person.image.largethreecolumnssquare/> 208w,
                                            <@hst.link hippobean=person.image.largethreecolumnsdoubledsquare/> 416w,
                                            <@hst.link hippobean=person.image.xlargethreecolumnssquare/> 256w,
                                            <@hst.link hippobean=person.image.xlargethreecolumnsdoubledsquare/> 512w"
                                        sizes="(min-width:1200px) 256px, (min-width:992px) 208px, 224px" />
                                <#else>
                                    <img width="300" height="300" loading="lazy" class="gov_person__image" src="<@hst.link path='/assets/images/people/placeholder.png'/>" alt="${person.title}">
                                </#if>
                            </a>
                        </div>

                        <div class="gov_person__text-container">
                            <h3 class="gov_person__name">${person.title}</h3>

                            <p class="gov_person__roles">
                                <#if person.roles??>
                                    <#list person.roles as role>
                                        <@hst.link var="rolelink" hippobean=role/>
                                        <#if !role?is_first>and</#if> <a class="gov_person__role-link" href="${rolelink}">${role.title}</a>
                                    </#list>
                                <#else>
                                    ${person.roleTitle}
                                </#if>
                            </p>

                            <#assign hasDirectorates=false/>
                            <#if person.roles??>
                                <#list person.roles as role>
                                    <#if role.directorates??>
                                        <#list role.directorates as directorate>
                                            <#assign hasDirectorates=true/>
                                        </#list>
                                    </#if>
                                </#list>
                            </#if>

                            <@hst.link var="documentlink" hippobean=document/>
                            <#if documentlink?contains("civil-service") && hasDirectorates>
                                <div class="gov_person__responsibilities">
                                    <#assign responsibilities = []/>
                                    <#list person.roles as role>
                                        <#list role.directorates as directorate>
                                            <#assign responsibilities = responsibilities + [directorate]/>
                                        </#list>
                                        <#-- end role.directorates loop -->
                                    </#list>

                                    <div class="ds_accordion" data-module="ds-accordion" data-name="${strSlug(person.title)}">
                                        <div class="ds_accordion-item  ds_accordion-item--small">
                                            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-${person.canonicalUUID}" aria-labelledby="panel-${person.canonicalUUID}-heading" />
                                            <div class="ds_accordion-item__header">
                                                <h3 id="panel-${person.canonicalUUID}-heading" class="ds_accordion-item__title">
                                                    Responsibilities (${responsibilities?size})
                                                </h3>
                                                <span class="ds_accordion-item__indicator"></span>
                                                <label class="ds_accordion-item__label" for="panel-${person.canonicalUUID}"><span class="visually-hidden">Show this section</span></label>
                                            </div>
                                            <div class="ds_accordion-item__body">
                                                <#if person.roles??>
                                                    <ul class="gov_person__responsibilities-list  ds_no-bullets">
                                                        <#list responsibilities as directorate>
                                                            <li>
                                                                <@hst.link var="directoratelink" hippobean=directorate/>
                                                                <a href="${directoratelink}">${directorate.title}</a>
                                                            </li>
                                                        </#list>
                                                        <#-- end directorates loop -->
                                                    </ul>
                                                </#if>
                                                <#-- end person.roles condition -->
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </#if>
                            <#-- end url condition -->
                        </div>
                    </div>
                </li>
            </#list>
        </ul>
    </div>
</div>

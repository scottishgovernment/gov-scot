<div class="az-list">
    <#list beansByLetter as letter>
        <div class="grid">
            <div class="grid__item two-twelfths medium--one-ninth az-list__chunkName" id="az-list__A-C">${letter.label}</div>
            <div class="grid__item ten-twelfths medium--seven-ninth az-list__chunk">
                <ul class="az-list__list grid">
                    <#list letter.beans as directorate>
                        <li class="az-list__item grid__item ">
                            <@hst.link var="link" hippobean=directorate/>
                            <a class="az-list__link" href="${link}">${directorate.title}</a>
                        </li>
                    </#list>
                </ul>
            </div>
        </div>
    </#list>
</div>

<#ftl output_format="HTML">
<#include "../../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="az-list">
    <#list topicsByLetter as letter>
      <div class="grid"><!--
        --><div class="grid__item two-twelfths medium--one-ninth az-list__chunkName" id="az-list__${letter.letter}">${letter.letter}</div><!--
        --><div class="grid__item ten-twelfths medium--seven-ninths az-list__chunk">
          <ul class="az-list__list grid"><!--
            <#list letter.topics as topic>
              --><li class="az-list__item grid__item ">
                    <a class="az-list__link" href="<@hst.link hippobean=topic />">${topic.title}</a>
              </li><!--
            </#list>
            <#-- end letter.topics loop -->
          --></ul>
        </div><!--
      --></div>
    </#list>
    <#-- end topicsByLetter loop -->
</div>

<#include "../include/imports.ftl">
<@hst.link var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<div class="grid" id="page-content"><!--
  --><div class="grid__item medium--eight-twelfths">
    <h1 class="article-header">Topics</h1>
  </div><!--
--></div>

<#if document??>
  <#if document.content>
    <div id="topics-intro">
      <@hst.html hippohtml=document.content />
    </div>
  </#if>
</#if>

<section class="topics">
  <div class="az-list">
    <#list topicsByLetter as letter>
      <div class="grid">
        <div class="grid__item two-twelfths medium--one-ninth az-list__chunkName" id="az-list__A">${letter.letter}</div>
        <div class="grid__item ten-twelfths medium--seven-ninth az-list__chunk">
          <ul class="az-list__list grid">
            <#list letter.topics as topic>
              <li class="az-list__item grid__item ">
                <div class="topic">
                  <h2 class="gamma topic__title">
                    <a href="<@hst.link hippobean=topic />">${topic.title}</a>
                  </h2>

                  <p>See all related:</p>

                  <div class="topic__buttons">
                    <a class="button button--xsmall button--primary button--pill button--margin-right" href="/policies/?topics=${topic.title}">
                        <svg class="svg-icon  mg-icon  mg-icon--absolute  mg-icon--medium  mg-icon--right">
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="${iconspath}#chevron-right"></use>
                        </svg>
                        Policies
                    </a>
                    <a class="button button--xsmall button--primary button--pill button--margin-right" href="/news/?topics=${topic.title}">
                        <svg class="svg-icon  mg-icon  mg-icon--absolute  mg-icon--medium  mg-icon--right">
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="${iconspath}#chevron-right"></use>
                        </svg>
                        News
                    </a>
                    <a class="button button--xsmall button--primary button--pill button--margin-right" href="/publications/?topics=${topic.title}">
                        <svg class="svg-icon  mg-icon  mg-icon--absolute  mg-icon--medium  mg-icon--right">
                            <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="${iconspath}#chevron-right"></use>
                        </svg>
                        Publications
                    </a>
                  </div>
                </div>
              </li>
            </#list>
            <#-- end letter.topics loop -->
          </ul>
        </div>
      </div>
    </#list>
    <#-- end topicsByLetter loop -->
  </div>
</section>

<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if collections?has_content>
    <#if collections?size == 1>
        <#assign description = 'a collection'/>
    <#else>
        <#assign description = '${collections?size} collections'/>
    </#if>

    <div class="expandable collection-expandable">
        <div class="expandable-item">
            <button class="expandable-item__header  js-toggle-expand" role="tab" id="collections-heading" data-toggle="collapse" aria-expanded="false" aria-controls="collections-body">
                <h3 class="expandable-item__title  gamma">
                    <span class="link-text">This document is part of ${description}</span>
                </h3>
                <span class="expandable-item__icon">
                    <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-more">
                        <use xlink:href="${iconspath}#sharp-expand_more-24px"></use>
                    </svg>
                    <svg class="svg-icon  mg-icon  mg-icon--full  optional-icon  icon-less">
                        <use xlink:href="${iconspath}#sharp-expand_less-24px"></use>
                    </svg>
                </span>
            </button>

            <div id="collections-body" class="expandable-item__body" role="tabpanel" aria-expanded="false" aria-labelledby=collections-heading">
                <ul class="contents-list">
                    <#list collections as collection>

                        <li class="contents-list__item">
                            <a class="contents-list__link" href="<@hst.link hippobean=collection/>">${collection.title}</a>
                        </li>

                    </#list>
                </ul>
            </div>
        </div>
    </div>
</#if>
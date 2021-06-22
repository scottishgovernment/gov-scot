<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if collections?has_content>
    <#if collections?size == 1>
        <#assign description = 'a collection'/>
    <#else>
        <#assign description = '${collections?size} collections'/>
    </#if>

    <div class="ds_accordion" data-module="ds-accordion">
        <div class="ds_accordion-item">
            <input type="checkbox" class="visually-hidden  ds_accordion-item__control" id="panel-collections" aria-labelledby="panel-collections-heading" />
            <div class="ds_accordion-item__header">
                <h3 id="panel-collections-heading" class="ds_accordion-item__title">
                    <img src="../../../../assets/images/icons/collection_@2x.png" style="
                        width: 32px;vertical-align: middle;margin-right: 16px;
                    ">
                    This document is part of ${description}
                </h3>
                <span class="ds_accordion-item__indicator"></span>
                <label class="ds_accordion-item__label" for="panel-collections"><span class="visually-hidden">Show this section</span></label>
            </div>
            <div class="ds_accordion-item__body">
                <ul class="no-bullets">
                    <#list collections as collection>

                        <li>
                            <a class="contents-list__link" href="<@hst.link hippobean=collection/>">${collection.title}</a>
                        </li>

                    </#list>
                </ul>
            </div>
        </div>
    </div>
</#if>

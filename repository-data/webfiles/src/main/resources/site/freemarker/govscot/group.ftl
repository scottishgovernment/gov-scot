<#include "../include/imports.ftl">

<#if document??>
    <article id="page-content">

        <div class="grid"><!--

        --><div class="grid__item medium--nine-twelfths large--seven-twelfths">

                <div class="article-header">
                    <h1>${document.title}</h1>
                </div>

                <h2>Role of the group</h2>
                <@hst.html hippohtml=document.content/>

                {{#relatedItemsByFormats "GROUP"}}
                {{#if $first}}<h2>Related groups</h2>{{/if}}

                {{#if $first}}<ul>{{/if}}
                <li>
                    <a href="{{url}}">{{title}}</a>
                </li>
                {{#if $last}}</ul>{{/if}}
                {{/relatedItemsByFormats}}


                {{#if contentItem.additionalContent}}
                <h2>Members</h2>
                {{#markdown}}{{contentItem.additionalContent}}{{/markdown}}
                {{/if}}
                {{#relatedItemsByFormats
                "APS_PUBLICATION"
                "publications-non-aps"
                }}
                {{#if $first}}<h2>Documents</h2>{{/if}}

                {{#if $first}}<ul>{{/if}}
                <li>
                    <a href="{{url}}">{{title}}</a>
                </li>
                {{#if $last}}</ul>{{/if}}
                {{/relatedItemsByFormats}}

            </div><!--

         --><div class="grid__item medium--nine-twelfths large--three-twelfths push--large--two-twelfths">
                <aside>
                    <div class="sidebar-block">
                        {{#relatedItemsByFormats "POLICY"}}
                        {{#if $first}}<h3 class="emphasis sidebar-block__heading">Related policies</h3>{{/if}}

                        {{#if $first}}<ul class="sidebar-block__list no-bullets">{{/if}}
                        <li class="sidebar-block__list-item">
                            <a href="{{url}}">{{title}}</a>
                        </li>
                        {{#if $last}}</ul>{{/if}}
                        {{/relatedItemsByFormats}}
                    </div>

                    {{#if contentItem.contactDetails}}
                    <div class="sidebar-block">
                        <h3 class="emphasis sidebar-block__heading">Contacts</h3>
                        {{#markdown}}{{contentItem.contactDetails}}{{/markdown}}
                    </div>
                    {{/if}}
                </aside>
            </div><!--
     --></div>

    </article>

<#-- @ftlvariable name="editMode" type="java.lang.Boolean"-->
<#elseif editMode>
    <div>
        <img src="<@hst.link path="/images/essentials/catalog-component-icons/simple-content.png" />"> Click to edit Content
    </div>
</#if>
<#ftl output_format="HTML">
<#include "../../include/imports.ftl">

<div class="ds_wrapper">
<div class="ds_cb__inner">

<#if policies?has_content>
    <section class="gov_content-block" id="related-policies">
        <h2 class="gov_content-block__title  gamma">Policies</h2>
        <ul class="ds_no-bullets">
            <#list policies as policy>
                <li><a data-navigation="policy-${policy?index + 1}" href="<@hst.link hippobean=policy/>">${policy.title}</a></li>
            </#list>
        </ul>

        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/policies/?topics=' + document.title/>" data-navigation="policies-all">
            <span class="gov_icon-link__text">See all policies <span class="visually-hidden">about ${document.title}</span></span>
            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
        </a>
    </section>
</#if>

<#if publications?has_content>
    <section class="gov_content-block" id="related-publications">
        <h2 class="gov_content-block__title  gamma">Publications</h2>
        <ul class="ds_no-bullets">
            <#list publications as publication>
                <li><a data-navigation="publications-${publication?index + 1}" href="<@hst.link hippobean=publication/>">${publication.title}</a></li>
            </#list>
        </ul>

        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/publications/?topics=' + document.title/>" data-navigation="publications-all">
            <span class="gov_icon-link__text">See all publications <span class="visually-hidden">about ${document.title}</span></span>
            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
        </a>
    </section>
</#if>

<#if news?has_content>
    <section class="gov_content-block" id="related-news">
        <h2 class="gov_content-block__title  gamma">News</h2>
        <ul class="ds_no-bullets">
            <#list news as newsItem>
                <li><a data-navigation="news-${newsItem?index + 1}" href="<@hst.link hippobean=newsItem/>">${newsItem.title}</a></li>
            </#list>
        </ul>

        <a class="gov_icon-link  gov_icon-link--major" href="<@hst.link path='/news/?topics=' + document.title/>" data-navigation="news-all">
            <span class="gov_icon-link__text">See all news <span class="visually-hidden">about ${document.title}</span></span>
            <span class="gov_icon-link__icon  gov_icon-link__icon--chevron" aria-hidden="true"></span>
        </a>
    </section>
</#if>

</div>
</div>
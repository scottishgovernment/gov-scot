<#include "../include/imports.ftl">

<h1 class="article-header">${document.title}</h1>

<div class="body-content  leader--first-para">
    <div class="grid__item  medium--four-twelfths">
        <section id="latest-publications" class="topic-block">
            <h2 class="emphasis  topic-block__title">
                Publications
            </h2>

            <div id="publications-container">
            <#if publications?has_content>
                <#list publications as publication>
                    <article class="homepage-publication">
                        <h3 class="js-truncate  homepage-publication__title">
                            <a href="<@hst.link hippobean=publication />" data-gtm="publications-${publication?index + 1}" title="${publication.title}">${publication.title}</a>
                        </h3>
                        <p class="homepage-publication__date"><@fmt.formatDate value=publication.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                    </article>
                </#list>
            <#else>
                <p>There are no publications relating to this topic.</p>
            </#if>
            </div>

        </section>
    </div>
</div>

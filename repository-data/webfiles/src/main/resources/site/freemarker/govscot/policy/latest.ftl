<#include "../../include/imports.ftl">

<div class="body-content page-group__content inner-shadow-top inner-shadow-top--no-desktop">

    <@hst.html hippohtml=document.content/>

    <section class="search-results">
        <ol id="search-results-list" class="search-results__list no-top-margin">
            <#list latest as item>
                <li class="search-results__item listed-content-item">
                    <@hst.link var="link" hippobean=item/>
                    <article class="listed-content-item__article <#if item == latest?last>listed-content-item__article--no-border</#if>">
                        <header class="listed-content-item__header">

                            <#if item.label == "news">
                                <div class="listed-content-item__meta">
                                    <p class="listed-content-item__label">NEWS</p>
                                    <p class="listed-content-item__date"><@fmt.formatDate value=item.publicationDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
                                </div>
                            <#else>
                                <div class="listed-content-item__meta listed-content-item__meta--has-icon">
                                    <span class="listed-content-item__icon file-icon file-icon--TXT"></span>
                                    <p class="listed-content-item__label">${item.label}</p>
                                    <p class="listed-content-item__date">
                                        <#if item.publicationDate??>
                                            <@fmt.formatDate value=item.publicationDate.time type="both" pattern="dd MMM yyyy"/>
                                        </#if>
                                    </p>
                                </div>
                            </#if>

                            <h2 class="gamma listed-content-item__title">
                                <a href="${link}" class="listed-content-item__link" title="${item.title}" data-gtm="search-pos-${latest?seq_index_of(item) + 1}">
                                    ${item.title}
                                </a>
                            </h2>
                        </header>

                        <p class="listed-content-item__summary">${item.summary}</p>
                    </article>
                </li>
            </#list>

        </ol>
    </section>

</div>

<nav class="ds_sequential-nav" aria-label="Article navigation">
    <#if prev??>
        <@hst.link var="link" hippobean=prev/>
        <div class="ds_sequential-nav__item  ds_sequential-nav__item--prev">
            <a title="Previous section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--left">
                <span class="ds_sequential-nav__text" data-label="previous">
                    ${prev.title}
                </span>
            </a>
        </div>
    </#if>

    <#if next??>
        <@hst.link var="link" hippobean=next/>
        <div class="ds_sequential-nav__item  ds_sequential-nav__item--next">
            <a title="Next section" href="${link}" class="ds_sequential-nav__button  ds_sequential-nav__button--right">
                <span class="ds_sequential-nav__text" data-label="next">
                    ${next.title}
                </span>
            </a>
        </div>
    </#if>
</nav>

<#include "../../include/imports.ftl">

<div class="body-content page-group__content inner-shadow-top inner-shadow-top--no-desktop">

	<section class="search-results">
		<ol id="search-results-list" class="search-results__list no-top-margin">
			<#list latest as item>
				<li class="search-results__item listed-content-item">
					<@hst.link var="link" hippobean=item/>
				    <a href="${link}" class="listed-content-item__link" title="${item.title}" data-gtm="search-pos-${latest?seq_index_of(item)}">
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
                                        <p class="listed-content-item__date"><@fmt.formatDate value=item.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                                    </div>
								</#if>

				                <h3 class="gamma listed-content-item__title">${item.title}</h3>
				            </header>

				            <p class="listed-content-item__summary">${item.summary}</p>
				        </article>
				    </a>
				</li>
			</#list>

		</ol>
	</section>

</div>

<nav class="multipage-nav visible-xsmall">
    <div class="grid"><!--
               -->
        <div class="grid__item small--six-twelfths push--small--six-twelfths">
		<#if next??>
            <div class="multipage-nav__container">
				<@hst.link var="link" hippobean=next/>
                <a class="multipage-nav__link multipage-nav__link--next" href="${link}">
				${next.title} <span
                        class="multipage-nav__icon multipage-nav__icon--right fa fa-chevron-right fa-2x"></span></a>
            </div>
		</#if>
        </div><!--

                         -->
        <div class="grid__item small--six-twelfths pull--small--six-twelfths">
		<#if prev??>
            <div class="multipage-nav__container">
				<@hst.link var="link" hippobean=prev/>
                <a class="multipage-nav__link multipage-nav__link--previous" href="${link}"><span
                        class="fa fa-2x fa-chevron-left multipage-nav__icon multipage-nav__icon--left"></span>
				${prev.title} </a>
            </div>
		</#if>
        </div><!--
     --></div>
</nav>

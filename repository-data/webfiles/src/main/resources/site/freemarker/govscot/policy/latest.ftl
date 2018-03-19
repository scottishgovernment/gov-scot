<#include "../../include/imports.ftl">

<div class="body-content page-group__content body-content inner-shadow-top inner-shadow-top--no-desktop">

	<section class="search-results">
		<ol id="search-results-list" class="search-results__list no-top-margin">

			<#list latest as item>
				<li class="search-results__item listed-content-item">
					<@hst.link var="link" hippobean=item/>
				    <a href="${link}" class="listed-content-item__link" title="${item.title}" data-gtm="search-pos-${latest?seq_index_of(item)}">
				        <article class="listed-content-item__article <#if item == latest?last>listed-content-item__article--no-border</#if>">
				            <header class="listed-content-item__header">

				                <#if item.class == "class scot.gov.www.beans.News">
				                    <div class="listed-content-item__meta">
				                        <p class="listed-content-item__label">NEWS</p>
				                        <p class="listed-content-item__date"><@fmt.formatDate value=item.publishedDate.time type="both" pattern="dd MMM yyyy HH:mm"/></p>
				                    </div>
				                <#else>
				                    <div class="listed-content-item__meta listed-content-item__meta--has-icon">
				                        <span class="listed-content-item__icon file-icon file-icon--TXT"></span>
				                        <p class="listed-content-item__label">PUBLICATION</p>
				                        <p class="listed-content-item__date"><@fmt.formatDate value=item.publishedDate.time type="both" pattern="dd MMM yyyy"/></p>
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

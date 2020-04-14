<#include "../include/imports.ftl">

<h1 class="article-header">${document.title}</h1>

<div class="body-content  leader--first-para">
    <#if document.summary?has_content>
            <p>${document.summary}</p>
    </#if>
</div>
    
<header class="topic-header  <#if document.image??>topic-header--has-image</#if>" id="page-content">
        <#if document.image??>
            <img alt="" src="<@hst.link hippobean=document.image.bannerdesktop/>" class="topic-header__image">
        </#if>
</header>

<div class="body-content">
    
  <@hst.html hippohtml=document.content />

    <section id="latest-publications" class="topic-block">
                <h2 class="emphasis  topic-block__title">
                Statistics and research
                </h2>
        
        <div class="grid"><!--

        --><div class="grid__item  medium--six-twelfths"><!-- 

          --><div id="publications-container">
                <#if publications?has_content>
                    <#list publications as publication>
                        <article class="homepage-publication">
                            <h3 class="js-truncate  homepage-publication__title">
                                <a href="<@hst.link hippobean=publication />" data-gtm="publications-${publication?index}" title="${publication.title}">${publication.title}</a>
                            </h3>
                             <ul class="homepage-publication__topics">
                                    <li>${publication.label}</li>
                            </ul>
                            <p class="homepage-publication__date"><@fmt.formatDate value=publication.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                        </article>
                    </#list>
                <#else>
                    <p>There are no statistics and research relating to this topic.</p>
                </#if>
            </div>

            <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
            <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline" href="<@hst.link path='/statisitics-and-research/?topics=${document.title}'/>"
            data-gtm="all-pubs">
                <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                    <use xlink:href="${iconspath}#3x3grid"></use>
                </svg>
                See all Statistics and research
            </a>
</div>
</div>
    </section>

  </div>  


    <#if document.featuredItems?has_content>

        <section id="featured-items" class="topic-block">
            <h2 class="emphasis  topic-block__title">Featured</h2>

            <ul class="grid"><!--
                <#list document.featuredItems as item>
                --><li class="grid__item  medium--six-twelfths  listed-content-item  listed-content-item--dark  listed-content-item--small">
                        <article class="listed-content-item__article">
                            <#-- use news as default image -->
                            <#assign imgLabel = 'news'/>
                            <#if item.label == 'news'>
                                <#assign imgLabel = 'news'/>
                            <#elseif item.label?contains('Consultation')>
                                <#assign imgLabel = 'cons'/>
                            <#elseif item.publicationType??>
                                <#assign imgLabel = 'pubs'/>
                            </#if>
                            <#if imgLabel??>
                                <img src="<@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop.jpg'/>"
                                    srcset="<@hst.link path='/assets/images/graphics/featured-${imgLabel}-tablet.jpg'/>  220w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-tablet_@2x.jpg'/> 440w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop.jpg'/> 293w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-desktop_@2x.jpg'/> 586w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-hd.jpg'/> 360w,
                                    <@hst.link path='/assets/images/graphics/featured-${imgLabel}-hd_@2x.jpg'/> 720w"
                                    sizes="(min-width:1200px) 360px, (min-width:992px) 293px, (min-width:768px) 220px, 360px" alt="" class="listed-content-item__feature-image">
                            </#if>

                            <header class="listed-content-item__heading">
                                <#if item.label?has_content>
                                    <#assign date = (item.publicationDate.time)!item.properties['hippostdpubwf:lastModificationDate'].time />
                                    <div class="listed-content-item__meta">
                                        <div class="listed-content-item__meta-right">
                                            <p class="listed-content-item__date"><@fmt.formatDate value=date type="both" pattern="dd MMM yyyy"/></p>
                                        </div>
                                        <div class="listed-content-item__meta-left">
                                            <p class="listed-content-item__label  js-truncate" data-lines="1">${item.label}</p>
                                        </div>
                                    </div>
                                </#if>

                                <h3 class="listed-content-item__title  js-truncate" title="${item.title}">
                                    <a href="<@hst.link hippobean=item/>" title="${item.title}" class="listed-content-item__link" data-gtm="featured-item-${item?index + 1}">
                                        ${item.title}
                                    </a>
                                </h3>
                            </header>

                            <p class="listed-content-item__summary  hidden-small  hidden-xsmall  js-truncate" title="${item.summary}">
                                ${item.summary}
                            </p>
                        </article>
                    </li><!--
                </#list>
            --></ul>
        </section>
    </#if>




<div class="grid"><!--
    --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
        <#include 'common/feedback-wrapper.ftl'>
    </div><!--
--></div>
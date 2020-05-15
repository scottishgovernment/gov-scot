<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<h1 class="article-header">${document.title}</h1>

<div class="body-content  leader--first-para">
    <#if document.summary?has_content>
            <p>${document.summary}</p>
    </#if>
</div>

    <#if document.image??>
        <header class="topic-header  <#if document.image??>topic-header--has-image</#if>" id="page-content">
            <img alt="" src="<@hst.link hippobean=document.image.bannerdesktop/>" class="topic-header__image">
        </header>
    </#if>

<div class="body-content">
    <#if document.content?has_content>
        <@hst.html hippohtml=document.content />
    </#if>

   <#if document.additionalContent?has_content>
   <section id="additional-content" class="topic-block">
        <#list document.additionalContent as additionalContent>
            <h2 class="emphasis  topic-block__title">${additionalContent.title}</h2>
            <@hst.html hippohtml=additionalContent.body />
        </#list>
    </section>    
    </#if>
    
    <section id="latest-stats-research" class="topic-block">
                <h2 class="emphasis  topic-block__title">
                Statistics and research publications
                </h2>
        
        <div class="grid"><!-- 
        --><div class="grid__item  medium--six-twelfths"><!-- 

              --><div id="publications-container">
                <h3 class="filter-search__subtitle homepage-block__subtitle">Latest</h3>
                    <#if statisticsAndResearch?has_content>
                        <#list statisticsAndResearch as statsItem>
                            <article class="homepage-publication">
                        <h3 class="js-truncate homepage-news__title">
                            <a href="<@hst.link hippobean=statsItem/>" data-gtm="stats-${statsItem?index}" title="${statsItem.title}">${statsItem.title}</a>
                        </h3>
                            <ul class="homepage-publication__topics">
                                    <li>${statsItem.label}</li>
                            </ul>

                            <p class="homepage-publication__date"><@fmt.formatDate value=statsItem.publicationDate.time type="both" pattern="dd MMM yyyy"/></p>
                    </article>
                        </#list>
                        <#else>
                        <p>There are no statistics and research relating to this topic.</p>
                    </#if>
                 </div>

                <!-- if you're changing this link remember to also change the non-mobile equivalent below -->
                <a class="button  button--tertiary  visible-xsmall  visible-xsmall--inline tst-all-stats" href="<@hst.link path='/statistics-and-research/?topics=${document.title}'/>"
                data-gtm="all-stats">
                    <svg class="svg-icon  mg-icon  mg-icon--medium  mg-icon--inline">
                        <use xlink:href="${iconspath}#3x3grid"></use>
                    </svg>
                    See all Statistics and research
                </a>
        </div><!--
  
         --><div class="grid__item  medium--six-twelfths"><!-- 
             --><div id="publications-container">
                    <h3 class="filter-search__subtitle homepage-block__subtitle">Filter by type</h3>
                    <p>Look for either statistics or research that have been published by the Scottish Government.</p>          
                    <form>
                        <div class="checkbox-group">
                            <fieldset>
                                <legend class="hidden">Publication type</legend>
                    
                                        <input id="research" name="topics[]" class="fancy-checkbox checkbox-group__input" type="checkbox" value="research-and-analysis"/><label for="research" class="checkbox-group__label fancy-checkbox">Research and analysis</label>
                                        <input id="statistics" name="topics[]" class="fancy-checkbox checkbox-group__input" type="checkbox" value="statistics"/><label for="statistics" class="checkbox-group__label fancy-checkbox">Statistics</label>
                      
                            </fieldset>
                        </div>    
                        <button href="<@hst.link path='/statistics-and-research/'/>" class="js-stats-form-submit button button--primary homepage-block__button">Go</button>
                    </form>  
            </div><!--
    --></section><!--
--></div>  
  
<#if document.featuredItems?has_content><!--
    --><section id="featured-items" class="topic-block">
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
        </section><!--
--></#if><!--

        
 --><#if document.trailingContent?has_content>
        <section id="trailing-content" class="topic-block">
        <#list document.trailingContent as trailingContent>
            <h2 class="emphasis  topic-block__title">${trailingContent.title}</h2>
            <@hst.html hippohtml=trailingContent.body />
        </#list>
        </section>
    </#if><!--

--><div class="grid">
    <div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
        <#include 'common/feedback-wrapper.ftl'>
       </div>
</div><!--


--><@hst.headContribution category="footerScripts">
    <script type="module" src="<@hst.webfile path="/assets/scripts/aboutstats.js"/>"></script>
    </@hst.headContribution>
    <@hst.headContribution category="footerScripts">
    <script nomodule="true" src="<@hst.webfile path="/assets/scripts/aboutstats.es5.js"/>"></script>
    </@hst.headContribution>


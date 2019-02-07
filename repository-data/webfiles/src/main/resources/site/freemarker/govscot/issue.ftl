<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#if document??>

<div class="layout--issue">

    <@hst.manageContent hippobean=document />

    <header class="topic-header  <#if document.image??>topic-header--has-image</#if>" id="page-content">
        <#if document.image??>
            <img src="<@hst.link hippobean=document.image.bannerdesktop/>" class="topic-header__image">
        </#if>

        <h1 class="article-header  topic-header__title">${document.title}</h1>

        <#if document.featureDateTitle?has_content ||
        document.featureDate?has_content ||
        document.featureDateSummary?has_content >
        <aside class="issue-callout">
            <div class="issue-callout__inner">
                <#if document.featureDateTitle?has_content><h3 class="issue-callout__beta">${document.featureDateTitle}</h3></#if>
                <#if document.featureDate?has_content><div class="issue-callout__alpha"><@fmt.formatDate value=document.featureDate.time type="both" pattern="d MMM yyyy"/></div></#if>
                <#if document.featureDateSummary?has_content><div class="issue-callout__gamma">${document.featureDateSummary}</div></#if>
            </div>
        </aside>
        </#if>

    </header>

    <div class="body-content">
        <div class="grid"><!--
         --><div class="grid__item  medium--eight-twelfths">

                <div id="introduction">
                    <@hst.html hippohtml=document.content/>
                </div>

                <div id="overview">
                    <@hst.html hippohtml=document.overview/>
                </div>

            </div><!--

         --><div class="grid__item  medium--four-twelfths">
                <div <#if document.featureDateTitle?has_content ||
                    document.featureDate?has_content ||
                    document.featureDateSummary?has_content >class="displace-by-issue-callout"</#if>>
                    <#if document.featuredItems?has_content>
                        <section id="featured-items" class="issue-sidebar-block">

                            <h2 class="crossbar-header"><span class="crossbar-header__text"><#if document.featuredItemsTitle?has_content>${document.featuredItemsTitle}<#else>Featured</#if></span></h2>

                            <ul class="no-bullets">
                                <#list document.featuredItems as item>
                                    <li class="listed-content-item  listed-content-item--compact  listed-content-item--highlight">
                                        <a href="<#if item.class == 'scot.gov.www.beans.ExternalLink'>${item.url}<#else><@hst.link hippobean=item/></#if>" class="listed-content-item__link <#if item.class == 'scot.gov.www.beans.ExternalLink'>external  listed-content-item__link--external</#if>" title="${item.title}">
                                            <article class="listed-content-item__article ">
                                                <header class="listed-content-item__header">
                                                    <div class="listed-content-item__meta">
                                                        <#if item.label??><p class="listed-content-item__label">${item.label}</p></#if>

                                                        <#if item.label == 'news'>
                                                            <p class="listed-content-item__date"><@fmt.formatDate value=item.publicationDate.time type="both" pattern="d MMM yyyy HH:mm"/></p>
                                                        <#elseif item.class == 'scot.gov.www.beans.ExternalLink'>
                                                            <p class="listed-content-item__date">${document.url}</p>
                                                        <#else>
                                                            <p class="listed-content-item__date"><@fmt.formatDate value=item.publicationDate.time type="both" pattern="d MMM yyyy"/></p>
                                                        </#if>

                                                    </div>

                                                    <h3 class="gamma  listed-content-item__title" title="${item.title}">${item.title}</h3>
                                                </header>
                                            </article>
                                        </a>
                                    </li>
                                </#list>
                            </ul>
                        </section>
                    </#if>

                    <#if news?has_content>
                        <section class="issue-sidebar-block" id="related-news">
                            <h2 class="gamma  emphasis  issue-sidebar-block__title">News</h2>
                            <ul class="no-bullets">
                                <#list news as newsItem>
                                    <li><a class="issue-sidebar-block__link" href="<@hst.link hippobean=newsItem/>">${newsItem.title}</a></li>
                                </#list>
                            </ul>
                            <a href="<@hst.link path='/news/?topics=' + document.title/>" class="see-all-button  see-all-button--icon  see-all-button--icon-grid"><span></span> See all news</a>
                        </section>
                    </#if>

                    <#if policies?has_content>
                        <section class="issue-sidebar-block" id="related-policies">
                            <h2 class="gamma  emphasis  issue-sidebar-block__title">Policies</h2>
                            <ul class="no-bullets">
                                <#list policies as policy>
                                    <li><a class="issue-sidebar-block__link" href="<@hst.link hippobean=policy/>">${policy.title}</a></li>
                                </#list>
                            </ul>
                            <a href="<@hst.link path='/policies/?topics=' + document.title/>" class="see-all-button  see-all-button--icon  see-all-button--icon-grid"><span></span> See all policies</a>
                        </section>
                    </#if>

                    <#if publications?has_content>
                        <section class="issue-sidebar-block" id="related-publications">
                            <h2 class="gamma  emphasis  issue-sidebar-block__title">Publications</h2>
                            <ul class="no-bullets">
                                <#list publications as publication>
                                    <li><a class="issue-sidebar-block__link" href="<@hst.link hippobean=publication/>">${publication.title}</a></li>
                                </#list>
                            </ul>
                            <a href="<@hst.link path='/publications/?topics=' + document.title/>" class="see-all-button  see-all-button--icon  see-all-button--icon-grid"><span></span> See all publications</a>
                        </section>
                    </#if>

                </div>
            </div><!--
     --></div>
    </div>

    <#if document.includeFeedback == true>
        <div class="grid"><!--
            --><div class="grid__item  medium--nine-twelfths  large--seven-twelfths">
                <#include 'common/feedback-wrapper.ftl'>
            </div><!--
        --></div>
    </#if>
</div>

<footer class="article-footer" id="article-footer">

    <div class="wrapper">

        <div class="grid"><!--
        <@hst.html hippohtml=document.phone var="phone"/>
        <@hst.html hippohtml=document.postalAddress var="address"/>
            <#if phone?has_content ||
                document.contactInformation.email?has_content ||
                address?has_content>
             --><div class="grid__item medium--four-twelfths">
                    <div id="contact">
                        <h2 class="gamma emphasis"><#if document.contactTitle?has_content>${document.contactTitle}<#else>Contact</#if></h2>

                        <#if phone?has_content><p><b>Phone</b>: ${phone}</p></#if>
                        <#if document.contactInformation.email?has_content><p><b>Email</b>: ${document.contactInformation.email}</p></#if>
                        <#if address?has_content><p><b>Address</b>:<br /> ${address}</p></#if>
                    </div>
                </div><!--
            </#if>

            <#if document.contactInformation.flickr?has_content ||
                document.contactInformation.twitter?has_content ||
                document.contactInformation.youtube?has_content ||
                document.contactInformation.website?has_content ||
                document.contactInformation.facebook?has_content ||
                document.contactInformation.blog?has_content>

             --><div class="grid__item medium--four-twelfths">
                    <div id="connect">
                        <h2 class="gamma emphasis"><#if document.socialMediaTitle?has_content>${document.socialMediaTitle}<#else>Connect</#if></h2>

                        <ul class="external-links">
                            <#if document.contactInformation.flickr?has_content>
                            <li class="external-links__item">
                                <a class="external-links__link" href="${document.contactInformation.flickr}"><span class="external-links__icon fa fa-flickr"></span>Flickr images</a>
                            </li>
                            </#if>
                            <#if document.contactInformation.twitter?has_content>
                            <li class="external-links__item">
                                <a class="external-links__link" href="http://twitter.com/${document.contactInformation.twitter}"><span class="external-links__icon fa fa-twitter"></span>${document.contactInformation.twitter}</a>
                            </li>
                            </#if>
                            <#if document.contactInformation.youtube?has_content>
                            <li class="external-links__item">
                                <a class="external-links__link" href="${document.contactInformation.youtube}"><span class="external-links__icon fa fa-youtube-square"></span>Youtube</a>
                            </li>
                            </#if>
                            <#if document.contactInformation.website?has_content>
                            <li class="external-links__item">
                                <a class="external-links__link" href="${document.contactInformation.website}"><span class="external-links__icon fa fa-link"></span>Website</a>
                            </li>
                            </#if>
                            <#if document.contactInformation.facebook?has_content>
                            <li class="external-links__item">
                                <a class="external-links__link" href="${document.contactInformation.facebook}"><span class="external-links__icon fa fa-facebook-square"></span>Facebook</a>
                            </li>
                            </#if>
                            <#if document.contactInformation.blog?has_content>
                            <li class="external-links__item">
                                <a class="external-links__link" href="${document.contactInformation.blog}"><span class="external-links__icon fa fa-comment"></span>Blog</a>
                            </li>
                            </#if>
                        </ul>
                    </div>
                </div><!--
            </#if>
            <#if document.contactInformation.twitter?has_content>
             --><div class="grid__item  medium--four-twelfths  force-right">

                    <div class="social-channel" id="twitter">
                        <header class="social-channel__header social-channel__header--twitter">
                            <a class="social-channel__link" href="https://twitter.com/${document.contactInformation.twitter}">
                                <p class="social-channel__intro">Scottish Government on</p>
                                <h3 class="social-channel__title">Twitter</h3>
                            </a>
                        </header>

                        <div class="social-channel__body">
                            <a class="twitter-timeline"
                               href="https://twitter.com/${document.contactInformation.twitter}"
                               data-tweet-limit="4"
                               data-chrome="nofooter noborders noheader"
                               <#if document.includeTwitterReplies == true>data-show-replies="true"</#if>
                            >Tweets by @${document.contactInformation.twitter}</a>
                            <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+"://platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
                        </div>
                    </div>

                </div><!--
            </#if>
     --></div>

    </div>
</footer>
</div>

</#if>

<@hst.headContribution category="footerScripts">
    <script src="<@hst.webfile path="/assets/scripts/issue-hub.js"/>" type="text/javascript"></script>
</@hst.headContribution>

<#if document??>
    <@hst.headContribution category="pageTitle">
        <title>${document.title?html} - gov.scot</title>
    </@hst.headContribution>
    <@hst.headContribution>
        <meta name="description" content="${document.metaDescription?html}"/>
    </@hst.headContribution>

    <@hst.link var="canonicalitem" hippobean=document canonical=true />
    <#include "common/canonical.ftl" />
</#if>

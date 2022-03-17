<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#-- @ftlvariable name="item" type="scot.gov.www.beans.SiteItem" -->
<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->

<footer id="site-footer" class="site-footer  ">
    <div class="wrapper">
        <div class="grid"><!--
            <#if pageable?? && pageable.items??>
             --><div class="grid__item medium--four-twelfths large--four-twelfths">
                    <ul class="site-items site-footer__list">
                        <#list pageable.items as item>
                            <li>
                                <#if item.externalLink?? && item.externalLink.url?has_content>
                                    <#assign href>${item.externalLink.url}</#assign>
                                <#else>
                                    <#assign href><@hst.link hippobean=item /></#assign>
                                </#if>

                                <a class="site-items__link site-footer__link" href="${href}" data-gtm="link-footer">${item.title}</a>
                            </li>
                        </#list>
                    </ul>
                </div><!--
            </#if>
             --><div class="grid__item medium--seven-twelfths large--seven-twelfths">
                <ul class="social-links site-footer__list">
                    <li class="social-links__item">
                        <a title="Facebook" class="social-links__link" href="https://www.facebook.com/TheScottishGovernment/timeline/" data-gtm="social">
                            <span class="fa fa-facebook" aria-hidden="true"></span>
                        </a>
                    </li>
                    <li class="social-links__item">
                        <a title="Twitter" class="social-links__link" href="https://twitter.com/scotgov" data-gtm="social">
                            <span class="fa fa-twitter" aria-hidden="true"></span>
                        </a>
                    </li>
                    <li class="social-links__item">
                        <a title="Flickr" class="social-links__link" href="https://www.flickr.com/photos/26320652@N02" data-gtm="social">
                            <span class="fa fa-flickr" aria-hidden="true"></span>
                        </a>
                    </li>
                    <li class="social-links__item">
                        <a title="YouTube" class="social-links__link" href="https://www.youtube.com/user/scottishgovernment" data-gtm="social">
                            <span class="fa fa-youtube-play" aria-hidden="true"></span>
                        </a>
                    </li>
                    <li class="social-links__item">
                        <a title="Instagram" class="social-links__link" href="https://www.instagram.com/scotgov/" data-gtm="social">
                            <span class="fa fa-instagram" aria-hidden="true"></span>
                        </a>
                    </li>
                </ul>
            </div><!--
         --></div>
    </div>
</footer>

<#include "../include/imports.ftl">

<#-- @ftlvariable name="item" type="scot.gov.www.beans.SiteItem" -->
<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->


<footer class="ds_site-footer  ds_reversed">
    <div class="ds_wrapper">
        <div class="ds_site-footer__content">
            <ul class="ds_site-footer__site-items">
                <#list pageable.items as item>
                    <li class="ds_site-items__item">
                        <#if item.externalLink?? && item.externalLink.url?has_content>
                            <#assign href>${item.externalLink.url}</#assign>
                        <#else>
                            <#assign href><@hst.link hippobean=item /></#assign>
                        </#if>

                        <a href="${href}" data-gtm="link-footer">${item.title}</a>
                    </li>
                </#list>
            </ul>

            <div class="ds_site-footer__copyright">
                <a class="ds_site-footer__copyright-logo" href="http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">
                    <img src="/assets/images/logos/ogl.svg" alt="Open Government License" />
                </a>
                <p>All content is available under the <a href="http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">Open Government Licence v3.0</a>, except for graphic assets and where otherwise stated</p>
                <p>&copy; Crown Copyright</p>
            </div>

            <div class="ds_site-footer__org">
                <a class="ds_site-footer__org-link" title="The Scottish Government" href="http://www.gov.scot/">
                    <img class="ds_site-footer__org-logo" src="/assets/images/logos/scottish-government.svg" alt="gov.scot" />
                </a>
            </div>
        </div>

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
    </div>
</footer>

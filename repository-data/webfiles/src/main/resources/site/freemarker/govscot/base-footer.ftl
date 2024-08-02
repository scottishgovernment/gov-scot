<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>

<#-- @ftlvariable name="item" type="scot.gov.www.beans.SiteItem" -->
<#-- @ftlvariable name="pageable" type="org.onehippo.cms7.essentials.components.paging.Pageable" -->
<!--noindex-->
<footer class="ds_site-footer">
    <aside class="gov_secondary-footer">
        <div class="ds_wrapper">
            <h2 class="visually-hidden">
                Follow The Scottish Government
            </h2>
            <ul class="gov_social-links">
                <li class="gov_social-links__item">
                    <a title="Facebook" class="gov_social-links__link" href="https://www.facebook.com/TheScottishGovernment/timeline/" data-footer="social-1">
                        <div class="gov_social-links__link-inner">
                            <span class="gov_social-links__icon"><svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#facebook"></use></svg></span>
                            <span>Facebook</span>
                        </div>
                    </a>
                </li>
                <li class="gov_social-links__item">
                    <a title="X" class="gov_social-links__link" href="https://x.com/scotgov" data-footer="social-2">
                        <div class="gov_social-links__link-inner">
                            <span class="gov_social-links__icon"><svg class="ds_icon  ds_icon--20" aria-hidden="true" role="img"><use href="${iconspath}#x"></use></svg></span>
                            <span>X</span>
                        </div>
                    </a>
                </li>
                <li class="gov_social-links__item">
                    <a title="Flickr" class="gov_social-links__link" href="https://www.flickr.com/photos/26320652@N02" data-footer="social-3">
                        <div class="gov_social-links__link-inner">
                            <span class="gov_social-links__icon"><svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#flickr"></use></svg></span>
                            <span>Flickr</span>
                        </div>
                    </a>
                </li>
                <li class="gov_social-links__item">
                    <a title="YouTube" class="gov_social-links__link" href="https://www.youtube.com/user/scottishgovernment" data-footer="social-4">
                        <div class="gov_social-links__link-inner">
                            <span class="gov_social-links__icon"><svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#youtube"></use></svg></span>
                            <span>YouTube</span>
                        </div>
                    </a>
                </li>
                <li class="gov_social-links__item">
                    <a title="Instagram" class="gov_social-links__link" href="https://www.instagram.com/scotgov/" data-footer="social-5">
                        <div class="gov_social-links__link-inner">
                            <span class="gov_social-links__icon"><svg class="ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#instagram"></use></svg></span>
                            <span>Instagram</span>
                        </div>
                    </a>
                </li>
            </ul>
        </div>
    </aside>

    <div class="ds_wrapper">
        <div class="ds_site-footer__content">
            <ul class="ds_site-footer__site-items">
                <#list children as item>
                    <li class="ds_site-items__item">
                        <#if item.externalLink?? && item.externalLink.url?has_content>
                            <#assign href>${item.externalLink.url}</#assign>
                        <#else>
                            <#assign href><@hst.link hippobean=item /></#assign>
                        </#if>

                        <a href="${href}">${item.title}</a>
                    </li>
                </#list>
            </ul>

            <div class="ds_site-footer__copyright">
                <a class="ds_site-footer__copyright-logo" href="https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">
                    <img width="300" height="121" src="<@hst.link path='assets/images/logos/ogl.svg' />" alt="Open Government License" loading="lazy" />
                </a>
                <p>All content is available under the <a href="https://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/">Open Government Licence v3.0</a>, except for graphic assets and where otherwise stated</p>
                <p>&copy; Crown Copyright</p>
            </div>

            <div class="ds_site-footer__org">
                <img width="300" height="57" class="ds_site-footer__org-logo" src="<@hst.link path='/assets/images/logos/scottish-government--min.svg' />" alt="gov.scot" loading="lazy" />
            </div>
        </div>
    </div>
</footer>
<!--endnoindex-->

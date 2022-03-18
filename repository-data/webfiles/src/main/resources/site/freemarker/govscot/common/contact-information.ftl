<#ftl output_format="HTML">
<#-- @ftlvariable name="contactInformation" type="scot.gov.www.beans.ContactInformation" -->
<#-- @ftlvariable name="postalAddress" type="org.hippoecm.hst.content.beans.standard.HippoHtml" -->

<aside class="contact-information">
    <#if contactInformation??>
        <div class="contact-information__group">
            <h2 class="gamma  emphasis  contact-information__title">Connect</h2>
            <ul class="external-links">
                <#if contactInformation.twitter?has_content>
                    <li class="external-links__item">
                        <a class="external-links__link" href="http://twitter.com/${contactInformation.twitter}"><span class="external-links__icon fa fa-twitter"></span>${contactInformation.twitter}</a>
                    </li>
                </#if>
                <#if contactInformation.flickr?has_content>
                    <li class="external-links__item">
                        <a class="external-links__link" href="${contactInformation.flickr}"><span class="external-links__icon fa fa-flickr"></span>Flickr images</a>
                    </li>
                </#if>
                <#if contactInformation.website?has_content>
                    <li class="external-links__item">
                        <a class="external-links__link" href="${contactInformation.website}"><span class="external-links__icon fa fa-link"></span>Website</a>
                    </li>
                </#if>
                <#if contactInformation.email?has_content>
                    <li class="external-links__item">
                        <a class="external-links__link" href="mailto:${contactInformation.email}"><span class="external-links__icon fa fa-envelope"></span>Email</a>
                    </li>
                </#if>
                <#if contactInformation.facebook?has_content>
                    <li class="external-links__item">
                        <a class="external-links__link" href="${contactInformation.facebook}"><span class="external-links__icon fa fa-facebook-square"></span>Facebook</a>
                    </li>
                </#if>
                <#if contactInformation.youtube?has_content>
                    <li class="external-links__item">
                        <a class="external-links__link" href="${contactInformation.youtube}"><span class="external-links__icon fa fa-youtube-square"></span>Youtube</a>
                    </li>
                </#if>
                <#if contactInformation.blog?has_content>
                    <li class="external-links__item">
                        <a class="external-links__link" href="${contactInformation.blog}"><span class="external-links__icon fa fa-comment"></span>Blog</a>
                    </li>
                </#if>
            </ul>
        </div>
    </#if>

    <#if postalAddress??>
        <div class="contact-information__group">
            <h2 class="gamma  emphasis  contact-information__title">Contact</h2>
            <@hst.html hippohtml=postalAddress/>
        </div>
    </#if>
</aside>

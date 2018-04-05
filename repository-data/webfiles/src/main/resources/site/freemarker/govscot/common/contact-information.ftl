<#-- @ftlvariable name="contactInformation" type="scot.gov.www.beans.ContactInformation" -->
<#-- @ftlvariable name="postalAddress" type="org.hippoecm.hst.content.beans.standard.HippoHtml" -->

<aside class="contact-information">
    <div class="contact-information__group">
        <h3 class="emphasis contact-information__title">Connect</h3>
        <ul class="external-links">
            <#if document.contactInformation.twitter?has_content>
                <li class="external-links__item">
                    <a class="external-links__link" href="http://twitter.com/${document.contactInformation.twitter}"><span class="external-links__icon fa fa-twitter"></span>${document.contactInformation.twitter}</a>
                </li>
            </#if>
            <#if document.contactInformation.flickr?has_content>
                <li class="external-links__item">
                    <a class="external-links__link" href="${document.contactInformation.flickr}"><span class="external-links__icon fa fa-flickr"></span>Flickr images</a>
                </li>
            </#if>
            <#if document.contactInformation.website?has_content>
                <li class="external-links__item">
                    <a class="external-links__link" href="${document.contactInformation.website}"><span class="external-links__icon fa fa-link"></span>Website</a>
                </li>
            </#if>
            <#if document.contactInformation.email?has_content>
                <li class="external-links__item">
                    <a class="external-links__link" href="mailto:${document.contactInformation.email}"><span class="external-links__icon fa fa-envelope"></span>Email</a>
                </li>
            </#if>
            <#if document.contactInformation.facebook?has_content>
                <li class="external-links__item">
                    <a class="external-links__link" href="${document.contactInformation.facebook}"><span class="external-links__icon fa fa-facebook-square"></span>Facebook</a>
                </li>
            </#if>
            <#if document.contactInformation.youtube?has_content>
                <li class="external-links__item">
                    <a class="external-links__link" href="${document.contactInformation.youtube}"><span class="external-links__icon fa fa-youtube-square"></span>Youtube</a>
                </li>
            </#if>
            <#if document.contactInformation.blog?has_content>
                <li class="external-links__item">
                    <a class="external-links__link" href="${document.contactInformation.blog}"><span class="external-links__icon fa fa-comment"></span>Blog</a>
                </li>
            </#if>
        </ul>
    </div>

    <#if document.postalAddress??>
        <div class="contact-information__group">
            <h3 class="emphasis contact-information__title">Contact</h3>
            <p>${document.postalAddress.content}</p>
        </div>
    </#if>
</aside>

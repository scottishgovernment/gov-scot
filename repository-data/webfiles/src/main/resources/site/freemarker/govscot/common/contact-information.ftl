<#ftl output_format="HTML">
<#-- @ftlvariable name="contactInformation" type="scot.gov.www.beans.ContactInformation" -->
<#-- @ftlvariable name="postalAddress" type="org.hippoecm.hst.content.beans.standard.HippoHtml" -->
<@hst.webfile var="iconspath" path="/assets/images/icons/icons.stack.svg"/>
<#include "../../include/imports.ftl">

<#if contactInformation??>
<div class="ds_contact-details">

    <h2 class="ds_contact-details__title  <#if contactInformationHeadingModifier??>${contactInformationHeadingModifier}</#if>">Contact</h2>

    <dl class="ds_contact-details__list">
        <@hst.html var="htmladdress" hippohtml=postalAddress/>
        <#if htmladdress?has_content>
            <div class="ds_contact-details__item">
                <dt>Address</dt>
                <dd translate="no">
                    <address>
                        <@hst.html hippohtml=postalAddress/>
                    </address>
                </dd>
            </div>
        </#if>

        <#if contactInformation.email?has_content>
            <div class="ds_contact-details__item">
                <dt>Email</dt>
                <dd><a href="mailto:${contactInformation.email}">${contactInformation.email}</a></dd>
            </div>
        </#if>

        <div class="ds_contact-details__item  ds_contact-details__social">
            <dt class="visually-hidden">Connect</dt>
            <#if contactInformation.facebook?has_content>
            <dd class="ds_contact-details__social-item">
                <a class="ds_contact-details__social-link" href="${contactInformation.facebook}">
                    <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#facebook"></use></svg>
                    Facebook
                </a>
            </dd>
            </#if>
            <#if contactInformation.twitter?has_content>
            <dd class="ds_contact-details__social-item">
                <a class="ds_contact-details__social-link" href="http://twitter.com/${contactInformation.twitter}">
                    <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#twitter"></use></svg>
                    ${contactInformation.twitter}
                </a>
            </dd>
            </#if>
            <#if contactInformation.flickr?has_content>
            <dd class="ds_contact-details__social-item">
                <a class="ds_contact-details__social-link" href="#${contactInformation.flickr}">
                    <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#flickr"></use></svg>
                    Flickr
                </a>
            </dd>
            </#if>
            <#if contactInformation.youtube?has_content>
            <dd class="ds_contact-details__social-item">
                <a class="ds_contact-details__social-link" href="${contactInformation.youtube}">
                    <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#youtube"></use></svg>
                    YouTube
                </a>
            </dd>
            </#if>
            <#if contactInformation.blog?has_content>
            <dd class="ds_contact-details__social-item">
                <a class="ds_contact-details__social-link" href="${contactInformation.blog}">
                    <svg class="ds_contact-details__social-icon  ds_icon" aria-hidden="true" role="img"><use href="${iconspath}#blog"></use></svg>
                    Blog
                </a>
            </dd>
            </#if>
        </div>
    </dl>

</div>
</#if>

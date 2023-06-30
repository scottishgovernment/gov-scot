<@hst.link var="imagelinkDefault" path='/assets/images/logos/SGLogo1200x630.png' fullyQualified=true/>
<@hst.link var="link" canonical=true fullyQualified=true/>

<#-- Facebook meta tags -->
<@hst.headContribution category="facebookMeta">
<meta property="og:url" content="${link}" />
</@hst.headContribution>

<@hst.headContribution category="facebookMeta">
<meta property="og:type" content="website" />
</@hst.headContribution>

<#if document.title?has_content>
<@hst.headContribution category="facebookMeta">
<meta property="og:title" content="${document.title?json_string}" />
</@hst.headContribution>
</#if>

<#if document.metaDescription?has_content>
<@hst.headContribution category="facebookMeta">
<meta property="og:description" content="${document.metaDescription?json_string}" />
</@hst.headContribution>
</#if>

<#-- For people that have no image omit as this metadata value is used for search results -->
<#if imageBlank?? && imageBlank>
<#-- omit as person with no image -->
<#elseif imagelink??>
<@hst.headContribution category="facebookMeta">
<meta property="og:image" content="${imagelink}" />
</@hst.headContribution>
<#else>
<#-- use default image -->
<@hst.headContribution category="facebookMeta">
<meta property="og:image" content="${imagelinkDefault}" />
</@hst.headContribution>
</#if>


<#-- Twitter Meta Tags -->
<@hst.headContribution category="twitterMeta">
<meta name="twitter:card" content="summary_large_image"/>
</@hst.headContribution>

<@hst.headContribution category="twitterMeta">
<meta property="twitter:url" content="${link}"/>
</@hst.headContribution>

<#if document.title?has_content>
<@hst.headContribution category="twitterMeta">
<meta name="twitter:title" content="${document.title?json_string}"/>
</@hst.headContribution>
</#if>

<#if document.metaDescription?has_content>
<@hst.headContribution category="twitterMeta">
<meta name="twitter:description" content="${document.metaDescription?json_string}"/>
</@hst.headContribution>
</#if>

<#if imagelink??>
<@hst.headContribution category="twitterMeta">
<meta name="twitter:image" content="${imagelink}" />
</@hst.headContribution>
<#else>
<#-- use default image -->
<@hst.headContribution category="twitterMeta">
<meta name="twitter:image" content="${imagelinkDefault}" />
</@hst.headContribution>
</#if>
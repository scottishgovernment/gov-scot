<#ftl output_format="HTML">
<#if document??>

<@hst.link var="imagelink" path='/assets/images/logos/SGLogo1200x630.png' fullyQualified=true/>
<@hst.link var="link" canonical=true fullyQualified=true/>

<@hst.headContribution category="articleschema">
    <script type="application/ld+json">
{
    "@context": "https://schema.org",
    "@type": "Article",

    "image": "${imagelink}",

    "mainEntityOfPage": {
        "@type": "WebPage",
        "@id": "${link}"
    },

    "author": {
        "@type": "Organization",
        "name": "The Scottish Government",
        "url": "https://www.gov.scot",
        "logo": {
            "@type": "ImageObject",
            "url": "${imagelink}"
        }
    },

    "headline": "${document.title?json_string}",
    "dateModified": "<@fmt.formatDate value=document.getProperty('hippostdpubwf:lastModificationDate').time type="Date" pattern="yyyy-MM-dd" />",
    <#if document.publicationDate??>
        "datePublished": "<@fmt.formatDate value=document.publicationDate.time type="Date" pattern="yyyy-MM-dd" />",
    </#if>
    <#if document.metaDescription??>
    "description": "${document.metaDescription?json_string}",
    </#if>
    "publisher": {
        "@type": "Organization",
        "name": "The Scottish Government",
        "url": "https://www.gov.scot",
        "logo": {
            "@type": "ImageObject",
            "url": "${imagelink}"
        }
    }
}
    </script>
</@hst.headContribution>

<#-- Facebook meta tags -->
<@hst.headContribution category="facebookMeta">
<meta property="og:url" content="${link}" />
</@hst.headContribution>

<@hst.headContribution category="facebookMeta">
<meta property="og:type" content="website" />
</@hst.headContribution>

<@hst.headContribution category="facebookMeta">
<meta property="og:title" content="${document.title?json_string}" />
</@hst.headContribution>

<#if document.metaDescription??>
<@hst.headContribution category="facebookMeta">
<meta property="og:description" content="${document.metaDescription?json_string}" />
</@hst.headContribution>
</#if>

<@hst.headContribution category="facebookMeta">
<meta property="og:image" content="${imagelink}" />
</@hst.headContribution>

<#-- Twitter Meta Tags -->
<@hst.headContribution category="twitterMeta">
<meta name="twitter:card" content="summary_large_image"/>
</@hst.headContribution>

<@hst.headContribution category="twitterMeta">
<meta property="twitter:url" content="${link}"/>
</@hst.headContribution>

<@hst.headContribution category="twitterMeta">
<meta name="twitter:title" content="${document.title?json_string}"/>
</@hst.headContribution>

<#if document.metaDescription??>
<@hst.headContribution category="twitterMeta">
<meta name="twitter:description" content="${document.metaDescription?json_string}"/>
</@hst.headContribution>
</#if>

<@hst.headContribution category="twitterMeta">
<meta name="twitter:image" content="${imagelink}" />
</@hst.headContribution>

<!-- DC Meta Tags -->

<#if title??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${title}"/>
    </@hst.headContribution>
    <@hst.headContribution category="pageTitle">
        <title>${title} <#if parent??>- ${parent.title} </#if>- gov.scot</title>
    </@hst.headContribution>
<#else>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.title" content="${document.title}"/>
    </@hst.headContribution>
    <@hst.headContribution category="pageTitle">
        <title>${document.title}  <#if parent??>- ${parent.title} </#if>- gov.scot</title>
    </@hst.headContribution>
</#if>

<#if parent??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.title.series" content="${parent.title}"/>
    </@hst.headContribution>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.title.series.link" content="<@hst.link hippobean=parent/>"/>
    </@hst.headContribution>
</#if>

<@hst.headContribution category="dcMeta">
    <meta name="dc.description" content="${document.summary}"/>
</@hst.headContribution>

<#if document.tags??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.subject" content="<#list document.tags as tag>${tag}<#sep>, </#sep></#list>"/>
    </@hst.headContribution>
</#if>

<#if document.displayDate??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=document.displayDate.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
    </@hst.headContribution>
<#elseif document.publicationDate??>
    <@hst.headContribution category="dcMeta">
        <meta name="dc.date.modified" content="<@fmt.formatDate value=document.publicationDate.time type="both" pattern="YYYY-MM-dd HH:mm"/>"/>
     </@hst.headContribution>
 </#if>


</#if>

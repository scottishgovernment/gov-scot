<@hst.headContribution category="articleschema">
    <script type="application/ld+json">
{
    "@context": "https://schema.org",
    "@type": "Article",

    image: "<@hst.link path='assets/images/logos/scotgovlogo.png' />",

    "mainEntityOfPage": {
        "@type": "WebPage",
        "@id": "<@hst.link hippobean=document />"
    },

    "author": {
        "@type": "Organization",
        "name": "The Scottish Government",
        "url": "https://www.gov.scot",
        "logo": {
            "@type": "ImageObject",
            "url": "<@hst.link path='assets/images/logos/scotgovlogo.png' />"
        }
    },

    "headline": "${document.title?json_string}",
    "dateModified": "<@fmt.formatDate value=document.getProperty('hippostdpubwf:lastModificationDate').time type="Date" pattern="yyyy-MM-dd" />",
    <#if document.publicationDate??>
        "datePublished": "<@fmt.formatDate value=document.publicationDate.time type="Date" pattern="yyyy-MM-dd" />",
    </#if>
    "description": "${document.metaDescription?json_string}",

    "publisher": {
        "@type": "Organization",
        "name": "The Scottish Government",
        "url": "https://www.gov.scot",
        "logo": {
            "@type": "ImageObject",
            "url": "<@hst.link path='assets/images/logos/scotgovlogo.png' />"
        }
    }
}
    </script>
</@hst.headContribution>

<@hst.headContribution category="articleschema">
    <script type="application/ld+json">
{
    "@context": "https://schema.org",
    "@type": "Article",

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
            "url": "<@hst.link path='assets/images/logos/scotgovlogo.svg' />"
        }
    },

    "headline": "${document.title}",
    "dateModified": "<@fmt.formatDate value=document.getProperty('hippostdpubwf:lastModificationDate').time type="Date" pattern="yyyy-MM-dd" />",
    "datePublished": "<@fmt.formatDate value=document.publicationDate.time type="Date" pattern="yyyy-MM-dd" />",
    "description": "${document.metaDescription}",
    
    "publisher": {
        "@type": "Organization",
        "name": "The Scottish Government",
        "url": "https://www.gov.scot",
        "logo": {
            "@type": "ImageObject",
            "url": "<@hst.link path='assets/images/logos/scotgovlogo.svg' />"
        }
    }
}
    </script>
</@hst.headContribution>

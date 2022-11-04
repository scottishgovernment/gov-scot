<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<@hst.link var="sitelink" hippobean=baseBean canonical=true fullyQualified=true/>
<@hst.headContribution category="title">
<script type="application/ld+json">
{
  "@context" : "https://schema.org",
  "@type" : "WebSite",
  "name" : "${sitetitle}",
  "url" : "${sitelink}"<#if isSearchEnabled>,
  "potentialAction": {
    "@type": "SearchAction",
    "target": {
        "@type": "EntryPoint",
        "urlTemplate": "${sitelink}search?q={search_term_string}"
    },
    "query-input": "required name=search_term_string"
  }
  <#else>

  </#if>
}
</script>
</@hst.headContribution>
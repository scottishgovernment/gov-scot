<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#if plausibleAnalyticsDomains?has_content>
<script defer data-domain="${plausibleAnalyticsDomains}" src="https://plausible.io/js/script.js"></script>
<script defer data-domain="${plausibleAnalyticsDomains}" src="https://plausible.io/js/script.file-downloads.js"></script>
<script defer data-domain="${plausibleAnalyticsDomains}" src="https://plausible.io/js/script.outbound-links.js"></script>
</#if>
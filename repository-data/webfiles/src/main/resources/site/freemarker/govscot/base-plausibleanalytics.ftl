<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#if plausibleAnalyticsDomains?has_content>
<script defer data-api="https://plausible.io/api/event" data-domain="${plausibleAnalyticsDomains}" src="<@hst.link path='/assets/scripts/vendor/plausible/script.file-downloads.outbound-links.js'/>"></script>
</#if>

<#ftl output_format="HTML">
<#include "../include/imports.ftl">
<#if plausibleAnalyticsDomains?has_content>
<script defer data-domain="${plausibleAnalyticsDomains}" src="<@hst.link path='/assets/scripts/vendor/plausible/script.js'/>"></script>
<script defer data-domain="${plausibleAnalyticsDomains}" src="<@hst.link path='/assets/scripts/vendor/plausible/script.file-downloads.js'/>"></script>
<script defer data-domain="${plausibleAnalyticsDomains}" src="<@hst.link path='/assets/scripts/vendor/plausible/script.outbound-links.js'/>"></script>
</#if>

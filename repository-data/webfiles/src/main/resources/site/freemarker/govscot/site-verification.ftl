<#ftl output_format="HTML">
<#include "../include/imports.ftl">

<#list siteverifications as siteverification>
    <@hst.headContribution category="siteverification">
    <meta name="${siteverification.type}" content="${siteverification.code}" />
    </@hst.headContribution>
</#list>

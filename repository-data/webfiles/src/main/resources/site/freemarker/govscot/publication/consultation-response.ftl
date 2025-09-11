<#ftl output_format="HTML">

<#if document.contentType == "govscot:Consultation">
    <#if isOpen>
        <div class="ds_inset-text">
            <div class="ds_inset-text__text">
                <p>
                    <strong class="ds_tag">Open</strong><br/>
                    ${timeToRespondString}<br/>

                    <#if document.responseUrl?has_content>
                        <a href="${document.responseUrl}">Respond online</a>
                    </#if>
                </p>
                <dl class="ds_metadata">
                    <div class="ds_metadata__item">
                        <dt class="ds_metadata__key">Closes</dt>
                        <dd class="ds_metadata__value">
                            <strong id="sg-meta__meeting-date">
                                ${closingDateTime}
                            </strong>
                        </dd>
                    </div>
                </dl>
            </div>
        </div>
    <#else>
        <div class="ds_inset-text">
            <div class="ds_inset-text__text">
                <p>
                    <strong class="ds_tag ds_tag--grey">Closed</strong><br>
                    This consultation closed <@fmt.formatDate value=document.closingDate.time type="both" pattern="d MMMM yyyy"/>.
                </p>
                <#if document.responseUrl?has_content>
                <p><a href="${document.responseUrl}">View this consultation</a> on consult.gov.scot, including responses once published.</p>
                </#if>
                <#if consultationAnalysis?has_content>
                <h2 class="ds_h3">Consultation analysis</h2>
                <ul class="ds_no-bullets">
                <#list consultationAnalysis as analysis>
                    <li><a href="<@hst.link hippobean=analysis />">${analysis.title}</a></li>
                </#list>
                </ul>
                </#if>
            </div>
        </div>
    </#if>
</#if>
<#ftl output_format="HTML">

<#if document.contentType == "govscot:Consultation">
    <#if isOpen>
        <div class="ds_inset-text">
            <div class="ds_inset-text__text">
                <p>
                    <strong class="ds_tag">Open</strong><br/>
                    ${timeToRespondString}<br/>
                    <a href="${document.responseUrl}">Respond online</a>
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
                    This consultation closed <@fmt.formatDate value=document.closingDate.time type="both" pattern="d MMMM yyyy"/>
                </p>
                <#if consultationAnalysis?has_content>
                <p>
                    <!-- TODO: handle as a list ... if there is only one then do this:-->

                    <#list consultationAnalysis as analysis>
                        <a href="<@hst.link hippobean=analysis />">Consultation analysis</a>
                    </#list>
                    <!-- if more then title and then comma separated list? -->

                </#if>
            </div>
        </div>
    </#if>
</#if>
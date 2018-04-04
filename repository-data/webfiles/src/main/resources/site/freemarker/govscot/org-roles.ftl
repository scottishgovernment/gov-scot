<#include "../include/imports.ftl">

<#if document??>
<pre>TODO:
====
* link from image/title to role (or person) page
* list responsibilities
<del>* multiple sections (cabinet/ministers, directors-general/non-exec)</del>
* <em>dynamic</em> section titles & intros
* script behavior
<del>* org name for section ID</del>
====
</pre>
<style>
#page-content a:not([href]), #page-content a[href="#"], #page-content a[href=""] {
    background: red;
}
</style>


<#--  <ul>
<#list document?keys as prop>
<li>${prop} = ${document[prop]}</li>
</#list>
</ul>  -->

    <div class="grid" id="page-content"><!--
        --><div class="grid__item medium--nine-twelfths push--medium--three-twelfths">

            <h1 class="article-header">
                ${document.title}
            </h1>

            <div class="grid">
                <div class="grid__item large--seven-ninths">
                    
                    <#if document.content.content??>
                        ${document.content.content}
                    </#if>

                    <#assign orgName = 'The Scottish Cabinet'/>
                    <#assign orgDescription = 'The Cabinet is the main decision-making body of the Scottish Government. It is made up of the First Minister, all Cabinet Secretaries, Minister for Parliamentary Business and Permanent Secretary. The Lord Advocate may also attend in his or her role as the Scottish Government’s principal legal adviser. Cabinet meetings are held weekly during Parliament in Bute House, Edinburgh, and may also be held at other times in locations throughout Scotland.'/>
                    <#assign orgRoles = document.orgRole/>
                    <#include 'about/org-roles-grid.ftl' />

                    <#assign orgName = 'Ministers'/>
                    <#assign orgDescription = 'Cabinet Secretaries are supported by Ministers. Responsibilities and biographies of Ministers, including the Lord Advocate and Solicitor General (who are also the principal legal advisers to the Scottish Government), are below.'/>
                    <#assign orgRoles = document.secondaryOrgRole/>
                    <#include 'about/org-roles-grid.ftl' />

                </div>
            </div>

        </div><!--
        --><div class="grid__item medium--three-twelfths pull--medium--nine-twelfths">
            <div>
                <@hst.include ref="side-menu"/>
            </div>
        </div><!--
    --></div>
</#if>

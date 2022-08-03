<#macro placeholdertext lines>
<div class="cms-placeholder-text"><#list 0..<lines as i><span class="cms-text"></span></#list></div>
</#macro>

<#macro placeholderimage>
<div class="ds_aspect-box">
    <svg class="ds_aspect-box__inner" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
        viewBox="0 0 160 90">
        <style type="text/css">
            .st0{fill:currentColor;opacity: 0.5;}
        </style>
        <rect class="st0" width="160" height="90"/>
    </svg>
</div>
</#macro>

<#macro placeholdervideo>
<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
    viewBox="0 0 160 90">
    <style type="text/css">
        .st0{fill:currentColor;opacity: 0.5;}
    </style>
    <rect class="st0" width="160" height="90"/>
</svg>
<svg class="ds_icon  ds_cb__poster-video-icon" role="img"><use href="${iconspath}#play_circle_outline"></use></svg>
</#macro>

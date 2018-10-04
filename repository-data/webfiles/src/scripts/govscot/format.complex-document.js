define([
    './component.expandable',
    './component.display-toggle'
], function (expandable, displayToggle) {
    'use strict';

    var complexDocumentPage = {};

    complexDocumentPage.init = function () {
        displayToggle.init();
        checkDocumentTitleSticky();
        tweakComplexDocumentMarkup();
        
        $(window).on('scroll resize', function () {
            checkDocumentTitleSticky();
        });
    };

    window.format = complexDocumentPage;
    window.format.init();

    function checkDocumentTitleSticky () {

        let documentNavSticky = $('.document-nav--sticky');

        if (documentNavSticky.length && documentNavSticky.css('position').match(/sticky/).length) {
            let stickyPropertyName = documentNavSticky.css('position');

            // set top offset of section marker (height of site-header--scaled)
            // set top offset of document nav (section marker height + site header height)
            let headerHeight = 0;
            if ($('.site-header--scaled').length) {
                headerHeight = parseInt($('.site-header--scaled').css('height'), 10);
            }

            const sectionMarker = $('.section-marker');
            sectionMarker.css({top: headerHeight});
            $('.document-nav').css({top: headerHeight + sectionMarker.height()});
    
            sectionMarker.css({position: 'static'});
            const triggerPoint = sectionMarker.offset().top - headerHeight;
            sectionMarker.css({position: stickyPropertyName});




            if ($(window).scrollTop() > triggerPoint) {
                $('.section-marker__document-title').addClass('section-marker__document-title--visible')
            } else {
                $('.section-marker__document-title').removeClass('section-marker__document-title--visible')
            }
        }
    }

    function tweakComplexDocumentMarkup () {
        // 1. add overflow class to image containers
        $('.bs_figure').addClass('overflow--medium--three-twelfths');

        // 2. style standard definition block
        $('blockquote.bs_blockquote').addClass('info-note blockquote');

        // 2.1 define elements
        let limitationTitle, standardTitle, newLimitationTitle, newStandardTitle;

        $('blockquote.bs_blockquote p').each(function (k, v) {
            if ($(this).text().toLowerCase() === "limitation:") {
                limitationTitle = $(this);
            }
        });

        // 2.2 replace elements
        if (limitationTitle) {
            newLimitationTitle = $("<h5 class=delta/>");
            newLimitationTitle.html(limitationTitle.text());
            newLimitationTitle.insertAfter(limitationTitle);
            limitationTitle.remove();
        }

        standardTitle = $('blockquote.bs_blockquote .bs_blockquote-title');
        newStandardTitle = $("<h4 class=\"beta no-top-margin\"/>");
        newStandardTitle.html(standardTitle.html());
        newStandardTitle.insertAfter(standardTitle);
        standardTitle.remove();
    }

    return complexDocumentPage;
});

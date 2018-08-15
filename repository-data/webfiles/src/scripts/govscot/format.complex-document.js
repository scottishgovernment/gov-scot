define([
    './component.expandable'
], function (expandable) {
    'use strict';

    var complexDocumentPage = {};

    complexDocumentPage.init = function () {
        checkDocumentTitleSticky();
        
        $(window).on('scroll resize', function () {
            checkDocumentTitleSticky();
        });
    };

    window.format = complexDocumentPage;
    window.format.init();

    function checkDocumentTitleSticky () {

        if ($('.document-nav--sticky').length) {

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
            sectionMarker.css({position: 'sticky'});

            if ($(window).scrollTop() > triggerPoint) {
                $('.section-marker__document-title').addClass('section-marker__document-title--visible')
            } else {
                $('.section-marker__document-title').removeClass('section-marker__document-title--visible')
            }
        }
    }

    return complexDocumentPage;
});

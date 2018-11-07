define([
    'jquery',
    './component.banner',
    './component.cookie-notice',
    './component.expand',
    './component.header',
    './component.google-analytics',
    './component.feedback'
], function ($, banner, cookieNotice, expand, header, mourningBanner, feedback) {
    'use strict';

    const global = {
        compensateAnchorOffsetForStickyElements: function () {
            // IE8 doesn't support window.pageYOffset
            if (window.pageXOffset !== undefined) {
                if (window.location.hash && $(window.location.hash).length) {
                    window.setTimeout(function () {
                        window.scrollTo(window.pageXOffset, parseInt($(window.location.hash).offset().top, 10) - 100);
                    }, 300);
                }

                $('a[href^="#"]:not(.js-display-toggle)').on('click', function () {
                    window.setTimeout(function () {
                        window.scrollTo(window.pageXOffset, window.pageYOffset - 100);
                    }, 10);
                });
            }
        },

        svgFallback: function () {
            if (!document.implementation.hasFeature("http://www.w3.org/TR/SVG11/feature#Image", "1.1")) {
                $('img[src$=".svg"]').each(function () {
                    $(this).attr('src', $(this).attr('src').replace(/\.svg$/, '.png'));
                });
            }
        },

        init: function () {
            this.svgFallback();
            this.compensateAnchorOffsetForStickyElements();

            banner.init('staging-banner');
            cookieNotice.init();
            expand.init();
            header.init();
        }
    };

    global.init();

    return global;
});

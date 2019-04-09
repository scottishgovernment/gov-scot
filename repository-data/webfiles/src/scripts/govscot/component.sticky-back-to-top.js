// STICKY BACK TO TOP
/*
 Contains functionality sticky "back to top" links, reusable across formats
 */

/* global window */

'use strict';

import $ from 'jquery';

const backToTop = {

    init: function () {
        const that = this;

        const backToTopIcon = $('<a class="sticky-back-to-top sticky-back-to-top--hidden" href="#page-top">Top</a>');
        backToTopIcon.appendTo('body');

        let t = false;
        const visibleTime = 2000;
        let lastScrollTop = 0;

        $(window).on('scroll', function () {
            // detect whether we are scrolling UP
            let scrollingUp = false;

            const scrollTop = $(window).scrollTop();

            if (scrollTop <= lastScrollTop) {
                // we are scrolling UP
                scrollingUp = true;
            }
            lastScrollTop = scrollTop;

            // detect whether we are near the top of the page (1/4 screen height)
            let nearTopOfPage = false;
            if ($(window).scrollTop() < $(window).innerHeight() * 0.25) {
                nearTopOfPage = true;
            }

            if(scrollingUp && !nearTopOfPage) {

                backToTopIcon.addClass('sticky-back-to-top--show');
                backToTopIcon.removeClass('sticky-back-to-top--hidden');

                window.clearTimeout(t);

                t = window.setTimeout(function () {
                    that.hideBackToTopIcon(backToTopIcon, t);
                }, visibleTime);
            } else {
                // if we are scrolling down, hide the back to top button
                backToTopIcon.removeClass('sticky-back-to-top--show');
                backToTopIcon.addClass('sticky-back-to-top--hidden');

                window.clearTimeout(t);
            }
        });

        backToTopIcon.on('mouseenter', function () {
            backToTopIcon.addClass('sticky-back-to-top--show');
            backToTopIcon.removeClass('sticky-back-to-top--hidden');

            window.clearTimeout(t);
        }).on('mouseleave', function () {
            t = window.setTimeout(function () {
                that.hideBackToTopIcon(backToTopIcon, t);
            }, visibleTime);
        }).on('click', function () {
            window.clearTimeout(t);
            that.hideBackToTopIcon(backToTopIcon, t);
        });
    },

    hideBackToTopIcon: function (backToTopIcon) {
        // match 1s fade time in CSS
        const fadeAnimationTime = 1000;

        backToTopIcon.removeClass('sticky-back-to-top--show');

        // hide after the fade animation has finished
        window.setTimeout(function () {
            backToTopIcon.addClass('sticky-back-to-top--hidden');
        }, fadeAnimationTime);
    }
};

export default backToTop;

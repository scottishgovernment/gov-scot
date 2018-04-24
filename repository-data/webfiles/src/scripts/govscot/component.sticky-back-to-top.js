//sticky-back-to-top.js
/*
 Contains functionality sticky "back to top" links, reusable across formats
 */

define([
    'jquery'
], function($) {

    var backToTop = {

        init: function () {
            var that = this;

            var backToTopIcon = $('<a class="sticky-back-to-top sticky-back-to-top--hidden" href="#page-top">Top</a>');
            backToTopIcon.appendTo('body');

            var t = false;
            var visibleTime = 2000;
            var lastScrollTop = 0;

            $(window).on('scroll', function () {
                // detect whether we are scrolling UP
                var scrollingUp = false;

                var scrollTop = $(window).scrollTop();

                if (scrollTop <= lastScrollTop) {
                    // we are scrolling UP
                    scrollingUp = true;
                }
                lastScrollTop = scrollTop;

                // detect whether we are near the top of the page (1/4 screen height)
                var nearTopOfPage = false;
                if ($(window).scrollTop() < $(window).innerHeight() * 0.25) {
                    nearTopOfPage = true;
                }

                if(scrollingUp && !nearTopOfPage) {

                    backToTopIcon.addClass('sticky-back-to-top--show');
                    backToTopIcon.removeClass('sticky-back-to-top--hidden');

                    clearTimeout(t);

                    t = setTimeout(function () {
                        that.hideBackToTopIcon(backToTopIcon, t);
                    }, visibleTime);
                } else {
                    // if we are scrolling down, hide the back to top button
                    backToTopIcon.removeClass('sticky-back-to-top--show');
                    backToTopIcon.addClass('sticky-back-to-top--hidden');

                    clearTimeout(t);
                }
            });

            backToTopIcon.on('mouseenter', function () {
                backToTopIcon.addClass('sticky-back-to-top--show');
                backToTopIcon.removeClass('sticky-back-to-top--hidden');

                clearTimeout(t);
            }).on('mouseleave', function () {
                t = setTimeout(function () {
                    that.hideBackToTopIcon(backToTopIcon, t);
                }, visibleTime);
            }).on('click', function (event) {
                event.preventDefault();
                window.scrollTo(0, 0);

                clearTimeout(t);
                that.hideBackToTopIcon(backToTopIcon, t);
            });
        },

        hideBackToTopIcon: function (backToTopIcon) {
            // match 1s fade time in CSS
            var fadeAnimationTime = 1000;

            backToTopIcon.removeClass('sticky-back-to-top--show');

            // hide after the fade animation has finished
            setTimeout(function () {
                backToTopIcon.addClass('sticky-back-to-top--hidden');
            }, fadeAnimationTime);
        }
    };

    backToTop.init();

    return backToTop;
});

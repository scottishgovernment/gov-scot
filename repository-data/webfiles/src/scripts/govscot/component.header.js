define([], function () {
    'use strict';
    
    let siteHeader = $('.site-header'),
        autoThreshold = 768,
        scrollPos = 0;

    const header = {
        init: function () {
            let that = this;

            $(window).on('scroll', function () {

                if (window.innerWidth >= autoThreshold) {
                    $(window).trigger('unstickheader');
                    return;
                }

                let pageTop = $('#main-wrapper').offset().top;

                if ($(window).scrollTop() > pageTop) {
                    $(window).trigger('stickheader');
                } else {
                    $(window).trigger('unstickheader');
                }

                $(window).trigger('positionnav');
                $(window).trigger('positionsearch');
            });

            $(window).on('stickheader', function () {
                let currentHeight = siteHeader.css('height'),
                headerHeight = currentHeight ?  Number(currentHeight.replace(/[^0-9]*/g,'')) : 0;

                $('#main-wrapper')
                    .addClass('site-header-container--fixed')
                    .css({paddingTop: headerHeight});
                siteHeader.addClass('site-header--fixed');

                // when it sticks, it shrinks
                siteHeader.addClass('site-header--scaled');
            });

            $(window).on('unstickheader', function () {
                $('#main-wrapper')
                    .removeClass('site-header-container--fixed')
                    .css({paddingTop: ''});
                siteHeader.removeClass('site-header--fixed');
                // when it unsticks, it unshrinks
                siteHeader.removeClass('site-header--scaled');
            });

            $('.js-mobile-nav-toggle').on('click', function () {
                if ($('#' + $(this).attr('aria-controls')).attr('aria-expanded') === 'false') {
                    that.closeMenuItem('search');
                    that.openMenuItem('nav');
                } else {
                    that.closeMenuItem('nav');
                }
                $('.main-nav__list').scrollTop(0);
            });

            $('.js-mobile-search-toggle').on('click', function () {
                if ($('#' + $(this).attr('aria-controls')).attr('aria-expanded') === 'false') {
                    that.closeMenuItem('nav');
                    that.openMenuItem('search');
                    $('#search-box-mobile').focus();
                } else {
                    that.closeMenuItem('search');
                }
            });

            $(window).on('positionnav', function () {
                let topOffset = that.getCurrentTopOffset();

                $('.main-nav__list').css({
                    maxHeight: 'calc(100% - ' + topOffset + 'px)',
                    top: topOffset
                });
            });

            $(window).on('positionsearch', function () {

                let pagePosition = siteHeader.height() + $(window).scrollTop() - $('#main-wrapper').offset().top;
                let menuPosition = siteHeader.height();

                let topOffset = Math.max(pagePosition, menuPosition);

                $('.search-box--mobile').css({top: topOffset + 28});
            });

            $(window).on('headerautofixing', function () {
                that.headerAutofixing();
            });
        },

        storedScrollPos: 0,

        scrollToMoveHeaderToTop: function() {
            let totalHeight =  scrollPos = parseInt($('.notification-wrapper').css('height'));
            window.scrollTo(0, totalHeight);
        },

        /**
         * Setup auto fixing the header when it passes the top of the window
         * Handles cases where there is content (i.e. the cookie notice) that
         * appears **above** the header itself. Also, it can be called
         * multiple times since it removes the scroll handler before adding it
         * again if needed.
         */
        headerAutofixing: function() {
            var offsetObject = siteHeader.offset(),
                headerOffset = offsetObject ? offsetObject.top : 0,
                /* .height() doesn't handle the box-sizing property correctly, so
                 * do calculations manually
                 */
                currentHeight = siteHeader.css('height'),
                headerHeight = currentHeight ?  Number(currentHeight.replace(/[^0-9]*/g,'')) : 0;

            function autofix() {
                let scrollPosition = $(window).scrollTop(),
                    windowWidth = $(window).width();

                if (windowWidth >= autoThreshold) {
                    return;
                }

                if (scrollPosition >= headerOffset ) {
                    siteHeader.css('position', 'fixed');
                    $('#main-wrapper').css('margin-top', headerHeight);
                } else {
                    siteHeader.css('position', '');
                    $('#main-wrapper').css('margin-top', '');
                }
            }

            // Fix the header if need be first. This means that on iOS when
            // nothing appears above header, the header is already fixed before
            // scrolling and doesn't have a lag.
            autofix();

            $(window).off('scroll', '', autofix);
            $(window).on('scroll', '', autofix);
        },

        openMenuItem: function (menuItemType) {
            if (typeof menuItemType !== 'undefined') {
                $('.mobile-' + menuItemType)
                    .addClass('mobile-layer--open')
                    .attr('aria-expanded', true);
                $('.site-header__button--' + menuItemType).addClass('site-header__button--selected');

                $(window).trigger('position' + menuItemType);
            }
        },

        closeMenuItem: function (menuItemType) {
            $('.mobile-' + menuItemType)
                .removeClass('mobile-layer--open')
                .attr('aria-expanded', false);
            $('.site-header__button--' + menuItemType).removeClass('site-header__button--selected');
        },

        getCurrentTopOffset: function () {
            let topOffset = Math.max($('#main-wrapper').offset().top - $(window).scrollTop(), 0);

            topOffset = topOffset + parseInt(siteHeader.css('height'), 10);

            return topOffset;
        }
    };

    return header;
});

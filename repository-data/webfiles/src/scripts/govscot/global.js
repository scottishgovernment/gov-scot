'use strict';

define([
    'jquery',
    './cookie',
    './component.expand'
], function ($, cookie) {

    // run autoscaling/fixing code up to.
    var autoThreshold = 768,
        siteHeader = $('.site-header'),
        notice = $('#cookie-notice'),
        endX,
        diffX,
        startX,
        scrollPos = 0;

    var global = {

        storedScrollPos: 0,

        scrollToMoveHeaderToTop: function() {
            var totalHeight =  scrollPos = parseInt($('.notification-wrapper').css('height'));
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
              var scrollPosition = $(window).scrollTop(),
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

        /**
         * Checks whether to display the cookie notice
         * Binds click handler to the cookie notice close button
         */
        initCookieNotice: function() {
            var that = this;

            // check whether we need to display the cookie notice
            if (!cookie('cookie-notification-acknowledged')) {
                notice.removeClass('hidden');
            }

            // When clicked, hide notice and set cookie for a year
            notice.on('click', '.notification__close', function(event) {
                event.preventDefault();

                notice.addClass('hidden');
                cookie('cookie-notification-acknowledged', 'yes', 365);
                that.headerAutofixing();

                $(window).trigger('positionnav');
            });
        },

        /**
         * Toggle banners
         */
        initBanner: function(id) {
            var that = this;
            var notice = $('#' + id);

            var bannerClosed = JSON.parse(sessionStorage.getItem(id + '-closed'));
            if (!bannerClosed) {
                notice.find('.notification__close').removeClass('notification__close--minimised');
                notice.find('.notification__extra-content').removeClass('hidden-xsmall');
            }

            notice.on('click', '.notification__close', function(event) {
                event.preventDefault();

                notice.find('.notification__close').toggleClass('notification__close--minimised');
                notice.find('.notification__extra-content').toggleClass('hidden-xsmall');

                bannerClosed = !bannerClosed;
                sessionStorage.setItem(id + '-closed', JSON.stringify(bannerClosed));

                that.headerAutofixing();

                $(window).trigger('positionnav');
            });
        },

        resetSwipeHandler: function() {
            var t = this;

            $('body').off('touchmove touchstart');

            $('body').on('touchstart', function(ev) {
                startX = t.getCoord(ev, 'X');
                diffX = 0;
            });

            $('body').on('touchmove', function(ev) {
                endX = t.getCoord(ev, 'X');
                diffX = endX - startX;

                if (Math.abs(diffX) > 7) {
                     // It's a swipe, prevent this
                    event.preventDefault();
                }
            });
        },

        getCoord: function(e, c) {
            return /touch/.test(e.type) ? (e.originalEvent || e).changedTouches[0]['page' + c] : e['page' + c];
        },

        initHeader: function () {
            var that = this;

            $(window).on('scroll', function () {

                if (window.innerWidth >= autoThreshold) {
                    $(window).trigger('unstickheader');
                    return;
                }

                var pageTop = $('#main-wrapper').offset().top;

                if ($(window).scrollTop() > pageTop) {
                    $(window).trigger('stickheader');
                } else {
                    $(window).trigger('unstickheader');
                }

                $(window).trigger('positionnav');
                $(window).trigger('positionsearch');
            });

            $(window).on('stickheader', function () {
                var currentHeight = siteHeader.css('height'),
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
                var topOffset = that.getCurrentTopOffset();

                $('.main-nav__list').css({
                    maxHeight: 'calc(100% - ' + topOffset + 'px)',
                    top: topOffset
                });
            });

            $(window).on('positionsearch', function () {

                var pagePosition = siteHeader.height() + $(window).scrollTop() - $('#main-wrapper').offset().top;
                var menuPosition = siteHeader.height();

                var topOffset = Math.max(pagePosition, menuPosition);

                $('.search-box--mobile').css({top: topOffset + 28});
            });

        },

        initMourningBanner : function () {
            $.ajax({
                type: 'GET',
                url: '/banners.json'
            })
            .done(function(data) {
                if (data.mourning.display === true) {
                    var bannerHtml = '' +
                        '<div id="mourning-banner" class="notification notification--mourning">' +
                        '    <div class="wrapper">' +
                        '        <div class="notification__main-content">' +
                        '        </div>' +
                        '    </div>' +
                        '</div>';

                    $('.notification-wrapper').append(bannerHtml);
                    if ($('#beta-banner').length > 0) {
                        $('#beta-banner').switchClass('notification--gold', 'notification--light');
                    }
                }
            });
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
            var topOffset = Math.max($('#main-wrapper').offset().top - $(window).scrollTop(), 0);

            topOffset = topOffset + parseInt(siteHeader.css('height'), 10);

            return topOffset;
        },

        compensateAnchorOffsetForStickyElements: function () {
            if (window.location.hash && $(window.location.hash).length) {
                window.setTimeout(function () {
                    window.scrollTo(window.pageXOffset, parseInt($(window.location.hash).offset().top, 10) - 100);
                }, 300);
            }

            $('a[href^="#"]').on('click', function () {
                window.setTimeout(function () {
                    window.scrollTo(window.pageXOffset, window.pageYOffset - 100);
                }, 10);
            });
        },

        init: function () {
            this.initHeader();
            this.initMourningBanner();
            this.initCookieNotice();
            this.initBanner('beta-banner');
            this.initBanner('staging-banner');
            this.resetSwipeHandler();

            // IE8 doesn't support window.pageYOffset
            if (window.pageXOffset !== undefined) {
                this.compensateAnchorOffsetForStickyElements();
            }
        }
    };

    global.init();

    return global;
});

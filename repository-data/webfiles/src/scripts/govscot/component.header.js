// HEADER COMPONENT

/* global window */

'use strict';

import $ from 'jquery';

let siteHeader = $('.site-header'),
    autoThreshold = 768;

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
        });

        $(window).on('stickheader', function () {
            // when it sticks, it shrinks
            siteHeader.addClass('site-header--scaled');
        });

        $(window).on('unstickheader', function () {
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
    },

    storedScrollPos: 0,

    scrollToMoveHeaderToTop: function() {
        let totalHeight = parseInt($('.notification-wrapper').css('height'));
        window.scrollTo(0, totalHeight);
    },

    openMenuItem: function (menuItemType) {
        if (typeof menuItemType !== 'undefined') {
            $('.mobile-' + menuItemType)
                .addClass('mobile-layer--open')
                .attr('aria-expanded', true);
            $('.site-header__button--' + menuItemType).addClass('site-header__button--selected');

            $(window).trigger('position' + menuItemType);
        }

        $('.js-mobile-' + menuItemType + '-overlay').addClass('mobile-layer__overlay--open');
        $('body').addClass('has-overlay');
    },

    closeMenuItem: function (menuItemType) {
        $('.mobile-' + menuItemType)
            .removeClass('mobile-layer--open')
            .attr('aria-expanded', false);
        $('.site-header__button--' + menuItemType).removeClass('site-header__button--selected');

        $('.js-mobile-' + menuItemType + '-overlay').removeClass('mobile-layer__overlay--open');
        $('body').removeClass('has-overlay');
    }
};

export default header;

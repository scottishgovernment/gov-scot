// HEADER COMPONENT

/* global window */

'use strict';

import $ from 'jquery';

const header = {
    init: function () {
        let that = this;

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
                $('#search-box-mobile')[0].focus();
            } else {
                that.closeMenuItem('search');
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

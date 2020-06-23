// GLOBAL

/* global window, document */

'use strict';

import $ from 'jquery';
import banner from './component.banner';
import cookieNotice from './component.cookie-notice';
import expand from './component.expand';
import header from './component.header';
import showHide from './component.showhide';
import './component.google-analytics';
import './component.feedback';
import './component.payment';
import NotificationBanner from './component.notification';
import Accordion from '../../scss/design-system-preview/components/accordion/accordion';
import SideNavigation from '../../scss/design-system-preview/components/side-navigation/side-navigation';
import MobileMenu from '../../scss/design-system-preview/components/site-navigation/site-navigation';

const global = {
    compensateAnchorOffsetForStickyElements: function () {
        // IE8 doesn't support window.pageYOffset
        if (window.pageXOffset !== undefined) {
            if (window.location.hash && $(window.location.hash).length) {
                window.setTimeout(function () {
                    window.scrollTo(window.pageXOffset, parseInt($(window.location.hash).offset().top, 10) - 100);
                }, 300);
            }

            $('a[href^="#"]:not(.js-display-toggle):not(.js-trigger)').on('click', function () {
                window.setTimeout(function () {
                    window.scrollTo(window.pageXOffset, window.pageYOffset - 100);
                }, 10);
            });
        }
    },

    svgFallback: function () {
        if (!document.implementation.hasFeature('http://www.w3.org/TR/SVG11/feature#Image', '1.1')) {
            $('img[src$=".svg"]').each(function () {
                $(this).attr('src', $(this).attr('src').replace(/\.svg$/, '.png'));
            });
        }
    },

    initPubsub: function () {
        const o = $({});

        $.subscribe = function() {
            o.on.apply(o, arguments);
        };

        $.unsubscribe = function() {
            o.off.apply(o, arguments);
        };

        $.publish = function() {
            o.trigger.apply(o, arguments);
        };

        window.pubsub = $;
    },

    initNotifications: function () {
        const notificationBanners = [].slice.call(document.querySelectorAll('[data-module="ds-notification"]'));
        notificationBanners.forEach(notificationBanner => new NotificationBanner(notificationBanner).init());
    },

    initAccordions: function () {
        const accordionModules = [].slice.call(document.querySelectorAll('[data-module="ds-accordion"],[data-module="ds_accordion"]'));
        accordionModules.forEach(accordion => new Accordion(accordion).init());
    },

    initSideNavigation: function () {
        const sideNavigationModules = [].slice.call(document.querySelectorAll('[data-module="ds-side-navigation"]'));
        sideNavigationModules.forEach(sideNavigation => new SideNavigation(sideNavigation).init());
    },

    initMobileMenu: function () {
        const mobileMenuModules = [].slice.call(document.querySelectorAll('[data-module="ds-site-navigation"]'));
        mobileMenuModules.forEach(mobileMenu => new MobileMenu(mobileMenu).init());
    },

    init: function () {
        document.documentElement.classList.add('js-enabled');

        this.initPubsub();
        this.svgFallback();
        this.compensateAnchorOffsetForStickyElements();

        banner.init('staging-banner');
        cookieNotice.init();
        expand.init();
        header.init();
        this.initNotifications();
        this.initAccordions();
        this.initSideNavigation();
        this.initMobileMenu();

        showHide.init();
    }
};

global.init();

export default global;

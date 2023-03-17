// GLOBAL

/* global window, document */

'use strict';

import './usertype';
import '../vendor/polyfills';

import feedback from './component.feedback';
import Notification from './component.notification';
import ToggleLink from './component.toggle-link';
import UpdateHistory from './component.update-history';
import storage from '../../../node_modules/@scottish-government/pattern-library/src/base/tools/storage/storage';

import '../../../node_modules/@scottish-government/pattern-library/src/all';

const global = {
    init: function () {
        document.documentElement.classList.add('js-enabled');

        feedback.init();
        this.setInitialCookiePermissions();
        this.initDesignSystemComponents();

        //non-DS code for notification banner
        const notificationBanners = [].slice.call(document.querySelectorAll('[data-module="ds-notification"]'));
        notificationBanners.forEach(notificationBanner => new Notification(notificationBanner, true).init());

        this.addTracking();
        this.checkVideoConsent();
    },

    addTracking: function () {
        if (window.DS.tracking) {
            window.DS.tracking.add.backToTopEvent = function (scope = document) {
                const backToTops = [].slice.call(scope.querySelectorAll('.ds_back-to-top__button'));
                backToTops.forEach(backToTop => {
                    if (!backToTop.classList.contains('js-has-tracking-event')) {
                        backToTop.addEventListener('click', () => {
                            window.dataLayer = window.dataLayer || [];
                            window.dataLayer.push({
                                event: 'backToTop',
                                scrollDepthAbs: window.scrollY,
                                scrollDepthRel: +(window.scrollY / window.innerHeight).toFixed(3)
                            });
                        });
                        backToTop.classList.add('js-has-tracking-event');
                    }
                });
            };

            window.DS.tracking.init();
        }
    },

    initDesignSystemComponents: function () {
        const backToTopEl = document.querySelector('[data-module="ds-back-to-top"]');
        if (backToTopEl) {
            const backToTop = new window.DS.components.BackToTop(backToTopEl);
            backToTop.init();
        }

        // need to preprocess accordion items to group them
        const accordionItems = [].slice.call(document.querySelectorAll('.ds_accordion-item'));
        const groups = [];
        let groupItems = [];
        accordionItems.forEach(accordionItem => {
            if(accordionItem.parentNode.classList.contains('ds_accordion')) {
                return;
            }
            groupItems.push(accordionItem);
            if (!(accordionItem.nextElementSibling && accordionItem.nextElementSibling.classList.contains('ds_accordion-item'))) {
                groups.push(groupItems);
                groupItems = [];
            }
        });
        groups.forEach(groupItems => {
            const wrapper = document.createElement('div');
            wrapper.classList.add('ds_accordion');
            wrapper.dataset.module = 'ds-accordion';

            if (groupItems.length > 1) {
                const openAllButton = document.createElement('button');
                openAllButton.setAttribute('class', 'ds_link  ds_accordion__open-all  js-open-all');
                openAllButton.innerHTML = 'Open all <span class="visually-hidden">sections</span>';
                wrapper.appendChild(openAllButton);
            }

            groupItems[0].parentNode.insertBefore(wrapper, groupItems[0]);
            groupItems.forEach(item => {
                wrapper.appendChild(item);
            });
        });

        const accordions = [].slice.call(document.querySelectorAll('[data-module="ds-accordion"]'));
        accordions.forEach(accordion => new window.DS.components.Accordion(accordion).init());

        const aspectBoxes = [].slice.call(document.querySelectorAll('.ds_aspect-box:not(.ds_aspect-box--fallback)'));
        aspectBoxes.forEach(aspectBox => new window.DS.components.AspectBox(aspectBox).init());

        const autocompletes = [].slice.call(document.querySelectorAll('[data-module="ds-autocomplete"]'));
        autocompletes.forEach(autocomplete => {
            let url = window.location.origin + document.getElementById('site-root-path').value;
            url += '/search/suggestions?partial_query=';
            const mapping = ppp => {
                const suggestionsObj = JSON.parse(ppp.responseText);
                return suggestionsObj.map(suggestionsObj => ({
                    key: suggestionsObj,
                    displayText: suggestionsObj,
                    weight: null,
                    type: null,
                    category: null
                }));
            };
            const autocompleteModule = new window.DS.components.Autocomplete(autocomplete, url, { suggestionMappingFunction: mapping });
            autocompleteModule.init();
        });

        const cookieNotificationEl = document.querySelector('[data-module="ds-cookie-notification"]');
        if (cookieNotificationEl) {
            const cookieNotification = new window.DS.components.CookieNotification(cookieNotificationEl);
            cookieNotification.init();
        }

        // this one is handled differently because it applies an event to the whole body and we only want that event once
        const hidePageButtons = [].slice.call(document.querySelectorAll('.ds_hide-page'));
        if (hidePageButtons.length) {
            const hidePage = new window.DS.components.HidePage();
            hidePage.init();
        }

        const mobileMenus = [].slice.call(document.querySelectorAll('[data-module="ds-mobile-navigation-menu"]'));
        mobileMenus.forEach(mobileMenu =>  new window.DS.components.MobileMenu(mobileMenu).init());

        const searchBoxes = [].slice.call(document.querySelectorAll('[data-module="ds-site-search"]'));
        searchBoxes.forEach(searchBox => new window.DS.components.CollapsibleSearchBox(searchBox).init());

        const sideNavigations = [].slice.call(document.querySelectorAll('[data-module="ds-side-navigation"]'));
        sideNavigations.forEach(sideNavigation => new window.DS.components.SideNavigation(sideNavigation).init());

        const tables = [].slice.call(document.querySelectorAll('table[data-smallscreen]'));
        if (tables.length) {
            const mobileTables = new window.DS.components.MobileTables();
            mobileTables.init();
        }

        const updateHistory = document.querySelector('[data-module="gov-update-history"]');
        if (updateHistory) {
            const updateHistoryModule = new UpdateHistory(updateHistory);
            updateHistoryModule.init();
        }

        const toggleLinks = [].slice.call(document.querySelectorAll('[data-module="gov-toggle-link"]'));
        toggleLinks.forEach(toggleLink => new ToggleLink(toggleLink).init());
    },

    setInitialCookiePermissions: function () {
        const permissionsString = storage.getCookie('cookiePermissions') || '';

        if (!storage.isJsonString(permissionsString)) {
            const permissions = {};
            permissions.statistics = true;
            permissions.preferences = true;

            storage.setCookie(storage.categories.necessary,
                'cookiePermissions',
                JSON.stringify(permissions)
            );
        }
    },

    checkVideoConsent: function () {
        const videoElements = [].slice.call(document.querySelectorAll('.youtube-embed-wrapper'));

        videoElements.forEach(videoElement => {

            if (storage.hasPermission(storage.categories.marketing)) {
                videoElement.innerHTML = `
                <iframe width="${videoElement.dataset.width}" height="${videoElement.dataset.height}" src="${videoElement.dataset.src}" frameborder="0" allowfullscreen></iframe>`;
            } else {
                videoElement.innerHTML = `
                    <div class="youtube-embed-wrapper__consent">
                        <h2 class="ds_h3">We need your permission to load YouTube content</h2>
                        <p>This video is hosted on YouTube and we need your permission to load it.</p>
                        <p>It may use cookies and/or other technologies not covered by <a href="/cookies">gov.scot cookie preferences</a>.</p>
                        <p>You should read <a href="https://policies.google.com/privacy?hl=en">Google's privacy policy</a>  before you agree.</p>
                        <p>Select the 'accept and continue' button to load the content.</p>
                        <button class="ds_button  ds_!_margin-bottom--2  js-video-opt-in" type="button">Accept and continue</button>
                    </div>
                `;

                videoElement.addEventListener('click', event => {
                    if (event.target.classList.contains('js-video-opt-in')) {

                        const permissionsString = storage.getCookie('cookiePermissions') || '';

                        let permissions;

                        if (!storage.isJsonString(permissionsString)) {
                            permissions = {};


                            storage.setCookie(storage.categories.necessary,
                                'cookiePermissions',
                                JSON.stringify(permissions)
                            );
                        } else {
                            permissions = JSON.parse(permissionsString);
                        }

                        permissions.marketing = true;

                        storage.setCookie(storage.categories.necessary,
                            'cookiePermissions',
                            JSON.stringify(permissions)
                        );

                        this.checkVideoConsent();
                    }
                });
            }
        });
    }
};

global.init();

export default global;

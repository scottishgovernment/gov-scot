// PUBLICATION FORMAT

/* global window, document, XMLHttpRequest */

'use strict';

import temporaryFocus from '../../../node_modules/@scottish-government/pattern-library/src/base/tools/temporary-focus/temporary-focus';
const PolyPromise = require('../vendor/promise-polyfill').default;

const publicationPage = {
    init: function () {
        this.initAsyncNavigation();
    },

    initAsyncNavigation: function () {
        this.pages = {};
        this.pages[window.location.href] = document.documentElement.outerHTML;

        document.addEventListener('click', event => {
            if (event.target.classList.contains('js-publication-navigation')) {
                event.preventDefault();

                const url = event.target.href;

                this.loadSubPageHtml(url)
                    .then(value => {
                        const tempDiv = document.createElement('div');
                        tempDiv.innerHTML = value;

                        const newPageContent = tempDiv.querySelector('.js-content-wrapper');
                        const newPageSeqNav = tempDiv.querySelector('.ds_sequential-nav');
                        const newSideNav = tempDiv.querySelector('.ds_side-navigation__list');

                        const contentElement = document.querySelector('.js-content-wrapper');
                        const seqNavElement = document.querySelector('.ds_sequential-nav');
                        const sideNavElement = document.querySelector('.ds_side-navigation__list');

                        contentElement.innerHTML = newPageContent.innerHTML;
                        seqNavElement.innerHTML = newPageSeqNav.innerHTML;
                        sideNavElement.innerHTML = newSideNav.innerHTML;

                        const rect = contentElement.getBoundingClientRect();
                        if (rect.top + 32 > window.innerHeight) {
                            window.scrollTo(window.scrollX, window.scrollY + rect.top / 2);
                        } else if (rect.top < 0) {
                            contentElement.scrollIntoView();
                        }

                        temporaryFocus(contentElement);

                        // Update the URL and history
                        if (window.history.pushState) {
                            window.history.pushState({},'', url);
                        }
                    })
                    .catch(error => {
                        console.log(error);
                        window.location = event.target.href;
                    });
            }
        });
    },

    loadSubPageHtml(url) {
        return new PolyPromise((resolve, reject) => {
            if (this.pages[url]) {
                resolve(this.pages[url]);
            } else {
                const request = new XMLHttpRequest();

                request.onreadystatechange = () => {
                    if (request.readyState !== 4) {
                        return;
                    }

                    if (request.status >= 200 && request.status < 300) {
                        this.pages[url] = request.responseText;
                        resolve(request.responseText);
                    } else {
                        reject({
                            status: request.status,
                            statusText: request.statusText
                        });
                    }
                };

                request.open('GET', url, true);
                request.send();
            }
        });
    }
};

window.format = publicationPage;
window.format.init();

export default publicationPage;

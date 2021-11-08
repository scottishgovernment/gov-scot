// PUBLICATION FORMAT

/* global window, document, XMLHttpRequest */

'use strict';

import $ from 'jquery';

const publicationPage = {},
    pages = {},
    isMobile = $('.toc-mobile-trigger').is(':visible');

publicationPage.init = function() {
    this.initAsyncNavigation();
};

/**
 * Init the async navigation in progressive enhancement approach
 */
publicationPage.initAsyncNavigation = function () {
    /* We initially prevent default action, but if we encounter an error
        * loading the data/page then we fallback to redirecting to the clicked
        * link.
        */

    $('.js-content-wrapper').on('click', '.js-contents a, .js-previous, .js-next', function (event) {
        const linkEl = $(this),
            url = linkEl.attr('href');
        if (url) {
            event.preventDefault();

            publicationPage.loadSubPageHtml(url)
                .done(function(){
                    linkEl.blur();
                    if (isMobile && linkEl.hasClass('page-group__link')) {
                        this.sideNavigationModule.closeSideNav();
                    }
                })
                .fail(function(){
                    window.location = url;
                });
        }
    });

    /**
     * Catch changes in state
     */
    window.onpopstate = function(e){
        if(e.state){
            const url = e.target.location.pathname;
            publicationPage.loadSubPageHtml(url, false)
                .fail(function(){
                    window.location = url;
                });
        }
    };

    /**
     * Add a class to pinpoint the top edge of the subpage for scrolling
     * to the right location after page loaded.
     */
    if ( isMobile ) {
        $('.publication-content').addClass('js-subpage-top-edge');
    } else {
        $('.js-sticky-header-position ').addClass('js-subpage-top-edge');
    }
};

/**
 * Load the HTML for the page and caches it in a local variable.
 * Returns a promise that will return successful if the request was
 * successful or there already was a hit in the cache.
 */
publicationPage.loadHtml = function (url) {

    const deferred = $.Deferred();

    if (pages[url]) {
        deferred.resolve(pages[url]);
    } else {
        let request = new XMLHttpRequest();
        request.open('GET', url, true);

        request.onreadystatechange = function() {
            if (this.readyState === 4) {
                if (this.status >= 200 && this.status < 400) {
                    pages[url] = this.responseText;
                    deferred.resolve(pages[url]);
                } else {
                    deferred.reject();
                }
            }
        };

        request.send();
    }

    return deferred.promise();
};

/**
 * Loads the given subpage
 * @param url
 * @param updateHistory
 */
publicationPage.loadSubPageHtml = function (url, updateHistory) {
    const deferred = $.Deferred();

    if (typeof(updateHistory) === 'undefined') {
        updateHistory = true;
    }

    this.loadHtml(url)
        .done(function (data) {
            const bodyContentWrapper = document.querySelector('.js-content-wrapper');

            // parse the HTML string and extract the parts we want
            const element = document.createElement('div');
            element.insertAdjacentHTML('beforeend', data);
            const newContents = element.querySelector('.js-content-wrapper');
            const newStickyHeader = element.querySelector('.sticky-document-info');

            // insert new HTML
            bodyContentWrapper.innerHTML = newContents.innerHTML;
            document.querySelector('.sticky-document-info').innerHTML = newStickyHeader.innerHTML;
            $('.js-mobile-toc-trigger-close').hide();

            // scroll to top of content
            const targetOffset = $('.js-content-wrapper').offset().top - parseInt($('.sticky-document-info').height(), 10);
            if (window.scrollY > targetOffset) {
                window.scrollTo(window.scrollX, targetOffset);
            }

            // Update the URL and history
            if (updateHistory && window.history.pushState) {
                window.history.pushState({
                    'html': 'inner html?',
                    'pageTitle': 'updated page title'
                },'', url);
            }
        })
        .fail(function () {
            deferred.reject();
        });

    return deferred.promise();
};

window.format = publicationPage;
window.format.init();

export default publicationPage;

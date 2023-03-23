'use strict';

import PromiseRequest from '../../../node_modules/@scottish-government/pattern-library/src/base/tools/promise-request/promise-request';
import temporaryFocus from '../../../node_modules/@scottish-government/pattern-library/src/base/tools/temporary-focus/temporary-focus';

class AsyncSearch {
    constructor(asyncSearch) {
    }

    init() {
        this.resultsContainer = document.getElementById('search-results');
        this.bindEvents();
    }

    bindEvents() {
        function querystringToObject(querystring) {
            const search = querystring.substring(1);
            return JSON.parse('{"' + search.replace(/&/g, '","').replace(/=/g, '":"') + '"}', function (key, value) { return key === "" ? value : decodeURIComponent(value); });
        }

        function objectToQuerystring(object) {
            const string = [];
            for (const prop in object) {
                if (object.hasOwnProperty(prop)) {
                    string.push(encodeURIComponent(prop) + "=" + encodeURIComponent(object[prop]));
                }
            }
            return '?' + string.join("&");
        }

        this.resultsContainer.addEventListener('click', event => {
            if (event.target.classList.contains('ds_pagination__link')) {
                event.preventDefault();

                const targetHref = event.target.href;

                var targetUrlParams = querystringToObject(targetHref);
                var currentUrlParams = querystringToObject(window.location.search);

                let page = 1;

                if (targetUrlParams.page) {
                    page = targetUrlParams.page;
                }

                currentUrlParams.page = page;

                const pageUrl = `${window.location.pathname}${objectToQuerystring(currentUrlParams)}`;
                const pathElements = window.location.pathname.split('/');
                const lastPathElement = pathElements[pathElements.length - 2];
                const urlWithReplacement = window.location.pathname.replace(lastPathElement, lastPathElement + 'results');
                const resultsUrl = `${urlWithReplacement}${objectToQuerystring(currentUrlParams)}`;
                this.loadResults(resultsUrl)
                    .then(value => {
                        window.history.pushState({page:page}, '', pageUrl);
                        this.populateResults(value.responseText);
                    })
                    .catch(error => {
                        console.log('Failed to fetch additional results. Navigating normally. ', error);
                        window.location = targetHref;
                    });
            }
        });

        window.addEventListener('popstate', event => {
            if (event.state) {
                const resultsUrl = window.location.href.replace('/funnelback?', '/funnelbackresults?');
                this.loadResults(resultsUrl)
                    .then(value => {
                        this.populateResults(value.responseText);
                    })
                    .catch(error => {
                        console.log('Failed to fetch additional results.', error);
                    });
            }
        });
    }

    loadResults(resultsUrl) {
        const promiseRequest = PromiseRequest(resultsUrl);
        return promiseRequest;
    }

    populateResults(html) {
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = html;
        if (!!tempDiv.querySelector('#search-results')) {
            this.resultsContainer.innerHTML = tempDiv.querySelector('#search-results').innerHTML;
        } else {
            this.resultsContainer.innerHTML = html;
        }
        const rect = this.resultsContainer.getBoundingClientRect();
        window.scrollTo(window.scrollX, document.documentElement.scrollTop + rect.top);

        window.DS.tracking.init(this.resultsContainer);
    }
}

export default AsyncSearch;

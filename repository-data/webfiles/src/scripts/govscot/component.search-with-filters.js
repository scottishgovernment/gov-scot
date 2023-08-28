// SEARCH FILTERS COMPONENT
// Contains functionality for the filterable search pages

/* global window, document */

'use strict';

import searchUtils from './search-utils';
import DSDatePicker from '../../../node_modules/@scottish-government/pattern-library/src/components/date-picker/date-picker';
import GovFilters from './component.filters';
import breakpointCheck from '../../../node_modules/@scottish-government/pattern-library/src/base/utilities/breakpoint-check/breakpoint-check';
import PromiseRequest from '../../../node_modules/@scottish-government/pattern-library/src/base/tools/promise-request/promise-request';

window.dataLayer = window.dataLayer || [];

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    let regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

// simple replacement of jQuery.extend
function extend(a, b){
    for (let key in b) {
        if (b.hasOwnProperty(key)) {
            a[key] = b[key];
        }
    }
    return a;
}

class SearchWithFilters {
    constructor(settings) {
        this.filtersContainer = document.getElementById('filters');
        this.resultsContainer = document.querySelector('#search-results');

        this.settings = {
            maxDate: new Date(),
            minDate: new Date(1999, 5, 1)
        };

        this.settings = extend(this.settings, settings);
    }

    init() {
        if (!this.filtersContainer) {
            return;
        }

        const govFilterEl = document.querySelector('[data-module="gov-filters"]');
        this.govFilters = new GovFilters(govFilterEl);
        this.govFilters.init();

        this.searchParams = this.gatherParams(true);

        this.enableJSFilters();
        this.attachEventHandlers();
        this.initDateFilters();
        this.searchUtils = searchUtils;
        this.submitSearch = options => {
            options = options || {};
            if (options.changingPage) { this.isChangingPage = true; }
            if (options.popstate) { this.isPopstate = true; }
            this.doSearch();
        };
        this.updateFilterCounts(this.gatherParams());
    }

    attachEventHandlers () {
        let t;

        const fieldGroups = [].slice.call(this.filtersContainer.querySelectorAll('.ds_field-group'));
        fieldGroups.forEach(fieldGroup => {
            const inputs = [].slice.call(fieldGroup.querySelectorAll('input[type=checkbox]'));

            inputs.forEach(input => {
                input.addEventListener('change', () => {
                    let containerType = input
                        .closest('.ds_accordion-item')
                        .querySelector('.ds_accordion-item__header-button')
                        .innerText
                        .toLowerCase();

                    dataLayer.push({
                        'filter': containerType,
                        'interaction': input.checked ? 'check' : 'uncheck',
                        'value': input.value,
                        'event': 'filters'
                    });

                    // If on mobile don't do the search automatically.
                    if (breakpointCheck('medium')) {
                        clearTimeout(t);

                        // do search on a small timeout to allow user to select multiple items without making multiple requests
                        t = setTimeout(() => {
                            delete this.searchParams.page;
                            this.submitSearch();
                        }, 300);
                    }
                });
            });
        });

        document.querySelector('.js-filter-search-submit').addEventListener('click', (event) => {
            event.preventDefault();
            this.submitSearch();
        });


        // clear filters
        this.resultsContainer.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-clear-filters')) {
                event.preventDefault();

                dataLayer.push({
                    'event': 'filters-clear'
                });

                // clear all filters
                const checkboxes = [].slice.call(this.filtersContainer.querySelectorAll('input[type="checkbox"]'));
                const textInputs = [].slice.call(this.filtersContainer.querySelectorAll('input[type="text"]'));

                checkboxes.forEach(element => element.checked = false);
                textInputs.forEach(element => element.value = '');

                this.clearErrors();

                delete this.searchParams.page;

                this.submitSearch();
            }
        });

        const paginationLinks = [].slice.call(document.querySelectorAll('.ds_pagination__page'));
        paginationLinks.forEach(link => {
            link.addEventListener('click', (event) => {
                event.preventDefault();

                this.searchParams.page = getParameterByName('page', event.target.href);
                this.submitSearch({changingPage: true});
            });
        })

        window.onpopstate = () => {
            this.searchParams.page = getParameterByName('page');
            this.submitSearch({
                changingPage: true,
                popstate: true
            });
        };
    }

    clearErrors() {
        // clear any error states on filter fields
        // quick & dirty, will be replaced by enterprise search
        const inputs = [].slice.call(this.filtersContainer.querySelectorAll('.ds_input--error'));
        const messages = [].slice.call(this.filtersContainer.querySelectorAll('.ds_question__error-message'));
        const questions = [].slice.call(this.filtersContainer.querySelectorAll('.ds_question--error'));

        inputs.forEach(element => {
            element.classList.remove('ds_input--error');
            element.removeAttribute('aria-invalid');
        });
        messages.forEach(element => element.parentNode.removeChild(element));
        questions.forEach(element => element.classList.remove('ds_question--error'));
    }

    doSearch() {
        if (!this.resultsContainer) {
            return;
        }

        // do not proceed if there are errors
        if (document.querySelectorAll('#filters [aria-invalid="true"]').length) {
            return false;
        }

        if (!this.isChangingPage) {this.searchParams.page = 1;}

        let currentParams = this.gatherParams();
        let newQueryString = searchUtils.getNewQueryString(currentParams);

        this.resultsContainer.classList.add('js-loading-inactive');
        this.filtersContainer.classList.add('js-loading-inactive');

        this.loadResults(window.location.pathname + newQueryString)
            .then(data => {
                // skip this if we're on popstate
                if (this.isPopstate) {
                    delete this.isPopstate;
                } else {
                    // update querystring
                    try {
                        window.history.pushState('', '', newQueryString);
                    } catch(error) {
                        // history API not supported
                    }
                }

                const clearFiltersElement = document.querySelector('.js-clear-filters');

                // temporary container for the search results so we can query a DOM tree
                const tempContainer = document.createElement('div');
                tempContainer.innerHTML = data.response;

                this.resultsContainer.innerHTML = tempContainer.querySelector('#search-results').innerHTML;

                // update display status of "clear" buttons
                if (clearFiltersElement) {
                    if (this.hasActiveSearch(currentParams)) {
                        clearFiltersElement.classList.remove('hidden');
                    } else {
                        clearFiltersElement.classList.add('hidden');
                    }
                }

                // remove "loading" message
                this.resultsContainer.classList.remove('js-loading-inactive');
                this.filtersContainer.classList.remove('js-loading-inactive');

                this.updateFilterCounts(currentParams);

                this.govFilters.closeFilters();

                // scroll to the top of the page if we are changing page
                if (this.isChangingPage) {
                    let pageContent = document.getElementById('main-content');
                    window.scrollTo(window.scrollX, pageContent.offsetTop + pageContent.offsetParent.offsetTop);
                }
                this.isChangingPage = false;
            })
            .catch(error => {
                window.location.search = newQueryString;
            });
    }

    enableJSFilters () {
        [].slice.call(document.querySelectorAll('.ds_field-group--checkboxes input[type="radio"]')).forEach((item => {
            item.type = 'checkbox';
            item.classList.remove('ds_radio__input');
            item.classList.add('ds_checkbox__input');
            item.dataset.form = item.dataset.form.replace('radio-', 'checkbox-');

            const label = item.nextElementSibling;
            label.classList.remove('ds_radio__label');
            label.classList.add('ds_checkbox__label');

            const parent = item.parentNode;
            parent.classList.remove('ds_radio');
            parent.classList.remove('ds_radio--small');
            parent.classList.add('ds_checkbox');
            parent.classList.add('ds_checkbox--small');
        }));

        // populate checkboxes from searchParams
        const checkedOnLoad = [].slice.call(document.querySelectorAll('.ds_field-group--checkboxes input[data-checkedonload]'));
        checkedOnLoad.forEach(checkbox => {
            checkbox.checked = true;
        })

        // date pickers display
        const datePickerButtons = [].slice.call(document.querySelectorAll('.js-show-calendar'));
        datePickerButtons.forEach(button => {
            button.classList.remove('hidden');
            button.classList.remove('hidden--hard');
        });

        // filter button display
        document.querySelector('#filter-actions').classList.add('filter-actions');
        document.querySelector('#filter-actions').classList.add('visible-xsmall');
    }

    gatherParams (initial) {
        let searchParams = this.searchParams || {};
        const searchTermElement = document.getElementById('filters-search-term');

        // KEYWORD / TERM
        if (window.location.href.indexOf('/search/') !== -1) {
            searchParams.q = encodeURIComponent(searchTermElement.value);
            searchParams.cat = 'sitesearch';
        } else {
            searchParams.term = encodeURIComponent(searchTermElement.value);
            searchParams.cat = 'filter';
        }

        // PAGINATION
        if (initial) {
            searchParams.page = getParameterByName('page') || 1;
            searchParams.size = getParameterByName('size') || 10;
        }

        // TOPICS
        searchParams.topics = [];
        const checkedTopicCheckboxes = [].slice.call(document.querySelectorAll('input[name="topics"]:checked'));
        checkedTopicCheckboxes.forEach(checkbox => {
            searchParams.topics.push(checkbox.value);
        });
        if (searchParams.topics.length === 0) {
            delete searchParams.topics;
        }

        // PUBLICATION TYPES
        searchParams.publicationTypes = [];
        const checkedPublicationTypeCheckboxes = [].slice.call(document.querySelectorAll('input[name="publicationTypes"]:checked'));
        checkedPublicationTypeCheckboxes.forEach(checkbox => {
            searchParams.publicationTypes.push(checkbox.value);
        });
        if (searchParams.publicationTypes.length === 0) {
            delete searchParams.publicationTypes;
        }

        // DATE RANGES
        if (document.getElementById('filter-date-range')) {
            searchParams.date = searchParams.date || {};

            searchParams.date.begin = encodeURI(document.getElementById('date-from').value);
            searchParams.date.end = encodeURI(document.getElementById('date-to').value);
        }

        return searchParams;
    }

    /**
    * Determines whether or not there is an active search
    * @param params
    * @returns {boolean}
    */
    hasActiveSearch(params) {
        let hasActiveSearch = false;

        for(let key in params) {
            if (!params.hasOwnProperty(key)) {
                continue;
            }

            let value = params[key];
            if (key === 'topics' || key === 'term' || key === 'publicationTypes') {
                if (value !== '') {
                    hasActiveSearch = true;
                }
            } else if (key === 'date' && (value.begin || value.end)) {
                hasActiveSearch = true;
            }
        }

        return hasActiveSearch;
    }

    initDateFilters() {
        const imagePath = document.getElementById('imagePath').value;
        const fromDatePickerElement = document.querySelector('#fromDatePicker');
        const toDatePickerElement = document.querySelector('#toDatePicker');
        const fromDatePicker = new DSDatePicker(fromDatePickerElement, { imagePath: imagePath, maxDate: new Date() });
        const toDatePicker = new DSDatePicker(toDatePickerElement, { imagePath: imagePath, maxDate: new Date() });

        fromDatePicker.init();
        toDatePicker.init();

        if (fromDatePickerElement) {
            fromDatePickerElement.addEventListener('change', () => {
                if (this.validateDateInput(fromDatePicker.inputElement)) {
                    toDatePicker.inputElement.dataset.mindate = fromDatePicker.inputElement.value;
                    if (breakpointCheck('medium')) {
                        delete this.searchParams.page;
                        this.submitSearch();
                    }
                }
            });
        }

        if (toDatePickerElement) {
            toDatePickerElement.addEventListener('change', () => {
                if (this.validateDateInput(toDatePicker.inputElement)) {
                    fromDatePicker.inputElement.dataset.maxdate = toDatePicker.inputElement.value;
                    if (breakpointCheck('medium')) {
                        delete this.searchParams.page;
                        this.submitSearch();
                    }
                }
            });
        }
    }

    loadResults(url) {
        return PromiseRequest(url);
    }

    updateFilterCounts(currentParams) {
        const publicationTypesCount = document.querySelector('.js-publication-types-count');
        const topicsCount = document.querySelector('.js-topics-count');

        if (publicationTypesCount) {
            if (currentParams.publicationTypes) {
                publicationTypesCount.dataset.count = currentParams.publicationTypes.length;
            } else {
                delete publicationTypesCount.dataset.count;
            }
        }

        if (topicsCount) {
            if (currentParams.topics) {
                topicsCount.dataset.count = currentParams.topics.length;
            } else {
                delete topicsCount.dataset.count;
            }
        }
    }

    validateDateInput(element) {
        let isValid = true;

        if (!searchUtils.validateInput(element, [searchUtils.dateRegex])) {
            isValid = false;
        }

        return isValid;
    }
}

export default SearchWithFilters;

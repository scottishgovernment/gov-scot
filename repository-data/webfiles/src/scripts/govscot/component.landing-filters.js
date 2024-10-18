// SEARCH FILTERS COMPONENT
// Contains functionality for the filterable search pages

/* global window, document */

'use strict';

import breakpointCheck from '../../../node_modules/@scottish-government/design-system/src/base/utilities/breakpoint-check/breakpoint-check';
import PromiseRequest from '../../../node_modules/@scottish-government/design-system/src/base/tools/promise-request/promise-request';
import temporaryFocus from '../../../node_modules/@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';
import searchUtils from './search-utils';

window.dataLayer = window.dataLayer || [];

class LandingFilters {
    constructor() {
        this.filtersContainer = document.querySelector('.ds_search-filters');
        this.resultsContainer = document.querySelector('.ds_search-results');

        this.timeoutDelay = 500;
    }

    init() {
        // set up date pickers
        const imagePath = document.getElementById('imagePath').value;
        const datePickers = [].slice.call(document.querySelectorAll('[data-module="ds-datepicker"]'));
        datePickers.forEach(datePicker => new window.DS.components.DSDatePicker(datePicker, {imagePath: imagePath, maxDate: new Date()}).init());

        // set up events
        this.attachEventHandlers();

        // set initialised
        this.filtersContainer.classList.add('js-initialised');
        this.resultsContainer.classList.add('js-initialised');
    }

    attachEventHandlers() {
        let t;

        // checkbox change submits search on medium+ viewports
        const checkboxGroups = [].slice.call(document.querySelectorAll('.ds_search-filters__checkboxes'));
        checkboxGroups.forEach(checkboxGroup => {
            const containerType = checkboxGroup.closest('.ds_accordion-item').querySelector('.ds_accordion-item__button').innerText.toLowerCase();
            const inputs = [].slice.call(checkboxGroup.querySelectorAll('.ds_checkbox__input'));

            inputs.forEach(input => {
                input.addEventListener('change', () => {
                    dataLayer.push({
                        'filter': containerType,
                        'interaction': input.checked ? 'check' : 'uncheck',
                        'value': input.value,
                        'event': 'filters'
                    });

                    if (breakpointCheck('medium')) {
                        clearTimeout(t);

                        t = setTimeout(() => {
                            this.doSearch(undefined, true);
                        }, this.timeoutDelay);
                    };
                });
            });
        });

        // date entry submits search on medium+ viewports
        const fromDatePickerInputElement = document.querySelector('#fromDatePicker .ds_input');
        const toDatePickerInputElement = document.querySelector('#toDatePicker .ds_input');

        if (fromDatePickerInputElement) {
            fromDatePickerInputElement.addEventListener('change', () => {
                if (this.validateDateInput(fromDatePickerInputElement)) {
                    toDatePickerInputElement.dataset.mindate = fromDatePickerInputElement.value;
                    if (breakpointCheck('medium')) {
                        this.doSearch(undefined, true);
                    }
                }
            });
        }

        if (toDatePickerInputElement) {
            toDatePickerInputElement.addEventListener('change', () => {
                if (this.validateDateInput(toDatePickerInputElement)) {
                    fromDatePickerInputElement.dataset.maxdate = toDatePickerInputElement.value;
                    if (breakpointCheck('medium')) {
                        this.doSearch(undefined, true);
                    }
                }
            });
        }

        // Submit filters if search term submitted or apply filters button clicked
        const filtersForm = document.querySelector('#filters');
        if(filtersForm) {
            filtersForm.addEventListener('submit', (event) => {
                event.preventDefault();
                this.doSearch(undefined, true);
            });
        }

        // sort dropdown change submits search
        this.resultsContainer.addEventListener('change', event => {
            if (event.target.classList.contains('js-sort-by')) {
                this.doSearch(undefined, true);
            }
        });

        this.resultsContainer.addEventListener('click', event => {
            // facet button submits search on click
            if (event.target.classList.contains('js-remove-facet')) {
                event.preventDefault();
                this.removeFacet(event.target);
            }

            // "clear filters" button submits search on click
            if (event.target.classList.contains('js-clear-filters')) {
                event.preventDefault();
                this.clearFilters();
            }
            
            // pagination link submits search on click
            if (event.target.classList.contains('ds_pagination__link')) {
                event.preventDefault();
                this.changePage(event.target);
            }
        });

        this.resultsContainer.addEventListener('keypress', event => {
            if (event instanceof KeyboardEvent &&
                (event.key == " " || event.key == "Enter")) {
                // facet button submits search on "space" or "enter" keypress
                if (event.target.classList.contains('js-remove-facet')) {
                    event.preventDefault();
                    this.removeFacet(event.target);
                }

                // "clear filters" button submits search on "space" or "enter" keypress
                if (event.target.classList.contains('js-clear-filters')) {
                    event.preventDefault();
                    this.clearFilters();
                }
            }
        });

        this.filtersContainer.querySelector('.js-apply-filter').addEventListener('click', event => {
            event.preventDefault();
            this.doSearch(undefined, true);
        });

        window.addEventListener('popstate', event => {
            const resultsUrl = window.location.href;
            this.isPopstate = true;
            this.syncFiltersToUrl();
            this.doSearch(resultsUrl);
        });
    }

    clearFilters() {
        const filterInputs = [].slice.call(this.filtersContainer.querySelectorAll('.ds_input, .ds_checkbox__input, .ds_radio__input'));
        filterInputs.forEach(input => {
            if (input.id === 'filters-search-term') {
                // Do nothing - Ignore search input
            } else if (input.type === 'checkbox' || input.type === 'radio') {
                // clear filter checkboxes/radios
                input.checked = false;
            } else {
                // clear filter textboxes
                input.value = '';
                searchUtils.removeError(input.closest('.ds_question'));
            }
        });

        this.doSearch(undefined, true);
    }

    changePage(linkElement) {
        const targetHref = linkElement.href;

        this.doSearch(targetHref);
    }

    convertUrl (url, newSearch) {
        return url.pathname + searchUtils.getNewQueryString(this.gatherParams(url, newSearch));    
    }

    doSearch(url, newSearch = false) {
        // Capture page url
        const pageUrl = window.location.pathname + searchUtils.getNewQueryString(this.gatherParams(url, newSearch));

        if (!url) {
           
            url = this.convertUrl(window.location, newSearch);

            // do not proceed if there are errors
            if (document.querySelectorAll('.ds_search-filters [aria-invalid="true"]').length) {
                return false;
            }
        } else {
            try {
                const definedUrl = new URL(url);
                url = this.convertUrl(definedUrl, newSearch);
            }  catch (error) {
                // invalid url
                return false;
            }
        
        }

        // disable search containers
        const containersToDisable = [].slice.call(document.querySelectorAll('.ds_search-filters, .ds_search-results'));
        containersToDisable.forEach(container => container.classList.add('js-disabled-search'));

        this.loadResults(url)
            .then(data => {
                if (this.isPopstate) {

                    delete this.isPopstate;


                } else {
                    try {
                        // update querystring
                        window.history.pushState('', '', pageUrl);

                    } catch (error) {
                        // history API not supported
                    }
                }

                // temporary container for the search results so we can query a DOM tree
                const tempContainer = document.createElement('div');
                tempContainer.innerHTML = data.response;

                if (!!tempContainer.querySelector('.ds_search-results')) {
                    this.resultsContainer.innerHTML = tempContainer.querySelector('.ds_search-results').innerHTML;
                } else {
                    this.resultsContainer.innerHTML = tempContainer.innerHTML;
                }

                // enable containers
                containersToDisable.forEach(container => container.classList.remove('js-disabled-search'));

                // focus
                temporaryFocus(this.resultsContainer);

                // scroll to top of results
                const rect = this.resultsContainer.getBoundingClientRect();
                window.scrollTo(window.scrollX, document.documentElement.scrollTop + rect.top);

                // update "selected" count
                this.updateSelectedFilterCounts();

                // add DS tracking
                window.DS.tracking.init();
            })
            .catch((error) => {
                // Load full search page endpoint instead
                window.location.href = pageUrl;
            });
    }

    gatherParams(url, newSearch = false) {
        const searchParams = this.searchParams || {};

        // dates
        searchParams.date = {};

        const beginParam = document.querySelector('input[name="begin"]').value;
        if(beginParam){
            searchParams.date.begin = encodeURIComponent(beginParam);
        }

        const endParam = document.querySelector('input[name="end"]').value;
        if(endParam){
            searchParams.date.end = encodeURIComponent(endParam);
        }

        // page
        if(newSearch){
            searchParams.page = 1;
        } else {
            const pageParam = searchUtils.getParameterByName('page', url) || 1;
            if(pageParam){
                searchParams.page = encodeURIComponent(pageParam);
            }
        }
        
        // size
        const sizeParam = searchUtils.getParameterByName('size') || 10;
        if(sizeParam){
            searchParams.size = encodeURIComponent(sizeParam);
        }
         

        // content types
        searchParams.type = [];
        const checkedPublicationTypeCheckboxes = [].slice.call(document.querySelectorAll('input[name="type"]:checked'));
        checkedPublicationTypeCheckboxes.forEach(checkbox => {
            searchParams.type.push(encodeURIComponent(checkbox.value));
        });
        if (searchParams.type.length === 0) {
            delete searchParams.type;
        }

        // sort
        const sortField = document.querySelector('.js-sort-by');
        if (sortField) {
            searchParams.sort = encodeURIComponent(sortField.value);
        }

        // term
        if(newSearch) {
            // If a new search then use the search term
            const searchField = document.getElementById('filters-search-term');
            if (searchField) {
                searchParams.q = encodeURIComponent(searchField.value);
            }
        } else {
            // Use the existing parameter if available - pagination action shouldn't submit new search input
            const qParam = searchUtils.getParameterByName('q');
            if(qParam){
                searchParams.q = encodeURIComponent(qParam);
            }
        }

        // cat 
        const catParam = searchUtils.getParameterByName('cat');
        if(catParam){
            searchParams.cat = encodeURIComponent(catParam);
        }

        // topics
        searchParams.topic = [];
        const checkedTopicCheckboxes = [].slice.call(document.querySelectorAll('input[name="topic"]:checked'));
        checkedTopicCheckboxes.forEach(checkbox => {
            searchParams.topic.push(encodeURIComponent(checkbox.value));
        });
        if (searchParams.topic.length === 0) {
            delete searchParams.topic;
        }

        return searchParams;
    }

    loadResults(url) {
        return PromiseRequest(url);
    }

    removeFacet(buttonElement) {
        // clear related input
        const input = document.querySelector('#' + buttonElement.dataset.slug);

        if (input.type === 'checkbox' || input.type === 'radio') {
            input.checked = false;
        } else {
            input.value = '';
        }

        // if we're clearing a date input
        // clear any errors from the date input
        // reset the other date input to the current searhparam value (and remove any errors from that too)
        const toElement = document.getElementById('date-to');
        const fromElement = document.getElementById('date-from');
        toElement.value = searchUtils.getParameterByName('end', buttonElement.href);
        fromElement.value = searchUtils.getParameterByName('begin', buttonElement.href);

        searchUtils.removeError(toElement.closest('.ds_question'));
        searchUtils.removeError(fromElement.closest('.ds_question'));
        this.doSearch(buttonElement.href, true);
    }

    syncFiltersToUrl() {
        const urlSearchParams = new URLSearchParams(window.location.href);

        const types = urlSearchParams.getAll('type');
        const topics = urlSearchParams.getAll('topic');
        const begin = urlSearchParams.get('begin');
        const end = urlSearchParams.get('end');
        const sort = urlSearchParams.get('sort');

        document.getElementById('date-from').value = begin;
        document.getElementById('date-to').value = end;

        [].slice.call(document.querySelectorAll('.ds_checkbox__input[name="topic"]')).forEach(checkbox => {
            checkbox.checked = topics.includes(checkbox.id);
        });

        [].slice.call(document.querySelectorAll('.ds_checkbox__input[name="type"]')).forEach(checkbox => {
            checkbox.checked = types.includes(checkbox.id);
        });

        const sortField = document.querySelector('.js-sort-by');
        if (sortField) {
            sortField.value = sort;
        }
    }

    updateSelectedFilterCounts() {
        [].slice.call(this.filtersContainer.querySelectorAll('.ds_accordion-item__body')).forEach(filterGroup => {
            const countElement = filterGroup.closest('.ds_accordion-item').querySelector('.ds_search-filters__filter-count');
            let count = 0;

            [].slice.call(filterGroup.querySelectorAll('.ds_checkbox__input, .ds_radio__input')).forEach(input => {
                if (input.checked) {
                    count++;
                }
            });

            [].slice.call(filterGroup.querySelectorAll('.ds_input')).forEach(input => {
                if (input.value.trim().length > 0) {
                    count++;
                }
            });

            if (count > 0) {
                countElement.innerText = `(${count} selected)`;
            } else {
                countElement.innerText = '';
            }
        });
    }

    validateDateInput(element) {
        return searchUtils.validateInput(element, [searchUtils.dateRegex]);
    }
};

export default LandingFilters;

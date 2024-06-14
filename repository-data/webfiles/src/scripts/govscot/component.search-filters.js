import breakpointCheck from '../../../node_modules/@scottish-government/design-system/src/base/utilities/breakpoint-check/breakpoint-check';
import PromiseRequest from '../../../node_modules/@scottish-government/design-system/src/base/tools/promise-request/promise-request';
import temporaryFocus from '../../../node_modules/@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';
import searchUtils from './search-utils';

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

class SearchFilters {
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
                            this.doSearch();
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
                        this.doSearch();
                    }
                }
            });
        }

        if (toDatePickerInputElement) {
            toDatePickerInputElement.addEventListener('change', () => {
                if (this.validateDateInput(toDatePickerInputElement)) {
                    fromDatePickerInputElement.dataset.maxdate = toDatePickerInputElement.value;
                    if (breakpointCheck('medium')) {
                        this.doSearch();
                    }
                }
            });
        }

        // sort dropdown change submits search on medium+ viewports
        this.resultsContainer.addEventListener('change', event => {
            if (event.target.classList.contains('js-sort-by')) {
                this.doSearch();
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
            this.doSearch();
        });

        window.addEventListener('popstate', event => {
            const resultsUrl = window.location.href;
            this.isPopstate = true;
            this.doSearch(resultsUrl);
            this.syncFiltersToUrl();
        });
    }

    clearFilters() {
        const filterInputs = [].slice.call(this.filtersContainer.querySelectorAll('.ds_input, .ds_checkbox__input, .ds_radio__input'));
        filterInputs.forEach(input => {
            if (input.type === 'checkbox' || input.type === 'radio') {
                // clear filter checkboxes/radios
                input.checked = false;
            } else {
                // clear filter textboxes
                input.value = '';
                searchUtils.removeError(input.closest('.ds_question'));
            }
        });

        this.doSearch();
    }

    doSearch(url) {
        if (!url) {
            url = window.location.pathname + searchUtils.getNewQueryString(this.gatherParams());

            // do not proceed if there are errors
            if (document.querySelectorAll('.ds_search-filters [aria-invalid="true"]').length) {
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
                        window.history.pushState('', '', url);
                    } catch (error) {
                        // history API not supported
                    }
                }

                // temporary container for the search results so we can query a DOM tree
                const tempContainer = document.createElement('div');
                tempContainer.innerHTML = data.response;

                this.resultsContainer.innerHTML = tempContainer.querySelector('.ds_search-results').innerHTML;

                // enable containers
                containersToDisable.forEach(container => container.classList.remove('js-disabled-search'));

                // focus
                temporaryFocus(this.resultsContainer);

                // update "selected" count
                this.updateSelectedFilterCounts();

                // add DS tracking
                window.DS.tracking.init();
            })
            .catch((error) => {
                window.location.href = url;
            });
    }

    gatherParams(initial) {
        const searchParams = this.searchParams || {};

        // dates
        searchParams.date = {};
        searchParams.date.begin = document.querySelector('input[name="begin"]').value;
        searchParams.date.end = document.querySelector('input[name="end"]').value;

        // page
        if (initial) {
            searchParams.page = getParameterByName('page') || 1;
            searchParams.size = getParameterByName('size') || 10;
        }

        // content types
        searchParams.type = [];
        const checkedPublicationTypeCheckboxes = [].slice.call(document.querySelectorAll('input[name="type"]:checked'));
        checkedPublicationTypeCheckboxes.forEach(checkbox => {
            searchParams.type.push(checkbox.value);
        });
        if (searchParams.type.length === 0) {
            delete searchParams.type;
        }

        // sort
        const sortField = document.querySelector('.js-sort-by');
        if (sortField) {
            searchParams.sort = sortField.value;
        }

        // term
        searchParams.q = getParameterByName('q');

        // topics
        searchParams.topic = [];
        const checkedTopicCheckboxes = [].slice.call(document.querySelectorAll('input[name="topic"]:checked'));
        checkedTopicCheckboxes.forEach(checkbox => {
            searchParams.topic.push(checkbox.value);
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
        toElement.value = getParameterByName('end', buttonElement.href);
        fromElement.value = getParameterByName('begin', buttonElement.href);

        searchUtils.removeError(toElement.closest('.ds_question'));
        searchUtils.removeError(fromElement.closest('.ds_question'));

        this.doSearch(buttonElement.href);
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
        sortField.value = sort;
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

export default SearchFilters;

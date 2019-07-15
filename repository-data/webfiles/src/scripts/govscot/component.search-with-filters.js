// SEARCH FILTERS COMPONENT
// Contains functionality for the filterable search pages

/* global window, document */

'use strict';

import searchUtils from './search-utils';
import expandable from './component.expandable';
import Pikaday from '../vendor/pikaday';
import dates from '../utils/dates';
import $ from 'jquery';

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

function PickerOptions(field, container, settings, theme) {
    this.field = field;
    this.container = container;
    this.format = 'DD/MM/YYYY';
    this.maxDate = settings.maxDate;
    this.minDate = settings.minDate;
    this.bound = false;
    this.yearRange = [1980, settings.maxDate.getFullYear()];
    this.theme = theme;
    this.onDraw = function() {
        $(this.el).find('select > option:disabled').prop('disabled', false);
        $(this.el).find('button, a, select').attr('tabindex', '-1');
    };

    this.onSelect = function () {

        dataLayer.push({
            'field': this.name,
            'interaction': 'select',
            'event': 'date-picker'
        });

        $(this._o.container).removeClass('date-entry__calendar--open');
        $(this._o.field).trigger('blur');
        // If on mobile don't do the search automatically.
        if ($(window).innerWidth() > 767) {
            $('#filters').submit();
        }
    };
}

const SearchWithFilters = function (settings) {

    this.settings = {
        responsiveWidthThreshold: 767,
        autoscaleThreshold: 786,
        autoscalePollingFrequency: 50,
        maxDate: new Date(),
        minDate: new Date(1999, 5, 1)
    };

    this.settings = $.extend(this.settings, settings);

    this.attachEventHandlers = attachEventHandlers;
    this.enableJSFilters = enableJSFilters;
    this.gatherParams = gatherParams;
    this.hasActiveSearch = hasActiveSearch;
    this.initDateFilters = initDateFilters;
    this.initStickyFilterButtons = initStickyFilterButtons;
    this.init = init;
    this.validateDateInput = validateDateInput;


};

function init() {
    let that = this;
    this.searchParams = this.gatherParams(true);

    this.attachEventHandlers();
    this.enableJSFilters();
    this.hideFilters = hideFilters;
    this.initDateFilters();
    this.initStickyFilterButtons();
    this.showFilters = showFilters;
    this.searchUtils = searchUtils;
    this.submitSearch = function (options) {
        options = options || {};
        if (options.changingPage) {that.isChangingPage = true;}
        if (options.popstate) {that.isPopstate = true;}
        $('#filters').submit();
    };
}

function attachEventHandlers () {
    let that = this;

    $('#filters').on('submit', function (event) {
console.log('submit search!')

        event.preventDefault();

        // do not proceed if there are errors
        if (document.querySelectorAll('#filters [aria-invalid]').length) {
            return false;
        }

        if (!that.isChangingPage) {that.searchParams.page = 1;}

        let currentParams = that.gatherParams();
        let newQueryString = searchUtils.getNewQueryString(currentParams);

        // insert "loading" message
        let searchResults = $('#search-results');
        searchResults.css({position: 'relative'});
        let overlay = $('<div class="search-results-overlay"></div>');
        overlay.appendTo(searchResults);

        // disable form
        $('#filters-fields').prop('disabled',true);

        $.ajax({
            url: window.location.pathname + newQueryString
        }).done(function (response) {

            // skip this if we're on popstate
            if (that.isPopstate){
                delete that.isPopstate;
            } else {
                // update querystring
                try {
                    window.history.pushState('', '', newQueryString);
                } catch(error) {
                    // history API not supported
                }
            }

            // update results (incl pagination and status readout)
            $('#search-results').html($(response).find('#search-results').html());

            // update display status of "clear" buttons
            if (that.hasActiveSearch(currentParams)) {
                $('.js-clear-filters').removeClass('hidden');
            } else {
                $('.js-clear-filters').addClass('hidden');
            }

            // remove "loading" message
            $('.search-results-overlay').remove();

            // enable form
            $('#filters-fields').prop('disabled',false);

            // update count for mobile
            $('.js-search-results-count').html($('#search-results .search-results__count').html());

            that.hideFilters();

            // scroll to the top of the page if we are changing page
            if (that.isChangingPage) {
                let pageContent = document.getElementById('page-content');
                window.scrollTo(window.scrollX, pageContent.offsetTop + pageContent.offsetParent.offsetTop);
            }
            that.isChangingPage = false;
        }).fail(function () {
            window.location.search = newQueryString;
        });
    });

    $('#search-results').on('click', '.js-load-more-results', function (event) {
        event.preventDefault();

        let startPage = +event.target.getAttribute('data-page-start');
        let qsArray = window.location.search.substring(1).replace(/page=\d+/, '').split('&');
        if (qsArray[0] === '') {
            qsArray = [];
        }

        qsArray = qsArray.filter(function (item) {return item !== ''});

        qsArray.push('page=' + startPage);
        let newQueryString = '?' + qsArray.join('&');

        $.ajax({
            url: window.location.pathname + newQueryString
        }).done(function (response) {
            // update querystring
            if (window.history.pushState) {
                window.history.pushState('', '', newQueryString);
            }

            // update results (incl pagination)
            $('#search-results-list').append($(response).find('#search-results-list').html());
            $('#pagination').html($(response).find('#pagination').html());
            $('#load-more').attr('data-page-start', startPage + 1);
        }).fail(function () {
            window.location.search = newQueryString;
        });
    });

    // show/hide filters
    $('.js-show-filters').on('click', function (event) {
        event.preventDefault();
        that.showFilters();
        expandable.showAllExpandableItems();
    });

    $('.js-cancel-filters').on('click', function (event) {
        event.preventDefault();
        that.hideFilters();
    });

    let t;

    $('.checkbox-group').on('change', 'input[type=checkbox]', function () {

        let containerType = $(this)
            .closest('.expandable-item')
            .find('.expandable-item__title')
            .text()
            .toLowerCase();

        dataLayer.push({
            'filter': containerType,
            'interaction': this.checked ? 'check': 'uncheck',
            'value': this.value,
            'event': 'filters'
        });

        // If on mobile don't do the search automatically.
        if ($(window).innerWidth() > that.settings.responsiveWidthThreshold) {
            clearTimeout(t);

            // do search on a small timeout to allow user to select multiple items without making multiple requests
            t = setTimeout(function() {
                delete that.searchParams.page;
                that.submitSearch();
            }, 300);
        }
    });

    // clear filters
    $('body').on('click', '.js-clear-filters', function (e) {
        e.preventDefault();

        dataLayer.push({
            'event': 'filters-clear'
        });

        // clear all filters
        let filtersForm = $('#filters');
        filtersForm.find('input[type="checkbox"]').prop('checked', false);
        filtersForm.find('input[type="text"]').val('');
        delete that.searchParams.page;

        that.submitSearch();
    });

    $('#search-results').on('click', '.pagination__page', function (event) {
        event.preventDefault();

        that.searchParams.page = getParameterByName('page', event.target.href);
        that.submitSearch({changingPage: true});
    });

    window.onpopstate = function () {
        that.searchParams.page = getParameterByName('page');
        that.submitSearch({
            changingPage: true,
            popstate: true
        });
    }
}

function enableJSFilters () {
    $('.checkbox-group input[type="radio"]')
        .attr('type', 'checkbox')
        .next('label')
        .removeClass('fancy-radio fancy-radio--min')
        .addClass('fancy-checkbox');

    // populate checkboxes from searchParams
    $('.checkbox-group input[data-checkedonload]').prop('checked', true);

    // date pickers display
    $('.js-show-calendar').removeClass('hidden  hidden--hard');

    // filter button display
    $('#filter-actions').addClass('visible-xsmall  filter-actions');
}

function gatherParams (initial) {
    let searchParams = this.searchParams || {};

    // KEYWORD / TERM
    if (window.location.href.indexOf('/search/') !== -1) {
        searchParams.q = encodeURI($('#filters-search-term').val());
    } else {
        searchParams.term = encodeURI($('#filters-search-term').val());
    }

    // PAGINATION
    if (initial) {
        searchParams.page = getParameterByName('page') || 1;
        searchParams.size = getParameterByName('size') || 10;
    }

    // TOPICS
    searchParams.topics = [];
    $.each( $('input[name="topics"]:checked') , function (index, checkbox) {
        searchParams.topics.push(checkbox.value);
    });
    if (searchParams.topics.length === 0) {
        delete searchParams.topics;
    }

    // PUBLICATION TYPES
    searchParams.publicationTypes = [];
    $.each( $('input[name="publicationTypes"]:checked') , function (index, checkbox) {
        searchParams.publicationTypes.push(checkbox.value);
    });
    if (searchParams.publicationTypes.length === 0) {
        delete searchParams.publicationTypes;
    }

    // DATE RANGES
    if ($('#filter-date-range').length) {
        searchParams.date = searchParams.date || {};

        searchParams.date.begin = encodeURI($('#date-from').val());
        searchParams.date.end = encodeURI($('#date-to').val());
    }

    return searchParams;
}

/**
    * Determines whether or not there is an active search
    * @param params
    * @returns {boolean}
    */
function hasActiveSearch(params) {
    let hasActiveSearch = false;

    for (let key in params) {
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

function hideFilters() {
    $('.filters-container').removeClass('filters-container--open').closest('.mobile-layer').removeClass('mobile-layer--open');
    if ($(window).innerWidth() <= this.settings.responsiveWidthThreshold) {
        window.scrollTo(0, $('.filter-buttons').offset().top - 64);
    }
}

function initDateFilters () {
    let that = this;

    function dateToDependsOnDateFrom(fromPickerOptions) {
        const originalMethod = fromPickerOptions.onSelect;
        fromPickerOptions.onSelect = function () {
            originalMethod.apply(this);
            that.dateToPicker.setMinDate(that.dateFromPicker.getDate());

            if (that.dateToPicker.getDate() &&
                that.dateToPicker.getDate() < that.dateFromPicker.getDate()) {
                that.dateToPicker.setDate(that.dateFromPicker.getDate());
            }
        };
    }

    const fromPickerOptions = new PickerOptions(document.getElementById('date-from'), $('#date-from').closest('.date-entry').find('.date-entry__calendar')[0], that.settings, 'tst-date-from'); //NOSONAR
    const toPickerOptions = new PickerOptions(document.getElementById('date-to'), $('#date-to').closest('.date-entry').find('.date-entry__calendar')[0], that.settings, 'tst-date-to'); //NOSONAR

    dateToDependsOnDateFrom(fromPickerOptions);

    if (this.searchParams.date) {
        $.extend(fromPickerOptions, {
            defaultDate: new Date(this.searchParams.date.begin),
            setDefaultDate: true
        });
        $.extend(toPickerOptions, {
            defaultDate: new Date(this.searchParams.date.end),
            setDefaultDate: true
        });
    }

    this.dateFromPicker = new Pikaday(fromPickerOptions);
    this.dateFromPicker.name = 'date-from';

    this.dateToPicker = new Pikaday(toPickerOptions);
    this.dateToPicker.name = 'date-to';

    $('.js-show-calendar').on('click', function () {
        let calendar = $($(this).closest('.date-entry').find('.date-entry__calendar').get(0));

        window.dataLayer.push({
            'field': calendar.parent().find('label').text().toLowerCase().replace(' ', '-'),
            'interaction': calendar.hasClass('date-entry__calendar--open') ? 'close': 'open',
            'event': 'date-picker'
        });

        // close any other open calendars
        $(this).closest('.date-entry').siblings().find('.date-entry__calendar').removeClass('date-entry__calendar--open');
        calendar.toggleClass('date-entry__calendar--open');
    });

    $('.js-close-calendar').on('click', function () {
        let calendar = $($(this).closest('.date-entry').find('.date-entry__calendar').get(0));
        calendar.removeClass('date-entry__calendar--open');
    });

    $('.date-entry__input').on('keypress', function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            if (that.validateDateInput($(this))) {
                delete that.searchParams.page;
                that.submitSearch();
            }
        }
    }).on('change', function () {
        let isValidDates = that.validateDateInput($(this));
        // If on mobile don't do the search automatically.
        if ($(window).innerWidth() > that.settings.responsiveWidthThreshold) {
            delete that.searchParams.page;
            that.submitSearch();
        }
    });

    // set calendar min dates
    this.dateFromPicker.setMinDate(this.settings.minDate);
    this.dateToPicker.setMinDate(this.settings.minDate);
}

function initStickyFilterButtons() {
    let interval,
        that = this;

    let init = function() {
        let windowWidth = $(window).width();

        // Reset autoscaling
        clearInterval(interval);
        interval = null;

        // Only run the autoscaling on small screens.
        if (windowWidth < that.settings.autoscaleThreshold) {
            // Polling often is the best practice for catching
            // scrolling events due to the inconsistencies in how
            // browsers handle the scrolling event.
            interval = setInterval(function() {
                let position = $(window).scrollTop(),
                    targetTopOffset;

                if ($('.search-results-header__right').offset()) {
                    targetTopOffset = $('.search-results-header__right').offset().top - $('.site-header').height();
                } else {
                    targetTopOffset = 0;
                }

                // Perform scaling
                if (position > targetTopOffset) {
                    $('.filter-buttons--sticky').addClass('filter-buttons--sticky--show');
                }
                else {
                    $('.filter-buttons--sticky').removeClass('filter-buttons--sticky--show');
                }
            }, that.settings.autoscalePollingFrequency);
        } else {
            // Ensure that header is scaled up if window is wide.
            $('.filter-buttons--sticky').removeClass('filter-buttons--sticky--show');
        }
    };
    $(window).resize(function() {
        init();
    });
    init();
}

function showFilters() {
    $('.filters-container')
        .addClass('filters-container--open')
        .closest('.mobile-layer')
        .addClass('mobile-layer--open');

    window.scrollTo(0, $('.filter-buttons').offset().top - 64);
}

function validateDateInput (element) {
    let isValid = true;

    // 1) is the date in an allowed format?
    let dateRegex = /^(0[1-9]|[1-2][0-9]|3[0-1])\/(0[1-9]|1[0-2])\/[0-9]{4}$/;
    let inputGroup = element.closest('.date-entry__input-group');
    searchUtils.removeError(inputGroup);

    if (!element.val().match(dateRegex) && element.val() !== '') {
        searchUtils.addError('Please enter date in dd/mm/yyyy format', inputGroup);
        isValid = false;
        element.attr('aria-invalid', true);
        return isValid;
    }

    // 2) does the date conflict?
    // expect date TO to be later than date FROM
    let dateFrom = dates.translateDate($('#date-from').val());
    let dateTo = dates.translateDate($('#date-to').val());

    dateFrom = dateFrom ? new Date(dateFrom) : this.settings.minDate;
    dateTo = dateTo ? new Date(dateTo) : this.settings.maxDate;

    if (dateTo.getTime() < dateFrom.getTime()) {
        searchUtils.addError('\'Date from\' must be earlier than \'Date to\'', element.closest('.date-entry__input-group'));
        isValid = false;
        element.attr('aria-invalid', true);
    } else {
        searchUtils.removeError($('#date-from').closest('.date-entry__input-group'));
        searchUtils.removeError($('#date-to').closest('.date-entry__input-group'));
        element.attr('aria-invalid', false);
    }

    return isValid;
}

export default SearchWithFilters;

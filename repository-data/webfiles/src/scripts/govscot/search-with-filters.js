'use strict';
define([
    './search-utils',
    '../utils/gup',
    '../utils/dates',
    './paginator',
    '../vendor/pikaday',
    './component.expandable',
    './search-with-filters-helper'
], function (searchUtils, gup, dates, Paginator, Pikaday, expandable, searchWithFiltersHelper) {

    /**
     * All of the logic for search and filtering is contained in this for use by any page that needs it
     * @param {object} settings
     * @returns {object} search with filters
     * @constructor
     */
    var Search = function (settings) {
        this.settings = {
            name: 'search',
            pageSize: 10,
            pagePadding: 3,
            searchUrl: '/service/search/site?q=',
            resultsContainer: '#search-results',
            resultsList: '#search-results-list',
            paginationContainer: '#pagination',
            responsiveWidthThreshold: 767,
            resultTemplate: false,
            didYouMeanTemplate: false,
            sortField: 'title',
            sortOrder: 'asc',
            displayText: {
                singular: 'result',
                plural: 'results'
            },
            autoscaleThreshold: 786,
            autoscalePollingFrequency: 50,
            maxDate: new Date(),
            minDate: new Date(1999, 5, 1),
            filters: {
                date: false,
                topics: false,
                publicationTypes: false,
                modified: false
            }
        };
        this.settings = $.extend(this.settings, settings);

        this.init = init;
        this.initPaginator = initPaginator;
        this.initStickyFilterButtons = initStickyFilterButtons;
        this.initDateFilters = initDateFilters;
        this.validateDateInput = validateDateInput;
        this.clearSearch = clearSearch;
        this.clearFiltersData = clearFiltersData;
        this.doInitialSearch = doInitialSearch;
        this.attachEventHandlers = attachEventHandlers;
        this.gatherParams = gatherParams;
        this.showFilters = showFilters;
        this.hideFilters = hideFilters;
        this.updatePage = updatePage;
        this.gatherInitialParams = gatherInitialParams;
        this.arrayIntersection = arrayIntersection;


        this.setDateRange = searchWithFiltersHelper.setDateRange;
        this.setFiltersFormControls = searchWithFiltersHelper.setFiltersFormControls;
        this.setCheckboxParamInGui = searchWithFiltersHelper.setCheckboxParamInGui;
        this.updateResultCount = searchWithFiltersHelper.updateResultCount;
        this.updateSearchResultStatusText = searchWithFiltersHelper.updateSearchResultStatusText;
        this.buildStatusText = searchWithFiltersHelper.buildStatusText;
        this.doSearch = searchWithFiltersHelper.doSearch;
        this.setSearchFieldDisplay = searchWithFiltersHelper.setSearchFieldDisplay;
        this.createQuery = searchWithFiltersHelper.createQuery;
        this.setHasActiveSearch = searchWithFiltersHelper.setHasActiveSearch;
        this.fetchResults = searchWithFiltersHelper.fetchResults;
        this.fetchAllResults = searchWithFiltersHelper.fetchAllResults;
        this.numberHits = searchWithFiltersHelper.numberHits;
        this.populateResults = searchWithFiltersHelper.populateResults;
    };

    function PickerOptions(field, container, settings, theme) {
        this.field = field;
        this.container = container;
        this.format = 'DD/MM/YY';
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
            if ($(window).innerWidth() >= settings.responsiveWidthThreshold) {
                $('#filters').submit();
            }
        };
    }

    function init() {
        this.searchParams = this.gatherInitialParams();

        this.doInitialSearch();
        this.attachEventHandlers();
        this.initDateFilters();
        this.setFiltersFormControls();
        this.initPaginator();
        this.initStickyFilterButtons();
        this.searchUtils = searchUtils;
    }

    function initPaginator() {
        this.paginator = new Paginator($(this.settings.paginationContainer), this.settings.pagePadding, this);
    }

    function initStickyFilterButtons() {
        var interval,
            that = this;

        var init = function() {
            var windowWidth = $(window).width();

            // Reset autoscaling
            clearInterval(interval);
            interval = null;

            // Only run the autoscaling on small screens.
            if (windowWidth < that.settings.autoscaleThreshold) {
                // Polling often is the best practice for catching
                // scrolling events due to the inconsistencies in how
                // browsers handle the scrolling event.
                interval = setInterval(function() {
                    var position = $(window).scrollTop(),
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

    function initDateFilters() {
        var that = this;

        function dateToDependsOnDateFrom(fromPickerOptions) {
            var originalMethod = fromPickerOptions.onSelect;
            fromPickerOptions.onSelect = function () {
                originalMethod.apply(this);
                that.dateToPicker.setMinDate(that.dateFromPicker.getDate());

                if (that.dateToPicker.getDate() &&
                    that.dateToPicker.getDate() < that.dateFromPicker.getDate()) {
                    that.dateToPicker.setDate(that.dateFromPicker.getDate());
                }
            };
        }

        var fromPickerOptions = new PickerOptions(document.getElementById('date-from'), $('#date-from').closest('.date-entry').find('.date-entry__calendar')[0], that.settings, 'tst-date-from'); //NOSONAR
        var toPickerOptions = new PickerOptions(document.getElementById('date-to'), $('#date-to').closest('.date-entry').find('.date-entry__calendar')[0], that.settings, 'tst-date-to'); //NOSONAR

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

            console.log(1);

            var calendar = $($(this).closest('.date-entry').find('.date-entry__calendar').get(0));

            dataLayer.push({
                'field': calendar.parent().find('label').text().toLowerCase().replace(' ', '-'),
                'interaction': calendar.hasClass('date-entry__calendar--open') ? 'close': 'open',
                'event': 'date-picker'
            });

            // close any other open calendars
            $(this).closest('.date-entry').siblings().find('.date-entry__calendar').removeClass('date-entry__calendar--open');
            calendar.toggleClass('date-entry__calendar--open');
        });

        $('.js-close-calendar').on('click', function () {
            var calendar = $($(this).closest('.date-entry').find('.date-entry__calendar').get(0));
            calendar.removeClass('date-entry__calendar--open');
        });

        $('.date-entry__input').on('keypress', function (event) {
            if (event.keyCode === 13) {
                event.preventDefault();
                if (that.validateDateInput($(this))) {
                    $('#filters').submit();
                }
            }
        }).on('change', function () {
            var isValidDates = that.validateDateInput($(this));
            // If on mobile don't do the search automatically.
            if ($(window).innerWidth() >= that.settings.responsiveWidthThreshold && isValidDates) {
                $('#filters').submit();
            }
        });

        // set calendar min dates
        this.dateFromPicker.setMinDate(this.settings.minDate);
        this.dateToPicker.setMinDate(this.settings.minDate);
    }

    function validateDateInput(element) {
        var isValid = true;

        // 1) is the date in an allowed format?
        var dateRegex = /^(0[1-9]|[1-2][0-9]|3[0-1])\/(0[1-9]|1[0-2])\/[0-9]{2}$/;
        var inputGroup = element.closest('.date-entry__input-group');
        searchUtils.removeError(inputGroup);

        if (!element.val().match(dateRegex) && element.val() !== '') {
            searchUtils.addError('Please enter date in dd/mm/yy format', inputGroup);
            isValid = false;
            return isValid;
        }

        // 2) does the date conflict?
        // expect date TO to be later than date FROM
        var dateFrom = dates.translateDate($('#date-from').val());
        var dateTo = dates.translateDate($('#date-to').val());

        dateFrom = dateFrom ? new Date(dateFrom) : this.settings.minDate;
        dateTo = dateTo ? new Date(dateTo) : this.settings.maxDate;

        if (dateTo.getTime() < dateFrom.getTime()) {
            searchUtils.addError('\'Date from\' must be earlier than \'Date to\'', element.closest('.date-entry__input-group'));
            isValid = false;
        } else {
            searchUtils.removeError($('#date-from').closest('.date-entry__input-group'));
            searchUtils.removeError($('#date-to').closest('.date-entry__input-group'));
        }

        return isValid;
    }

    function clearSearch() {
        this.clearFiltersData();
        delete this.searchParams.term;

        searchUtils.updateQueryString(this.settings, this.searchParams);

        this.setFiltersFormControls();
        this.setSearchFieldDisplay('');

        // remove any stored page choices
        var urlWithoutPage = window.location.href
            .replace(/\&page=\d{1,3}/g, '')
            .replace(/\?page=\d{1,3}/g, '');

        if (window.history.pushState) {
            window.history.pushState('', '', urlWithoutPage);
        }

        $('.input-group--has-error')
            .removeClass('input-group--has-error')
            .find('.message')
            .remove();

        $('.scrollable').find('.scrollable__content')[0].scrollTop = 0;

        this.doInitialSearch();
    }

    /**
     * Deletes filter information from searchParams and resets start point to zero
     * Leaves 'term' alone
     */
    function clearFiltersData() {
        delete this.searchParams.date;
        delete this.searchParams.topics;
        delete this.searchParams.publicationTypes;
        this.searchParams.from = 0;
    }

    function doInitialSearch() {
        //Get initial search term
        this.searchParams.from = searchUtils.getQueryString('from') || 0;
        this.searchParams.size = searchUtils.getQueryString('size') || this.settings.pageSize;

        // parse a page querystring param
        if (searchUtils.getQueryString('page') !== '') {
            this.searchParams.from = parseInt(searchUtils.getQueryString('page') - 1, 10) * this.searchParams.size;
        }

        searchUtils.updateQueryString(this.settings, this.searchParams);

        this.doSearch(this.searchParams, false);
    }

    function attachEventHandlers() {
        var that = this;

        // filter results
        $('#filters').on('submit', function (e) {
            var hasErrors = $('.input-group--has-error').length !== 0;

            if (hasErrors) {
                return false;
            }

            e.preventDefault();

            that.searchParams = that.gatherParams();

            searchUtils.updateQueryString(that.settings, that.searchParams);
            that.doSearch(that.searchParams, false);

            that.hideFilters();
        });

        // show/hide filters
        $('.js-show-filters').on('click', function () {
            that.showFilters();

            expandable.showAllExpandableItems();
        });

        $('.js-cancel-filters').on('click', function () {
            that.hideFilters();
        });

        var t;

        $('.checkbox-group').on('change', 'input[type=checkbox]', function () {

            var containerType = $(this)
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
            if ($(window).innerWidth() >= that.settings.responsiveWidthThreshold) {
                clearTimeout(t);

                // do search on a small timeout to allow user to select multiple items without making multiple requests
                t = setTimeout(function() {
                    $('#filters').submit();
                }, 300);
            }
        });

        // checkbox group changes on mobile
        // search form events
        $('.js-filter-search-submit').on('click', function (e) {
            e.preventDefault();

            that.searchParams = that.gatherParams();

            dataLayer.push({
                'event': 'filters-search',
                'value': 'q={' + JSON.stringify(that.searchParams.term) + '}'
            });

            searchUtils.updateQueryString(that.settings, that.searchParams);

            that.doSearch(that.searchParams, false);

            // update button display
            that.setSearchFieldDisplay(
                $('#filters-search-term').val()
            );



            that.hideFilters();
        });

        // clear filters
        $('.js-clear-filters').on('click', function (e) {
            e.preventDefault();

            dataLayer.push({
                'event': 'filters-clear'
            });

            that.clearSearch();
        });

        // reset & hide filters
        $('.js-cancel-filters').on('click', function (e) {
            e.preventDefault();

            // reset filters
            that.setFiltersFormControls();

            // publish an analytics event - cancel filters button
            $.publish('analyticsEvent', [that.settings.name + '-filters', 'click', 'cancel filters']);

            that.hideFilters();
        });
    }

    function gatherParams(resetStart) {
        var params = {};

        // KEYWORD / TERM
        params.term = $('#filters-search-term').val();

        // PAGINATION
        if (resetStart) {
            params.from = 0;
        }
        else {
            params.from = searchUtils.getQueryString('from') || 0;
        }
        params.size = searchUtils.getQueryString('size') || this.settings.pageSize;

        // TOPICS
        params.topics = [];
        $.each( $('input[name="topics[]"]:checked') , function (index, checkbox) {
            params.topics.push(checkbox.value);
        });
        if (params.topics.length === 0) {
            delete params.topics;
        }

        // PUBLICATION TYPES
        params.publicationTypes = [];
        $.each( $('input[name="publicationTypes[]"]:checked') , function (index, checkbox) {
            params.publicationTypes.push(checkbox.value);
        });
        if (params.publicationTypes.length === 0) {
            delete params.publicationTypes;
        }

        // DATE RANGES
        if (this.settings.filters.date) {
            params.date = params.date || {};

            var dateFrom = dates.translateDate($('#date-from').val());
            var dateTo = dates.translateDate($('#date-to').val());

            if (dateFrom) {
                params.date.begin = dates.dateFormatMachine(new Date(dateFrom));
                if (!params.date.begin) {
                    document.getElementById('date-from').value = '';
                }
            }

            if (dateTo) {
                params.date.end = dates.dateFormatMachine(new Date(dateTo));
                if (!params.date.end) {
                    document.getElementById('date-to').value = '';
                }
            }
        }

        return params;
    }

    function showFilters() {
        $('.filters-container')
            .addClass('filters-container--open')
            .closest('.mobile-layer')
            .addClass('mobile-layer--open');

        window.scrollTo(0, $('.filter-buttons').offset().top - 64);
    }

    function hideFilters() {
        $('.filters-container').removeClass('filters-container--open').closest('.mobile-layer').removeClass('mobile-layer--open');
        if ($(window).innerWidth() <= this.settings.responsiveWidthThreshold) {
            window.scrollTo(0, $('.filter-buttons').offset().top - 64);
        }
    }

    function updatePage() {
        var pageNumber = gup('page') || 1;
        this.searchParams.from = (pageNumber - 1) * this.searchParams.size;
        this.doSearch(this.searchParams, false);
    }

    function gatherInitialParams() {
        var params = {};

        var term = gup('term'),
            topics = gup('topics'),
            page = gup('page'),
            publicationTypes = gup('publicationTypes'),
            begin = gup('begin'),
            end = gup('end');

        if (term) {
            params.term = decodeURIComponent(term.replace(/\+/g, '%20'));
        }

        var allowedTopics = [];
        $('input[name="topics[]"]').each(function (k, v) {
            allowedTopics.push(v.value);
        });
        if (topics) {
            topics = topics.replace(/%20/g, ' ').split('|');

            topics = this.arrayIntersection(topics, allowedTopics);
            params.topics = topics;
        }

        if (publicationTypes) {
            publicationTypes = publicationTypes.replace(/%20/g, ' ').split('|');
        }

        var allowedPublicationTypes = [];
        $('input[name="publicationTypes[]"]').each(function (k, v) {
            allowedPublicationTypes.push(v.id);
        });

        if (publicationTypes) {
            publicationTypes = this.arrayIntersection(publicationTypes, allowedPublicationTypes);

            params.publicationTypes = publicationTypes;
        }

        params.date = {};
        if (begin && begin.match(/\d{2}-\d{2}-\d{2}/)) {
            params.date.begin = begin;
        }
        if (end && end.match(/\d{2}-\d{2}-\d{2}/)) {
            params.date.end = end;
        }
        if (page && page.match(/\d+/)) {
            params.from = page;
        }

        if (this.settings.filters.modified) {
            params.modified = { before: $('#build-time').val()};
        }

        return params;
    }

    function arrayIntersection(array1, array2) {
        return $.map(array1, function (el) {
            return $.inArray(el, array2) < 0 ? null : el;
        });
    }

    return Search;
});

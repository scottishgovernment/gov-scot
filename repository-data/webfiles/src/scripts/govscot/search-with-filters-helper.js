'use strict';
define('search-with-filters-helper', [
    '../utils/gup',
    '../utils/dates',
    './tool.text-truncate',
    'jquery.dotdotdot'
], function (gup, dates, TextTruncate) {

    /**
     * If there are form selections stored in the querystring, present them in the GUI
     */
    function setFiltersFormControls() {
        var filtersForm = $('#filters');
        filtersForm.find('input[type="checkbox"]').prop('checked', false);
        filtersForm.find('.date-entry__input').val('');
        $('.input-group--has-error')
            .removeClass('input-group--has-error')
            .find('.message')
            .remove();

        for (var key in this.searchParams) {
            if (!this.searchParams.hasOwnProperty(key)) {
                continue;
            }

            var value = this.searchParams[key];

            switch (key) {
                case 'topics':
                case 'publicationTypes':
                    for (var i = 0, il = value.length; i < il; i++) {
                        this.setCheckboxParamInGui(value[i]);
                    }
                    break;
                case 'term':
                    this.setSearchFieldDisplay(value);
                    break;
                case 'date':
                    this.setDateRange(value);
                    break;
                default:
                    break;
            }
        }
    }

    function setCheckboxParamInGui(value) {
        $('input[value="' + value + '"]').prop('checked', true);
    }

    function setDateRange(range) {
        if (dates.dateFormatHuman(range.begin) !==
            dates.dateFormatHuman(this.settings.minDate)) {
            $('#date-from').val(dates.dateFormatHuman(range.begin));
        } else {
            $('#date-from').val('');
        }
        if (dates.dateFormatHuman(range.end) !==
            dates.dateFormatHuman(this.settings.maxDate)) {
            $('#date-to').val(dates.dateFormatHuman(range.end));
        } else {
            $('#date-to').val('');
        }
    }

    function setSearchFieldDisplay(value) {
        $('#filters-search-term').val(value);
    }

    /**
     * Determines whether or not there is an active search
     * @param params
     * @returns {boolean}
     */
    function setHasActiveSearch(params) {
        var hasActiveSearch = false;

        for (var key in params) {
            if (!params.hasOwnProperty(key)) {
                continue;
            }

            var value = params[key];
            if (key === 'topics' || key === 'term' || key === 'publicationTypes') {
                if (value !== '') {
                    hasActiveSearch = true;
                }
            } else if (key === 'date' && (value.begin || value.end)) {
                hasActiveSearch = true;
            }
        }

        this.hasActiveSearch = hasActiveSearch;
    }

    /**
     * Updates the "showing n results" text
     * @param {number} count
     *
     * @returns {boolean} success
     */
    function updateResultCount(count) {
        var that = this;

        var countElement = $('.js-search-results-count');

        if (countElement.length > 0 && typeof count === 'number') {

            // build the string
            var countText = 'Showing ';
            if (!that.hasActiveSearch) {
                countText += 'all ';
            }

            countText += '<b>' + count + '</b> ';

            if (count === 1) {
                countText += this.settings.displayText.singular;
            } else {
                countText += this.settings.displayText.plural;
            }

            countElement.html(countText);
            return true;
        } else {
            return false;
        }
    }

    function updateSearchResultStatusText(count, params) {
        var statusElement = $('.js-search-results-status');

        if (statusElement.length > 0 && typeof count === 'number') {
            // build the string
            var statusHtml = 'Showing ';
            if (!this.hasActiveSearch) {
                statusHtml += 'all <b>' + count + '</b> ' + this.settings.displayText.plural;
            } else {
                statusHtml += this.buildStatusText(count, params);
            }

            statusElement.html(statusHtml);

            return true;
        } else {
            return false;
        }
    }

    function buildStatusText(count, params) {
        var statusHtml = '',
            i, il;

        // go through params

        // 1. count
        statusHtml += '<b>' + count + '</b> ';

        if (count === 1) {
            statusHtml += this.settings.displayText.singular + ' ';
        } else {
            statusHtml += this.settings.displayText.plural + ' ';
        }
        // 2. term
        if (params.term) {
            statusHtml += 'containing <b>' + params.term + '</b> ';
        }
        // 3. dates
        if (params.date) {
            if (params.date.begin) {
                statusHtml += 'from <b>' + dates.dateFormatHuman(params.date.begin) + '</b> ';
            }
            if (params.date.end) {
                statusHtml += 'to <b>' + dates.dateFormatHuman(params.date.end) + '</b> ';
            }
        }

        // 4. topics
        if (params.topics) {
            statusHtml += 'about ';

            for (i = 0, il = params.topics.length; i < il; i++) {
                if (i > 0) {
                    statusHtml += 'or ';
                }
                statusHtml += '<b>' + params.topics[i] + '</b> ';
            }
        }

        // 5. types
        if (params.publicationTypes) {
            statusHtml += 'of type ';

            for (i = 0, il = params.publicationTypes.length; i < il; i++) {
                if (i > 0) {
                    statusHtml += 'or ';
                }

                var rawType = params.publicationTypes[i];

                statusHtml += '<b>' + $('label[for=' + rawType + ']').text() + '</b> ';
            }
        }

        return statusHtml;
    }

    /**
     * @param {object} params - search parameters
     * @param {boolean} append
     */
    function doSearch(params, append) {
        var that = this;
        var queryParams = this.createQuery(params);

        var elementsToHideWhileLoading = $('.js-search-results-count, #pagination,' +
            ' #search-results-list');
        var ajaxLoader = $('<div id="ajax-loader">Loading results</div>');

        if (!append) {
            elementsToHideWhileLoading.css({opacity: 0.2});
        }

        ajaxLoader.insertAfter('#search-results-list');

        var sort = params.term ? 'rank' : this.settings.sortField + ':' + this.settings.sortOrder;

        that.fetchResults(queryParams, params.from, params.size, sort)
            .done(function (result) {
                that.paginator.setParams(params.from, params.size, result.hits.total);

                that.paginator.renderPages();

                // display any "did you mean" suggestions
                if (result.hasOwnProperty('suggest') &&
                    typeof that.settings.didYouMeanTemplate === 'function') {
                    var didYouMeanItems = that.getDidYouMean(result.suggest);

                    if (didYouMeanItems.length > 0) {
                        var html = that.settings.didYouMeanTemplate({items: didYouMeanItems});
                        $('.search-box__form').parent().after(html);
                    }
                }

                that.numberHits(result, params);

                //Put the results on screen
                that.populateResults(result, append);

                // update the list's start point
                $(that.settings.resultsContainer)
                    .find('ol.content-items')
                    .attr('start', params.from + 1);

                that.updateSearchResultStatusText(result.hits.total, params);
                that.updateResultCount(result.hits.total);

                if (that.hasActiveSearch) {
                    $('.js-clear-filters').removeClass('hidden');
                } else {
                    $('.js-clear-filters').addClass('hidden');
                }
            })
            .fail(function () {

            })
            .always(function () {
                // remove "loading" spinner
                ajaxLoader.remove();
                elementsToHideWhileLoading.css({opacity: 1});
            });

    }

    /**
     * Enchance the params before sending the search.

     * @param params
     * @returns {*} JSON query object
     */
    function createQuery(params) {

        // set that we have (or don't have) an active search
        this.setHasActiveSearch(params);

        // clone the params
        var queryParams = JSON.parse(JSON.stringify(params));

        if (typeof this.queryModifierFunction === 'function') {
           queryParams = this.queryModifierFunction(queryParams);
        }

        queryParams.sortOrder = this.settings.sortOrder;

        queryParams.sortField = queryParams.term ?
        this.settings.sortWithTermField :
        this.settings.defaultSortField;

        return queryParams;
    }

    function fetchResults(query, from, size) {
        var queryStringItems = [
            'from=' + from,
            'size=' + size
        ];

        if (gup('q')) {
            queryStringItems.push('q=' + gup('q'));
        }

        var url = this.settings.searchUrl +
            (this.settings.searchUrl.indexOf('?') === -1 ? '?' : '&') +
            queryStringItems.join('&');

        return $.ajax({
            type: 'POST',
            url: url,
            dataType: 'json',
            data: JSON.stringify(query),
            contentType: 'application/json; charset=UTF-8'
        });
    }

    function numberHits(data, params) {
        var hits = data.hits.hits;

        for (var i = 0, il = hits.length; i < il; i++) {
            hits[i].globalIndex = params.from + i;
        }
    }

    /**
     *
     * @param {object} data
     * @param {boolean} append
     */
    function populateResults(data, append) {

        for (var i = 0, il = data.hits.hits.length; i < il; i++) {
            var thisItem = data.hits.hits[i];

            if (this.settings.dateTimeProperty) {
                thisItem._source.filterDate = dates.formatDateTime(thisItem._source[this.settings.dateTimeProperty], true);
            } else {
                thisItem._source.filterDate = dates.dateFormatPretty(thisItem._source.filterDate);
            }
        }

        var html = this.settings.resultTemplate(data);
        if (append) {
            $(this.settings.resultsList).append(html);
        } else {
            $(this.settings.resultsList).html(html);
        }

        TextTruncate();
    }

    return {
        setFiltersFormControls: setFiltersFormControls,
        setCheckboxParamInGui: setCheckboxParamInGui,
        setDateRange: setDateRange,
        setSearchFieldDisplay: setSearchFieldDisplay,
        updateResultCount: updateResultCount,
        updateSearchResultStatusText: updateSearchResultStatusText,
        buildStatusText: buildStatusText,
        doSearch: doSearch,
        createQuery: createQuery,
        setHasActiveSearch: setHasActiveSearch,
        fetchResults: fetchResults,
        numberHits: numberHits,
        populateResults: populateResults
    };
});

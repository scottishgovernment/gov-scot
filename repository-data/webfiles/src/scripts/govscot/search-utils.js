'use strict';
define('search-utils', [
    '../utils/gup',
    '../utils/dates'
], function (gup, dates) {
    var searchUtils = {
        /**
         * Helper function to determine whether session/local storage can be written to
         * @returns {boolean}
         */
        browserHasWriteableStorage: function() {
            var mod, result;

            mod = new Date();

            try {
                sessionStorage.setItem(mod, mod.toString());
                result = sessionStorage.getItem(mod) === mod.toString();
                sessionStorage.removeItem(mod);
            } catch(err) {
                result = false;
            }

            return result;
        },

        addError: function (message, inputGroup) {
            var errorContainer = inputGroup.find('.message');

            if (errorContainer.length === 0) {
                errorContainer = $('<div class="message"></div>');
                errorContainer.prependTo(inputGroup);
            }

            errorContainer.text(message);
            inputGroup.addClass('input-group--has-error');
        },

        removeError: function (inputGroup) {
            inputGroup.find('.message').remove();
            inputGroup.removeClass('input-group--has-error');
        },

        /**
         * Updates the search parameters stored the query string
         *
         * @param params
         */
        updateQueryString: function (searchSettings, params) {
            var newQueryStringParams = [],
                newQueryString;

            if (params.term) {
                newQueryStringParams.push('term=' + params.term);
            }
            if (params.topics) {
                newQueryStringParams.push('topics=' + params.topics.join('|'));
            }
            if (params.publicationTypes) {
                newQueryStringParams.push('publicationTypes=' + params.publicationTypes.join('|'));
            }

            if (params.date) {
                if (params.date.begin) {
                    newQueryStringParams.push('begin=' + params.date.begin);
                }

                if (params.date.end) {
                    newQueryStringParams.push('end=' + params.date.end);
                }
            }

            if (params.from) {
                newQueryStringParams.push('page=' + ((params.from / searchSettings.pageSize) + 1) );
            }

            if (newQueryStringParams.length > 0) {
                newQueryString = '?' + newQueryStringParams.join('&');
            } else {
                newQueryString = window.location.pathname;
            }

            if (window.history.replaceState) {
                window.history.replaceState('', '', newQueryString);
            }
        },

        /**
         * @param param
         * @param $window
         * @param $gup
         * @returns {*|string}
         */
        getQueryString: function (param, $window, $gup) {
            gup = $gup || gup;
            return gup(param, $window) || '';
        }
    };

    return searchUtils;
});

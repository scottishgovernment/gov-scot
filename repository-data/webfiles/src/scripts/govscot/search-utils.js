// SEARCH UTILITIES

'use strict';

import $ from 'jquery';

const searchUtils = {
    addError: function (message, inputGroup) {
        let errorContainer = inputGroup.find('.message');

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
     * Updates the search parameters stored on the query string
     *
     * @param params
     */
    getNewQueryString: function (params) {
        let newQueryStringParams = [],
            newQueryString;
        
        if (params.cat) {
            newQueryStringParams.push('cat=' + params.cat);
        }

        if (params.term) {
            newQueryStringParams.push('term=' + params.term);
        }
        if (params.q) {
            newQueryStringParams.push('q=' + params.q);
        }
        if (params.topics) {
            newQueryStringParams.push('topics=' + params.topics.join(';'));
        }
        if (params.publicationTypes) {
            newQueryStringParams.push('publicationTypes=' + params.publicationTypes.join(';'));
        }

        if (params.date) {
            if (params.date.begin) {
                newQueryStringParams.push('begin=' + params.date.begin);
            }

            if (params.date.end) {
                newQueryStringParams.push('end=' + params.date.end);
            }
        }

        if (params.page) {
            newQueryStringParams.push('page=' + params.page);
        }

        if (newQueryStringParams.length > 0) {
            newQueryString = '?' + newQueryStringParams.join('&');
        } else {
            newQueryString = '?';
        }

        return newQueryString;
    }
};

export default searchUtils;

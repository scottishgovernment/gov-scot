// ABOUT STATS FORMAT

/* global window */

'use strict';

import $ from 'jquery';

window.dataLayer = window.dataLayer || [];

const homePage = {

    init: function () {
        this.attachEventHandlers();
    },

    attachEventHandlers: function () {
        const that = this;

        const statsSubmitLink = $('.js-stats-form-submit');

        // submit stats form on press of enter on keyword input
        statsSubmitLink.on('click', function (event) {
            event.preventDefault();

            that.submitstatsForm($(this).attr('href'));

        });
    },

        submitstatsForm: function (destinationUrl) {
            const queryStringParams = [],
            pubtype = [];
        let queryString;

        $.each($('input[name="pubtype[]"]:checked'), function (index, checkbox) {
            pubtype.push(checkbox.value);

        });

          // build querystring

        queryStringParams.push('cat=filter');

        if (pubtype.length > 0) {
            queryStringParams.push('publicationTypes=' + pubtype.join(';'));
        }

        if (queryStringParams.length > 0) {
            queryString = '?' + queryStringParams.join('&');
        } else {
            queryString = window.location.pathname.substring(1);
        }

        // navigate to stats page
        this.navigateToUrl(destinationUrl + queryString);
    },

    navigateToUrl: function (url) {
        window.location.href = url;
    }
};

window.format = homePage;
window.format.init();

export default homePage;

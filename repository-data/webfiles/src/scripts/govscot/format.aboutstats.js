// ABOUT STATS FORMAT

/* global window */

'use strict';

import TextTruncate from './tool.text-truncate';
import 'jquery.dotdotdot';
import $ from 'jquery';

window.dataLayer = window.dataLayer || [];

const homePage = {

    init: function () {
        this.attachEventHandlers();

        TextTruncate();
    },

    attachEventHandlers: function () {
        const that = this;

        const policySubmitLink = $('.js-stats-form-submit');

        // submit policy form on press of enter on keyword input
        policySubmitLink.on('click', function (event) {
            event.preventDefault();

            that.submitPolicyForm($(this).attr('href'));

        });   
    },

        submitPolicyForm: function (destinationUrl) {
            const queryStringParams = [],
            term = $('#filters-search-term').val(),
            topics = [];
        let queryString;

        $.each($('input[name="topics[]"]:checked'), function (index, checkbox) {
            push(checkbox.value);

        });

          // build querystring
      
        queryStringParams.push('cat=filter');

        if (length > 0) {
            queryStringParams.push('publicationTypes=' + join(';'));
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
    },

    navigateToUrl: function (url) {
        window.location.href = url;
    },

};

window.format = homePage;
window.format.init();

export default homePage;

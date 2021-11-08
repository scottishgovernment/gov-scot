// HOME FORMAT

/* global window */

'use strict';

import $ from 'jquery';

window.dataLayer = window.dataLayer || [];

const homePage = {

    settings: {
    },

    init: function () {
        this.attachEventHandlers();
        this.initHeroItemAnalytics();
    },

    initHeroItemAnalytics: function () {
        // add data-gtm attribute to links in carousel content
        const heroItems = document.querySelectorAll('.hero-item');

        for (let i = 0, il = heroItems.length; i < il; i++) {
            let heroItem = heroItems[i];

            let links = heroItem.querySelectorAll('.hero-item__content a');

            for (let j = 0, jl = links.length; j < jl; j++) {
                let link = links[j];

                if (!link.getAttribute('data-gtm')) {
                    link.setAttribute('data-gtm', `hero-item-link-${i + 1}-${j}`);
                }
            }
        }
    },

    attachAnalyticsEvents: function () {
        $('.js-topics').on('change', 'input[type=checkbox]', function () {
            window.dataLayer.push({
                'filter': 'topics',
                'interaction': this.checked ? 'check': 'uncheck',
                'value': this.value,
                'event': 'filters'
            });
        });

        $('.js-policy-form-submit').on('click', function () {
            window.dataLayer.push({
                'event': 'policies-go'
            });
        });
    },

    attachEventHandlers: function () {
        const that = this;

        const policySubmitLink = $('.js-policy-form-submit');

        // submit policy form on press of enter on keyword input
        $('#filters-search-term').on('keypress', function (event) {
            if (event.keyCode === 13) {
                event.preventDefault();

                that.submitPolicyForm(policySubmitLink.attr('href'));
            }
        });

        policySubmitLink.on('click', function (event) {
            event.preventDefault();

            that.submitPolicyForm($(this).attr('href'));
        });

        this.attachAnalyticsEvents();
    },

    submitPolicyForm: function (destinationUrl) {
        const queryStringParams = [],
            term = $('#filters-search-term').val(),
            topics = [];
        let queryString;

        $.each($('input[name="topics[]"]:checked'), function (index, checkbox) {
            topics.push(checkbox.value);
        });

        // build querystring
        if (term.length > 0) {
            queryStringParams.push('term=' + term);
        }
        queryStringParams.push('cat=filter');

        if (topics.length > 0) {
            queryStringParams.push('topics=' + topics.join(';'));
        }

        if (queryStringParams.length > 0) {
            queryString = '?' + queryStringParams.join('&');
        } else {
            queryString = window.location.pathname.substring(1);
        }

        // navigate to policy page
        this.navigateToUrl(destinationUrl + queryString);
    },

    navigateToUrl: function (url) {
        window.location.href = url;
    }
};

window.format = homePage;
window.format.init();

export default homePage;

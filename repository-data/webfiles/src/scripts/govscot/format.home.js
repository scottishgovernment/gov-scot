// HOME FORMAT

/* global window */

'use strict';

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
        const topicCheckboxes = [].slice.call(document.querySelectorAll('.js-topics input[type=checkbox]'));

        topicCheckboxes.forEach(element => {
            element.addEventListener('change', function () {
                window.dataLayer.push({
                    'filter': 'topics',
                    'interaction': this.checked ? 'check' : 'uncheck',
                    'value': this.value,
                    'event': 'filters'
                });
            });
        });

        document.querySelector('.js-policy-form-submit').addEventListener('click', function () {
            window.dataLayer.push({
                'event': 'policies-go'
            });
        });
    },

    attachEventHandlers: function () {
        const policySubmitLinkElement = document.querySelector('.js-policy-form-submit');
        const policySubmitLinkHref = policySubmitLinkElement.getAttribute('href');

        // submit policy form on press of enter on keyword input
        document.querySelector('#filters-search-term').addEventListener('keypress', event => {
            if (event.keyCode === 13) {
                event.preventDefault();

                this.submitPolicyForm(policySubmitLinkHref);
            }
        });

        policySubmitLinkElement.addEventListener('click', event => {
            event.preventDefault();

            this.submitPolicyForm(policySubmitLinkHref);
        });

        this.attachAnalyticsEvents();
    },

    submitPolicyForm: function (destinationUrl) {
        const queryStringParams = [],
            term = document.querySelector('#filters-search-term').value,
            topics = [];
        let queryString;

        const checkboxes = [].slice.call(document.querySelectorAll('input[name="topics[]"]')).filter(item => item.checked);

        checkboxes.forEach(checkbox => {
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

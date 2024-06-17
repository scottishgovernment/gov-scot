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
        const heroItems = document.querySelectorAll('.gov_hero-item');

        for (let i = 0, il = heroItems.length; i < il; i++) {
            let heroItem = heroItems[i];

            let links = heroItem.querySelectorAll('.gov_hero-item__content a');

            for (let j = 0, jl = links.length; j < jl; j++) {
                let link = links[j];

                if (!link.getAttribute('data-navigation')) {
                    link.setAttribute('data-navigation', `hero-${i + 1}-link-${j}`);
                }
            }
        }
    },

    attachEventHandlers: function () {
        const policySubmitLinkElements = [].slice.call(document.querySelectorAll('.js-policy-form-submit'));
        const keywordSearchInputElement = document.querySelector('#filters-search-term');

        // submit policy form on press of enter on keyword input
        keywordSearchInputElement.addEventListener('keypress', event => {
            if (event.keyCode === 13) {
                event.preventDefault();

                this.submitPolicyForm(keywordSearchInputElement.parentNode.querySelector('.js-policy-form-submit').dataset.href);
            }
        });

        policySubmitLinkElements.forEach(element => {
            element.addEventListener('click', event => {
                event.preventDefault();
                event.stopPropagation();

                this.submitPolicyForm(element.dataset.href);
            });
        });
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
            queryStringParams.push('q=' + term);
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

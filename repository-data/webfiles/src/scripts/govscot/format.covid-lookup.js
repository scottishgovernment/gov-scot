'use strict';

import 'promise-polyfill/src/polyfill';

// polyfill for element.closest
if (!Element.prototype.matches) {
    Element.prototype.matches =
        Element.prototype.msMatchesSelector ||
        Element.prototype.webkitMatchesSelector;
}

if (!Element.prototype.closest) {
    Element.prototype.closest = function (s) {
        var el = this;

        do {
            if (Element.prototype.matches.call(el, s)) {
                return el;
            }
            el = el.parentElement || el.parentNode;
        } while (el !== null && el.nodeType === 1);
        return null;
    };
}

const locationTitleTemplate = function (restriction) {
    let titleParts = [];
    titleParts.push(`<span data-${restriction.type}="${restriction.title}">${restriction.title}</span>`);
    if (restriction.parent) {
        titleParts.push(`<span data-${restriction.parent.type}="${restriction.parent.title}">${restriction.parent.title}</span>`);
    }
    return titleParts.join(', ');
};

const resultTemplate = function (templateData) {
    return `<h1 class="overflow--medium--three-twelfths  overflow--large--two-twelfths  overflow--xlarge--two-twelfths">COVID protection level: ${templateData.restriction.level.title}</h1>

    <p>We've matched the postcode <strong>${templateData.postcode}</strong> to <strong>${locationTitleTemplate(templateData.restriction)}</strong>.

    ${templateData.restriction.description}

    <p>Check what you can and cannot do in this area at protection level ${templateData.restriction.level.title}.</p>

    ${templateData.resultsPageContent ? templateData.resultsPageContent : ''}

    <div id="restrictions-detail">
    </div>

    <h2>Find information about somewhere else</h2>
    <p><a href="#" class="js-enter-another">Check another postcode protection level</a></p>`;
};

const restrictionsDetailTemplate = function (templateData) {
    return `<div>${templateData.body}</div>`;
};

const covidLookup = {
    init: function () {
        this.requestCurrentRestrictions()
            .then((data) => {
                this.currentRestrictions = JSON.parse(data.response);
                this.enableSearchForm();
                this.checkCurrentView();
            });

        this.landingSection = document.querySelector('#covid-restrictions-lookup-landing');
        this.resultsSection = document.querySelector('#covid-restrictions-lookup-results');
        this.searchForm = document.querySelector('#covid-restrictions-lookup-form');
        this.postcodeField = this.searchForm.querySelector('#postcode');

        // we'll want to insert this into the results page later
        const resultsPageContentContainer = document.querySelector('#covid-restrictions-lookup-results-content');
        this.resultsPageContent = resultsPageContentContainer.innerHTML;
        // resultsPageContentContainer.parentNode.removeChild(resultsPageContentContainer);

        this.resultsSection.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-enter-another')) {
                event.preventDefault();
                this.landingSection.classList.remove('hidden');
                this.resultsSection.classList.add('hidden');
                window.history.pushState({}, '', '#!/');
                window.setTimeout(() => {
                    if (!this.isInViewport(this.landingSection)) {
                        this.landingSection.scrollIntoView();
                    }
                }, 0);
            }
        });

        window.onpopstate = () => {
            this.checkCurrentView();
        };
    },

    checkCurrentView: function () {
        // do we have a particular view?
        if (window.location.hash.match(/^#!\//)) {
            const postcode = window.location.hash.substring(3);
            this.postcodeField.value = this.formatPostcode(postcode);

            if (this.validatePostcode(postcode)) {
                this.getLoctionForPostcode(postcode)
                    .then((data) => {
                        const response = JSON.parse(data.response);
                        const wardId = response.ward;
                        const districtId = response.district;
                        this.showResult(wardId, districtId, this.formatPostcode(postcode));
                    });
            } else {
                // display default view
                this.landingSection.classList.remove('hidden');
                this.resultsSection.classList.add('hidden');
            }
        } else {
            // display default view
            this.landingSection.classList.remove('hidden');
            this.resultsSection.classList.add('hidden');
        }
    },

    enableSearchForm: function () {
        this.searchForm.classList.remove('hidden');

        this.searchForm.addEventListener('submit', (event) => {
            event.preventDefault();

            this.resultsSection.innerHTML = '';

            const postcode = this.postcodeField.value.trim();
            const isValid = this.validatePostcode(postcode);
            const findButton = this.searchForm.querySelector('[type="submit"]');
            const fieldset = this.searchForm.querySelector('fieldset');

            if (postcode === '') {
                this.setErrorMessage(isValid, window.errorMessages.badPostcode, 'required-postcode', this.postcodeField);
            } else {
                this.setErrorMessage(isValid, window.errorMessages.badPostcode, 'invalid-postcode', this.postcodeField);
            }

            if (isValid) {
                fieldset.disabled = true;

                this.getLoctionForPostcode(postcode)
                    .then(data => {
                        const response = JSON.parse(data.response);
                        const wardId = response.ward;
                        const districtId = response.district;
                        this.showResult(wardId, districtId, this.formatPostcode(postcode));
                        window.history.pushState({}, '', `#!/${postcode.toUpperCase().replace(/\s+/g, '')}`);
                        this.postcodeField.value = this.formatPostcode(postcode);

                        fieldset.disabled = false;
                    }, error => {
                        if (error.status === 404) {
                            let errorMessage = this.getPostcodeErrorMessage(postcode);
                            this.setErrorMessage(false, errorMessage, 'invalid-postcode', this.postcodeField);
                        } else {
                            this.setErrorMessage(false, window.errorMessages.serviceUnavailable, 'service-unavailable', findButton);
                        }

                        fieldset.disabled = false;
                    }).catch(() => {
                        fieldset.disabled = false;
                    });
            }
        });
    },

    showResult: function (wardId, districtId, postcode) {
        let localRestrictions = [];

        const wardRestrictions = this.currentRestrictions.filter((restriction) => restriction.type === 'electoral-ward');
        const authorityRestrictions = this.currentRestrictions.filter((restriction) => restriction.type === 'local-authority');

        for (let i = 0, il = wardRestrictions.length; i < il; i++) {
            if (wardRestrictions[i].id === wardId) {
                localRestrictions.push(wardRestrictions[i]);
                break;
            }
        }

        for (let i = 0, il = authorityRestrictions.length; i < il; i++) {
            if (authorityRestrictions[i].id === districtId) {
                localRestrictions.push(authorityRestrictions[i]);
                break;
            }
        }

        const templateData = {
            restriction: localRestrictions[0],
            postcode: postcode,
            resultsPageContent: this.resultsPageContent || false
        };

        if (localRestrictions[1]) {
            templateData.restriction.parent = localRestrictions[1];
        }

        if (!localRestrictions.length) {
            this.setErrorMessage(false, window.errorMessages.restrictionMessage, 'no-restrictions', this.postcodeField);
            return false;
        }

        this.landingSection.classList.add('hidden');
        this.resultsSection.classList.remove('hidden');

        this.postcodeField.value = this.formatPostcode(postcode);
        this.resultsSection.innerHTML = resultTemplate(templateData);
        window.setTimeout(() => {
            if (!this.isInViewport(this.resultsSection)) {
                this.resultsSection.scrollIntoView({
                    behaviour: 'smooth',
                    block: 'center',
                    inline: 'nearest'
                });
            }
        }, 0);

        this.requestLocalRestrictionDetails('/' + localRestrictions[0].level.link)
            .then((data) => {
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = data.responseText;
                this.resultsSection.querySelector('#restrictions-detail').innerHTML = restrictionsDetailTemplate({ body: tempDiv.querySelector('.body-content .ds_accordion').outerHTML });
                const detailsAccordion = new window.DS.components.GovAccordion(this.resultsSection.querySelector('#restrictions-detail .ds_accordion'));
                detailsAccordion.init();
            });
    },

    setErrorMessage: function (valid, message, errortype, field) {
        const question = field.closest('.ds_question');
        const errorMessageElement = field.parentNode.querySelector('.ds_question__error-message');

        if (valid) {
            question.classList.remove('ds_question--error');
            field.classList.remove('ds_input--error');
            field.removeAttribute('aria-invalid', 'true');
            errorMessageElement.classList.add('hidden');
            errorMessageElement.dataset.form = '';
        } else {
            question.classList.add('ds_question--error');
            field.classList.add('ds_input--error');
            field.setAttribute('aria-invalid', 'true');
            errorMessageElement.dataset.form = `error-${errortype}`;
            errorMessageElement.classList.remove('hidden');
            errorMessageElement.innerHTML = message;
        }
    },

    getLoctionForPostcode: function (postcode) {
        return this.promiseRequest(`/service/geosearch/postcodes/${postcode.toUpperCase().replace(/\s+/g, '')}`);
    },

    requestCurrentRestrictions: function () {
        return this.promiseRequest('/rest/covid/restrictions');
    },

    requestLocalRestrictionDetails: function (url) {
        return this.promiseRequest(url);
    },

    promiseRequest: (url, method = 'GET') => {
        const request = new XMLHttpRequest();

        return new Promise((resolve, reject) => {
            request.onreadystatechange = () => {
                if (request.readyState !== 4) {
                    return;
                }

                if (request.status >= 200 && request.status < 300) {
                    resolve(request);
                } else {
                    reject({
                        status: request.status,
                        statusText: request.statusText
                    });
                }
            };

            request.open(method, url, true);
            request.send();
        });
    },

    formatPostcode: function (postcode) {
        postcode = postcode.trim().toUpperCase().replace(/\s+/g, '');
        return postcode.substring(0, postcode.length - 3) + ' ' + postcode.slice(-3);
    },

    validatePostcode: function (postcode) {
        postcode = postcode.trim().toUpperCase().replace(/\s+/g, '');

        let postcodeRegExp = new RegExp('^[A-Z]{1,2}[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$');
        let valid = postcode.match(postcodeRegExp) !== null;

        return valid;
    },

    getPostcodeErrorMessage : function (postcode) {
        postcode = postcode.trim().toUpperCase().replace(/\s+/g, '');
        if (this.isEnglishPostcode(postcode)) {
            return window.errorMessages.englishPostcode;
        }

        if (this.isWelshPostcode(postcode)) {
            return window.errorMessages.welshPostcode;
        }

        if (this.isNorthernIrishPostcode(postcode)) {
            return window.errorMessages.northernIrishPostcode;
        }

        return window.errorMessages.unrecognisedPostcode;
    },

    isEnglishPostcode : function (postcode) {
        let expression = /^(AL|B|B[ABDHLNRS]|C[ABHMORTVW]|D[AEHLNTY]|E|E[CNX]|FY|G[LUY]|H[ADGPUX]|I[GM‌​P]‌​|JE|KT|L|L[AENSU]|M|M[EK]|N|N[EGNRW]|O[LX]|P[ELOR]|R[GHM]|S|S[EGKLMNOPRSTW]|T[AFNQ‌​‌​RSW]|UB|W|W[ACDFNRSV]|YO)\d{1,2}\s?(\d[\w]{2})?/;
        return expression.test(postcode);
    },

    isWelshPostcode : function (postcode) {
        let expression = /^(LL|SY|LD|HR|NP|CF|SA)[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$/;
        return expression.test(postcode);
    },

    isNorthernIrishPostcode : function (postcode) {
        let expression = /^(BT){1,2}[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$/;
        return expression.test(postcode);
    },

    isInViewport: function (element) {
        const bounding = element.getBoundingClientRect();
        return (bounding.top >= 0 &&
            bounding.left >= 0 &&
            bounding.bottom <= (window.innerHeight) &&
            bounding.right <= (window.innerWidth));
    }
};

window.format = covidLookup;
window.format.init();

export default covidLookup;

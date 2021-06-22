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

const resultTemplate = function (templateData) {
    return `<div class="ds_layout__header">
        <div class="ds_error-summary  fully-hidden  flashable" id="covid-restrictions-error-summary" aria-labelledby="error-summary-title" role="alert"></div>
        <header class="ds_page-header">
            <h1 class="ds_page-header__title">${templateData.title}</h1>
        </header>
    </div>

    <div class="ds_layout__content">
        ${templateData.matchDescription}

        ${templateData.restrictions.map(restriction => `${restriction.description}\n`).join('')}

        ${templateData.restrictionsSummary}

        ${templateData.resultsPageContent ? templateData.resultsPageContent : ''}

        <div id="restrictions-detail">
        </div>

        <h2>Find information about somewhere else</h2>
        <p><a href="#" class="js-enter-another">Check another postcode protection level</a></p>
    </div>`;
};


const restrictionsDetailTemplate = function (templateData) {
    return `<div>${templateData.body}</div>`;
};

const errorSummaryTemplate = function (templateData) {
    return `<h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>

        <ul class="ds_error-summary__list">
            ${templateData.messages.map(message => `<li><a class="js-error-link" data-fieldid="${message.fieldId}" href="#${message.fieldId}">${message.content}</a></li>`).join('')}
        </ul>`;
};

const covidLookup = {
    init: function () {
        this.requestCurrentRestrictions()
            .then((data) => {
                let restrictions = JSON.parse(data.response);
                this.restrictionsById = {};
                restrictions.forEach(restriction => this.restrictionsById[restriction.id] = restriction);
                this.enableSearchForm();
                this.checkCurrentView();
            });

        this.landingSection = document.querySelector('#covid-restrictions-lookup-landing');
        this.resultsSection = document.querySelector('#covid-restrictions-lookup-results');
        this.searchForm = document.querySelector('#covid-restrictions-lookup-form');
        this.errorSummary = document.querySelector('#covid-restrictions-error-summary');
        this.postcodeField = this.searchForm.querySelector('#postcode');

        // we'll want to insert this into the results page later
        const resultsPageContentContainer = document.querySelector('#covid-restrictions-lookup-results-content');
        this.resultsPageContent = resultsPageContentContainer.innerHTML;
        // resultsPageContentContainer.parentNode.removeChild(resultsPageContentContainer);

        this.errorSummary.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-error-link')) {
                event.preventDefault();
                const targetField = document.getElementById(event.target.dataset.fieldid);
                targetField.focus();
                targetField.scrollIntoView({
                    behaviour: 'smooth',
                    block: 'center',
                    inline: 'nearest'
                });
            }
        });

        this.resultsSection.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-enter-another')) {
                event.preventDefault();
                this.landingSection.classList.remove('fully-hidden');
                this.resultsSection.classList.add('fully-hidden');
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
                this.getLocationForPostcode(postcode)
                    .then((data) => this.showResult(data));
            } else {
                // display default view
                this.landingSection.classList.remove('fully-hidden');
                this.resultsSection.classList.add('fully-hidden');
            }
        } else {
            // display default view
            this.landingSection.classList.remove('fully-hidden');
            this.resultsSection.classList.add('fully-hidden');
        }
    },

    enableSearchForm: function () {
        this.searchForm.classList.remove('fully-hidden');

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

                this.getLocationForPostcode(postcode)
                    .then(response => {
                        this.showResult(response);
                        window.history.pushState({}, '', `#!/${this.normalisePostcode(postcode)}`);
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

    showResult: function (response) {
        response.locations.forEach(restriction => this.addRestriction(restriction));
        response.distinctLevelCount = this.getLevelCount(response);
        let postcode = this.formatPostcode(response.postcode);
        let title = this.resultsTitle(response);
        let matchDescription = this.describeMatch(postcode, response);
        let restrictionsSummary = this.restrictionsSummary(response);
        const templateData = {
            title: title,
            matchDescription: matchDescription,
            restrictions: response.locations.map(location => location.restriction),
            postcode: postcode,
            restrictionsSummary: restrictionsSummary,
            resultsPageContent: this.resultsPageContent || false
        };

        this.landingSection.classList.add('fully-hidden');
        this.resultsSection.classList.remove('fully-hidden');

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

        // if there is only one level for the postcode and the postcode is not split on the scotlsand england border,
        // fetch the content and display it inline
        if (response.distinctLevelCount === 1 && response.splitWithEngland === false) {
            this.requestLocalRestrictionDetails('/' + response.locations[0].restriction.level.link)
                .then((data) => {
                    const tempDiv = document.createElement('div');
                    tempDiv.innerHTML = data.responseText;
                    this.resultsSection.querySelector('#restrictions-detail').innerHTML = restrictionsDetailTemplate({body: tempDiv.querySelector('.body-content').innerHTML});
                    const detailsAccordion = new window.DS.components.Accordion(this.resultsSection.querySelector('#restrictions-detail .ds_accordion'));
                    detailsAccordion.init();
                });
        }
    },

    resultsTitle : function (response) {
        // the title is simplified either if it is split in such a way that more than one level might apply
        if (response.splitWithEngland) {
            return 'There may be different protection levels in this area';
        }

        if (response.distinctLevelCount > 1) {
            return 'There are different protection levels in this area';
        }

        return `COVID protection level: ${response.locations[0].restriction.level.title}`;
    },

    describeMatch : function (postcode, response) {
        let locationsDescription = this.describeLocations(response);
        if (response.splitWithEngland) {
            return `<p>The postcode <strong>${postcode}</strong> is covered by ${locationsDescription}.</p>`;
        }

        if (response.locations.length === 1) {
            return `<p>We've matched the postcode <strong>${postcode}</strong> to ${locationsDescription}.</p>`;
        }

        let levelTitle = response.locations[0].restriction.level.title;
        let title = `<p>The postcode <strong>${postcode}</strong> is covered by 2 wards or councils: ${locationsDescription}.</p>`;
        if (response.distinctLevelCount === 1) {
            return title + `<p>Both these areas are in protection level ${levelTitle} right now.</p>`;
        } else {
            return title + '<p>There are different protection levels in each of these areas right now.</p>';
        }
    },

    restrictionsSummary : function (response) {
        if (response.splitWithEngland) {
            // Do we need to make this link editable in the cms since it might change?
            return `
                <p>The protection level you need to follow depends on which country the address is in.</p>
                <p>Check what you can and cannot do in these areas:</p>
                <ul>
                    ${this.listItemForLocation(response.locations[0], response.splitWithEngland)}
                    <li><a href="https://www.gov.uk/guidance/new-national-restrictions-from-5-november">COVID guidance for England</a></li>
                </ul>
                <p>If you’re unsure who covers the address, you can check with the <a href="https://www.mygov.scot/find-your-local-council/">local council in Scotland</a></p>
                `;
        }

        // only one level
        if (response.distinctLevelCount === 1) {
            let levelTitle = response.locations[0].restriction.level.title;
            return `<p>Check what you can and cannot do in this area at protection level ${levelTitle}.</p>`;
        }

        // more than one level ...
        let listItems = response.locations.map(
            location => this.listItemForLocation(location, response.splitWithEngland)).join('');
        return `<p>Check what you can and cannot do in these areas:</p>
                <ul>
                    ${listItems}
                </ul>
                <p>The protection level you need to follow depends on each address in that postcode. The address decides which local council covers it. </p>
                <p>If you’re unsure who covers the address, you should check <a href="https://www.mygov.scot/find-your-local-council/">with one of the councils.</a></p>
                `;
    },

    listItemForLocation : function(location, splitWithEngland) {
        let title = this.restrictionTitle(location.restriction, splitWithEngland);
        return `<li>
                    <a href="/${location.restriction.level.link}">${title}:  protection level ${location.restriction.level.title}</a>
                </li>`;
    },

    describeLocations : function (response) {
        let titles = response.locations.map(loc => this.restrictionTitle(loc.restriction, response.splitWithEngland));
        if (response.splitWithEngland === true) {
            titles.push('<span data-country="England">England</span>');
        }
        return titles.map(title => `<strong>${title}</strong>`).join(' and ');
    },

    restrictionTitle : function (restriction, splitWithEngland) {
        return splitWithEngland ? restriction.titleMarkup + ', Scotland' : restriction.titleMarkup;
    },

    addRestriction : function (location) {
        if (location.ward in this.restrictionsById) {
            let wardRestriction = this.restrictionsById[location.ward];
            let districtRestriction =  this.restrictionsById[location.district];
            location.restriction = wardRestriction;
            location.restriction.titleMarkup =
                `
                    <span data-ward="${wardRestriction.title}">${wardRestriction.title}</span>,
                    <span data-local-authority="${districtRestriction.title}">${districtRestriction.title}</span>
                `;
            return;
        }

        if (location.district in this.restrictionsById) {
            let districtRestriction =  this.restrictionsById[location.district];
            location.restriction = this.restrictionsById[location.district];
            location.restriction.titleMarkup = `<span data-local-authority="${districtRestriction.title}">${districtRestriction.title}</span>`;
            return;
        }
    },

    setErrorMessage: function (valid, message, errortype, field) {
        const question = field.closest('.ds_question');
        const errorMessageElement = field.parentNode.querySelector('.ds_question__error-message');

        if (valid) {
            question.classList.remove('ds_question--error');
            field.classList.remove('ds_input--error');
            field.removeAttribute('aria-invalid', 'true');
            errorMessageElement.classList.add('fully-hidden');
            errorMessageElement.dataset.form = '';
        } else {
            question.classList.add('ds_question--error');
            field.classList.add('ds_input--error');
            field.setAttribute('aria-invalid', 'true');
            errorMessageElement.dataset.form = `error-${errortype}`;
            errorMessageElement.classList.remove('fully-hidden');
            errorMessageElement.innerHTML = message;
        }

        const errorQuestions = [].slice.call(this.searchForm.querySelectorAll('.ds_question--error'));
        if (errorQuestions.length) {
            // display error summary
            const errorMessages = [];

            errorQuestions.forEach(question => {
                errorMessages.push({
                    fieldId: field.id,
                    content: question.querySelector('.ds_question__error-message > *:first-child').innerHTML
                });
            });

            this.errorSummary.innerHTML = errorSummaryTemplate({
                messages: errorMessages
            });
            this.errorSummary.classList.remove('fully-hidden');

            this.errorSummary.scrollIntoView();
            this.errorSummary.classList.add('flashable--flash');
            window.setTimeout(() => {
                this.errorSummary.classList.remove('flashable--flash');
            }, 200);
        } else {
            // remove error summary
            this.errorSummary.innerHTML = '';
            this.errorSummary.classList.add('fully-hidden');
        }

    },

    getLocationForPostcode: function (postcode) {

        postcode = this.normalisePostcode(postcode);

        // call both the geosearch and split postode endpoints and combine the result
        return new Promise((resolve, reject) =>
            Promise.all([
                this.promiseRequest(`/service/geosearch/postcodes/${postcode}`),
                this.promiseRequest(`/rest/split-postcodes/${postcode}`)
            ]).then(
                values => resolve(this.combineLocationResponses(values)),
                error => reject(error))
        );
    },

    combineLocationResponses : function (responses) {
        const geoResponse = JSON.parse(responses[0].response);
        const splitsResponse = JSON.parse(responses[1].response);

        if (splitsResponse.splits.length > 0) {
            geoResponse.locations = splitsResponse.splits;
        } else {
            geoResponse.locations = [{ ward : geoResponse.ward, district : geoResponse.district }];
        }
        // if the postcode is split on the England / Scotland border then there will be a single split element in the
        // response from the split postcodes endpoint
        geoResponse.splitWithEngland = splitsResponse.splits.length === 1;
        return geoResponse;
    },

    getLevelCount : function (response) {
        let levels = {};
        response.locations.forEach(loc => levels[loc.restriction.level.level] = true);
        return Object.keys(levels).length;
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
        postcode = this.normalisePostcode(postcode);
        return postcode.substring(0, postcode.length - 3) + ' ' + postcode.slice(-3);
    },

    validatePostcode: function (postcode) {
        postcode = this.normalisePostcode(postcode);

        let postcodeRegExp = new RegExp('^[A-Z]{1,2}[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$');
        let valid = postcode.match(postcodeRegExp) !== null;

        return valid;
    },

    getPostcodeErrorMessage : function (postcode) {
        postcode = this.normalisePostcode(postcode);

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
    },

    normalisePostcode : function (postcode) {
        return postcode.trim().toUpperCase().replace(/\s+/g, '');
    }
};

window.format = covidLookup;
window.format.init();

export default covidLookup;

'use strict';

const locationTitleTemplate = function (restriction) {
    let titleParts = [];
    titleParts.push(`<span data-${restriction.type}="${restriction.title}">${restriction.title}</span>`);
    if (restriction.parent) {
        titleParts.push(`<span data-${restriction.parent.type}="${restriction.parent.title}">${restriction.parent.title}</span>`);
    }
    return titleParts.join(', ');
};

const resultTemplate = function (templateData) {
    return `<h1>Local COVID Alert Level: ${templateData.restriction.level.title}</h1>
    <p>We've matched the postcode <strong>${templateData.searchTerm}</strong> to <strong>${locationTitleTemplate(templateData.restriction)}</strong>.

    <h2>Current Local COVID Alert Level</h2>
    <p>This area is in Local COVID Alert Level: ${templateData.restriction.level.title}.</p>
    <p><a href="/${templateData.restriction.level.link}">Read more about the protection level in your area.</a></p>

    <h2>Find information about somewhere else</h2>
    <p><a href="#" class="js-enter-another">Enter another postcode.</a></p>`;
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

            if (this.validatePostcode(postcode)) {
                this.getLoctionForPostcode(postcode)
                    .then((data) => {
                        const response = JSON.parse(data.response);
                        const wardId = response.ward;
                        const districtId = response.district;
                        this.showResult(wardId, districtId, postcode);
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
        const searchForm = document.querySelector('#covid-restrictions-lookup-form');
        searchForm.classList.remove('hidden');

        searchForm.addEventListener('submit', (event) => {
            event.preventDefault();

            this.resultsSection.innerHTML = '';

            const postcodeField = searchForm.querySelector('#postcode');
            const postcode = postcodeField.value.trim().toUpperCase().replace(/\s+/g, '');
            const isValid = this.validatePostcode(postcode);
            const findButton = searchForm.querySelector('[type="submit"]');
            const fieldset = searchForm.querySelector('fieldset');

            if (postcode === '') {
                this.setErrorMessage(isValid, 'Please enter a postcode', 'required-postcode', postcodeField);
            } else {
                this.setErrorMessage(isValid, 'Please enter a valid postcode', 'invalid-postcode', postcodeField);
            }

            if (isValid) {
                fieldset.disabled = true;

                this.getLoctionForPostcode(postcode)
                    .then(data => {
                        const response = JSON.parse(data.response);
                        const wardId = response.ward;
                        const districtId = response.district;
                        this.showResult(wardId, districtId, postcode);
                        window.history.pushState({}, '', `#!/${postcode}`);

                        fieldset.disabled = false;
                    }, error => {
                        if (error.status === 404) {
                            this.setErrorMessage(false, 'Enter a full valid postcode in Scotland', 'invalid-postcode', postcodeField);
                        } else {
                            this.setErrorMessage(false, 'Sorry, we have a problem at our side. Please try again later.', 'service-unavailable', findButton);
                        }

                        fieldset.disabled = false;
                    }).catch(() => {
                        fieldset.disabled = false;
                    });
            }
        });
    },

    showResult: function (wardId, districtId, searchTerm) {
        let localRestrictions = [];

        const wardRestrictions = this.currentRestrictions.filter((restriction) => restriction.type === 'electoral-ward');
        const authorityRestrictions = this.currentRestrictions.filter((restriction) => restriction.type === 'local-authority');

        for (let i = 0, il = wardRestrictions.length; i < il; i++) {
            if (wardRestrictions[i].id === wardId) {
                localRestrictions.push(wardRestrictions[i]);
                break;
            }
        }

        for (let i = 0, il = this.currentRestrictions.length; i < il; i++) {
            if (authorityRestrictions[i].id === districtId) {
                localRestrictions.push(authorityRestrictions[i]);
                break;
            }
        }

        const templateData = {
            restriction: localRestrictions[0],
            searchTerm: searchTerm
        };

        if (localRestrictions[1]) {
            templateData.restriction.parent = localRestrictions[1];
        }

        if (!localRestrictions.length) {
            console.warn('no restrictions found');
            return false;
        }

        this.landingSection.classList.add('hidden');
        this.resultsSection.classList.remove('hidden');

        this.resultsSection.innerHTML = resultTemplate(templateData);
        window.setTimeout(() => {
            if (!this.isInViewport(this.resultsSection)) {
                this.resultsSection.scrollIntoView();
            }
        }, 0);
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
            errorMessageElement.innerText = message;
        }
    },

    getLoctionForPostcode: function (postcode) {
        return this.promiseRequest(`/service/geosearch/postcodes/${postcode}`);
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

    validatePostcode: function (postcode) {
        postcode = postcode.trim().toUpperCase().replace(/\s+/g, '');

        let postcodeRegExp = new RegExp('^[A-Z]{1,2}[0-9R][0-9A-Z]?[0-9][ABD-HJLNP-UW-Z]{2}$');
        let valid = postcode.match(postcodeRegExp) !== null;

        return valid;
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

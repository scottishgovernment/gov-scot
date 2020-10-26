/* global window */

'use strict';

const resultTemplate = function (templateData) {
    return `<h1>Local COVID Alert Level: ${templateData.localRestrictions.level.title}</h1>
    <p>We've matched the postcode <strong>${templateData.searchTerm}</strong> to <strong>${templateData.localRestrictions.title}</strong>.

    <h2>Current Local COVID Alert Level</h2>
    <p>This area is in Local COVID Alert Level: ${templateData.localRestrictions.level.title}.</p>
    <p><a href="/${templateData.localRestrictions.level.link}">What you can and cannot do.</a></p>

    <div id="restrictions-detail">
    </div>

    <h2>Find information about somewhere else</h2>
    <p><a href="#" class="js-enter-another">Enter another postcode.</a></p>`;
};

// const restrictionsDetailTemplate = function (templateData) {
//     return `<div data-module="ds-accordion" class="ds_accordion">
//         <div class="ds_accordion-item">
//             <input aria-labelledby="panel-720627-heading" id="panel-720627" type="checkbox" class="ds_accordion-item__control hidden">
//             <div class="ds_accordion-item__header">
//                 <h3 id="panel-720627-heading" class="ds_accordion-item__title">What you can and cannot do</h3>

//                 <div class="ds_accordion-item__indicator">&nbsp;</div>
//                 <label for="panel-720627" class="ds_accordion-item__label"><span class="hidden">Show this section</span></label>
//             </div>

//             <div class="ds_accordion-item__body">
//                 ${templateData.body}
//             </div>
//         </div>
//     </div>`;
// };

const covidLookup = {
    init: function () {
        this.requestCurrentRestrictions()
            .then((data) => {
                this.currentRestrictions = JSON.parse(data.response);
                this.enableSearchForm();
                this.checkCurrentView();
            });

        this.landingSection = document.querySelector('#lookup-landing');
        this.resultsSection = document.querySelector('#lookup-results');

        this.resultsSection.addEventListener('click', (event) => {
            if (event.target.classList.contains('js-enter-another')) {
                event.preventDefault();
                this.landingSection.classList.remove('hidden');
                this.resultsSection.classList.add('hidden');
                window.history.pushState({}, '', '#!/');
                window.setTimeout(() => this.landingSection.scrollIntoView(), 0);
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
        const searchForm = document.querySelector('#covid-lookup');
        searchForm.classList.remove('hidden');

        searchForm.addEventListener('submit', (event) => {
            event.preventDefault();

            this.resultsSection.innerHTML = '';

            const postcodeField = searchForm.querySelector('#postcode');
            const postcode = postcodeField.value.trim().toUpperCase().replace(/\s+/g, '');
            const isValid = this.validatePostcode(postcode);

            this.setErrorMessage(isValid, 'Please enter a valid postcode', postcodeField);

            if (isValid) {
                this.getLoctionForPostcode(postcode)
                    .then((data) => {
                        const response = JSON.parse(data.response);
                        const wardId = response.ward;
                        const districtId = response.district;
                        this.showResult(wardId, districtId, postcode);
                        window.history.pushState({}, '', `#!/${postcode}`);
                    }).catch((e)=>console.log(e));
            }
        });
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

    showResult: function (wardId, districtId, searchTerm) {
        let localRestrictions = false;

        const wardRestrictions = this.currentRestrictions.filter((restriction) => restriction.type === 'electoral-ward');
        const authorityRestrictions = this.currentRestrictions.filter((restriction) => restriction.type === 'local-authority');

        for (let i = 0, il = wardRestrictions.length; i < il; i++) {
            if (wardRestrictions[i].id === wardId) {
                localRestrictions = wardRestrictions[i];
                break;
            }
        }

        if (!localRestrictions) {
            for (let i = 0, il = this.currentRestrictions.length; i < il; i++) {
                if (authorityRestrictions[i].id === districtId) {
                    localRestrictions = authorityRestrictions[i];
                    break;
                }
            }
        }

        if (!localRestrictions) {
            console.warn('no restrictions found');
            return false;
        }

        this.landingSection.classList.add('hidden');
        this.resultsSection.classList.remove('hidden');
        this.resultsSection.innerHTML = resultTemplate({searchTerm: searchTerm, localRestrictions: localRestrictions});
        window.setTimeout(() => this.resultsSection.scrollIntoView(), 0);

        // this.requestLocalRestrictionDetails('/' + localRestrictions.level.link)
        //     .then((data) => {
        //         const tempDiv = document.createElement('div');
        //         tempDiv.innerHTML = data.responseText;
        //         this.resultsSection.querySelector('#restrictions-detail').innerHTML = restrictionsDetailTemplate({ body: tempDiv.querySelector('.body-content').innerHTML });
        //     });
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

    setErrorMessage: function (valid, message, field) {
        const question = field.closest('.ds_question');
        const errorMessageElement = field.parentNode.querySelector('.ds_question__error-message');

        if (valid) {
            question.classList.remove('ds_question--error');
            field.classList.remove('ds_input--error');
            field.removeAttribute('aria-invalid', 'true');
            errorMessageElement.classList.add('hidden');
        } else {
            question.classList.add('ds_question--error');
            field.classList.add('ds_input--error');
            field.setAttribute('aria-invalid', 'true');
            errorMessageElement.classList.remove('hidden');
            errorMessageElement.innerText = message;
        }
    }
};

window.format = covidLookup;
window.format.init();

export default covidLookup;

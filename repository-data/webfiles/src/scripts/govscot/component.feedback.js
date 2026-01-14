//feedback.js
/*
 Contains functionality for on page feedback
 */

'use strict';

import temporaryFocus from '@scottish-government/design-system/src/base/tools/temporary-focus/temporary-focus';

const errorSummaryTemplate = {
    render: function (templateData) {
        if (templateData.errors.length === 0) {
            return ''
        } else {
            let html = `
            <div class="ds_error-summary" id="error-summary" aria-labelledby="error-summary-title" role="alert">
                <h2 class="ds_error-summary__title" id="error-summary-title">There is a problem</h2>`;

            if (templateData.errors.length > 1) {
                html += `<ul class="ds_error-summary__list">`;

                templateData.errors.forEach((item) => {
                    html += `<li>`;
                    if (item.fragmentId) {
                        html += `<a class="ds_error-summary__link" href="#${item.fragmentId}">${item.fieldName}: ${item.message}</a>`;
                    } else {
                        html += `${item.message}`;
                    }
                    html += `</li>`;
                });

                html += `</ul>`;
            } else {
                html += `<p class="ds_error-summary__list">`;
                if (templateData.errors[0].fragmentId) {
                    html += `<a class="ds_error-summary__link" href="#${templateData.errors[0].fragmentId}">${templateData.errors[0].fieldName}: ${templateData.errors[0].message}</a>`;
                } else {
                    html += `${templateData.errors[0].message}`;
                }
                html += `</p>`
            }

            html += `</div>`;

            return html
        }
    }
}

const feedbackTemplate = {
    render: function (templateData) {
        if (templateData.step === 'type') {
            return `<div id="feedback-step-1" class="feedback-step">
                <div class="ds_question">
                    <fieldset id="feedback-type" data-newvalidation="true" data-validation="requiredRadio">
                        <legend>Was this page helpful?</legend>

                        <div class="ds_radio  ds_radio--small">
                            <input value="yes" type="radio" class="ds_radio__input" name="feedback-type" id="feedback-yes" />
                            <label class="ds_radio__label" for="feedback-yes">Yes</label>
                        </div>

                        <div class="ds_radio  ds_radio--small">
                            <input value="no" type="radio" class="ds_radio__input" name="feedback-type" id="feedback-no" />
                            <label class="ds_radio__label" for="feedback-no">No</label>
                        </div>

                        <div class="ds_radio  ds_radio--small">
                            <input value="yesbut" type="radio" class="ds_radio__input" name="feedback-type" id="feedback-yesbut" />
                            <label class="ds_radio__label" for="feedback-yesbut">Yes, but</label>
                        </div>
                    </fieldset>
                </div>

                <button type="button" class="js-continue  ds_button  ds_button--secondary">Continue</button>
            </div>`;
        } else if (templateData.step === 'details') {
            let html = `<div id="feedback-step-2" class="feedback-step">`;

            if (templateData.data.type === 'no') {
                html += `
                    <div class="ds_question">
                        <label class="ds_label" for="reason">Choose a reason for your feedback</label>
                        <div class="ds_select-wrapper">
                            <select name="reason" id="reason" class="ds_select" data-newvalidation="true" data-validation="requiredDropdown">
                                <option selected="" disabled="" value="">Please select a reason</option>
                                <option value="It wasn't detailed enough">It wasn't detailed enough</option>
                                <option value="It's hard to understand">It's hard to understand</option>
                                <option value="It's incorrect">It's incorrect</option>
                                <option value="It needs updating">It needs updating</option>
                                <option value="There's a broken link">There's a broken link</option>
                                <option value="It wasn't what I was looking for">It wasn't what I was looking for</option>
                                <option value="Other">Other</option>
                            </select>
                            <span class="ds_select-arrow" aria-hidden="true"></span>
                        </div>
                    </div>`
            } else if (templateData.data.type === 'yesbut') {
                html += `
                    <div class="ds_question">
                        <label class="ds_label" for="reason">Choose a reason for your feedback</label>
                        <div class="ds_select-wrapper">
                            <select name="reason" id="reason" class="ds_select" data-newvalidation="true" data-validation="requiredDropdown">
                                <option selected="" disabled="" value="">Please select a reason</option>
                                <option value="It's hard to understand">It's hard to understand</option>
                                <option value="It needs updating">It needs updating</option>
                                <option value="There's a broken link">There's a broken link</option>
                                <option value="There's a spelling mistake">There's a spelling mistake</option>
                                <option value="Other">Other</option>
                            </select>
                            <span class="ds_select-arrow" aria-hidden="true"></span>
                        </div>
                    </div>`
            }

            html += `<div class="ds_question">
                    <label class="ds_label" for="feedback-comments">Your comments</label>
                    <p class="ds_hint-text">Your feedback helps us to improve this website. Do not give any personal information because we cannot reply to you directly.</p>
                    <textarea name="comment" class="ds_input" rows="5" maxlength="250" id="feedback-comments" {% if data.type === 'no' or data.type === 'yesbut' %}data-newvalidation="true" data-validation="requiredField"{% endif %}></textarea>
                </div>

                <div class="ds_button-group">
                    <button type="button" class="js-cancel  ds_button  ds_button--secondary">Cancel</button>
                    <button type="submit" class="js-submit  ds_button">Submit</button>
                </div>
            </div>`;

            return html;
        }
    }
}

// todo: replace with DS PromiseRequest if it gets updated to support POST
const PromiseRequest = function (url, method, data, headers) {
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

        if (typeof headers === 'object') {
            for (const header of Object.entries(headers)) {
                request.setRequestHeader(header[0], header[1])
            }
        }

        request.send(data);
    });
};

// todo: this is a clone of the needed commonForms fields from mygov
const commonForms = {
    appendErrorContainer: function (field) {
        const formErrors = document.querySelector('.form-errors');

        if (!formErrors) {
            return;
        }

        let errorContainer = document.querySelector(`.${field.id}-errors`);

        if (!errorContainer) {
            errorContainer = document.createElement('ul');
            errorContainer.classList.add('ds_error-summary__list');
            errorContainer.classList.add(`${field.id}-errors`);
            formErrors.appendChild(errorContainer);
        }
    },

    getLabelText: function (field) {
        if (field.getAttribute('aria-labeledby')) {
            return document.getElementById(field.getAttribute('aria-labeledby')).innerHTML;
        } else if (field.nodeName === 'FIELDSET') {
            return null;
        } else {
            return document.querySelector(`label[for="${field.id}"]`).innerHTML;
        }
    },

    getTitleFromLegend: function (legendEl) {
        if (!legendEl) {
            return '';
        }
        if (legendEl.querySelector('.js-question-title')) {
            return legendEl.querySelector('.js-question-title').innerHTML;
        } else {
            return legendEl.innerHTML;
        }
    },

    requiredDropdown: function (field) {
        const value = field.value;
        const fieldName = commonForms.getLabelText(field);
        const message = 'Select one of the options';

        const valid = value !== null && value !== '';

        commonForms.toggleFormErrors(field, valid, fieldName, message);
        commonForms.toggleCurrentErrors(field, valid, 'required', message);

        return valid;
    },

    requiredField: function (field, customMessage) {
        const trimmedValue = field.value.trim();
        const fieldName = commonForms.getLabelText(field);

        const valid = trimmedValue !== '';

        let message = 'This field is required';
        if (field.dataset.message || customMessage) {
            message = '';
            if (fieldName) {
                message += `<strong>${fieldName}</strong> <br>`;
            }
            message += `${field.dataset.message || customMessage}`;
        }

        commonForms.toggleFormErrors(field, valid, fieldName, message);
        commonForms.toggleCurrentErrors(field, valid, 'invalid-required', message);

        return valid;
    },

    requiredRadio: function (container) {
        const radioButtons = [].slice.call(container.querySelectorAll('input[type="radio"]'));

        const title = commonForms.getTitleFromLegend(container.querySelector('legend'));
        const message = 'Select one of the options';

        let valid = false;

        radioButtons.forEach(radioButton => {
            if (radioButton.checked) {
                valid = true;
            }
        });

        commonForms.toggleFormErrors(container, valid, title, message);
        commonForms.toggleCurrentErrors(container, valid, 'required', message);

        return valid;
    },

    toggleCurrentErrors: function (field, valid, errorClass, message) {
        // add error to field's errors object
        field.errors = field.errors || {};
        if (!valid) {
            field.errors[errorClass] = message;
        } else {
            delete field.errors[errorClass];
        }

        // obtain field ID
        let fieldId;

        if (field.nodeName !== 'INPUT' && field.nodeName !== 'SELECT' && field.nodeName !== 'TEXTAREA') {
            // assume fieldset
            if (field.querySelectorAll('input[type="radio"]').length) {
                fieldId = field.querySelectorAll('input[type="radio"]')[0].name;
            } else if (field.querySelectorAll('input[type="checkbox"]').length) {
                fieldId = field.querySelectorAll('input[type="checkbox"]')[0].id;
            } else {
                fieldId = field.id;
            }
        } else {
            fieldId = field.id;
        }

        if (!fieldId) { return; }

        // update error display
        let question;
        if (document.getElementById(fieldId) && document.getElementById(fieldId).closest('.ds_question')) {
            question = document.getElementById(fieldId).closest('.ds_question');
        } else {
            // this essentially swallows errors when no ds_question element is found
            question = document.createElement('div');
        }

        // remove error container
        let errorContainer = document.querySelector(`#${fieldId}-errors`);
        if (errorContainer) {
            errorContainer.parentNode.removeChild(errorContainer);
        }

        if (Object.keys(field.errors).length) {
            question.classList.add('ds_question--error');
            field.setAttribute('aria-invalid', true);

            // add an error container
            if (field.getAttribute('type') !== 'hidden') {
                field.setAttribute('aria-describedby', `${fieldId}-errors`);
                errorContainer = document.createElement('div');
                errorContainer.classList.add('ds_question__error-message');
                errorContainer.id = `${fieldId}-errors`;
                errorContainer.dataset.field = fieldId;

                if (field.nodeName === 'FIELDSET') {
                    field.querySelector('legend').insertAdjacentElement('afterend', errorContainer);
                } else {
                    document.querySelector(`[for=${field.id}]`).insertAdjacentElement('afterend', errorContainer);
                }
            }

            for (let key in field.errors) {
                const errorEl = document.createElement('p');
                errorEl.classList.add(key);
                errorEl.innerHTML = `<span class="visually-hidden">Error:</span> ${field.errors[key]}`;
                errorContainer.appendChild(errorEl);
            }
        } else {
            question.classList.remove('ds_question--error');
            field.removeAttribute('aria-invalid');
        }

        if (errorContainer) {
            if (window.DS && window.DS.tracking) {
                window.DS.tracking.init(errorContainer);
            }
        }
    },

    validateInput: function (field, validationChecks, highlightField = true) {
        commonForms.appendErrorContainer(field);
        let valid = true;

        for (let i = 0; i < validationChecks.length; i++) {
            if (validationChecks[i] && validationChecks[i](field) === false) {
                valid = false;
            }
        }

        if (highlightField) {
            if (!valid) {
                field.classList.add('ds_input--error');
            } else {
                field.classList.remove('ds_input--error');
            }
        }

        return valid;
    },

    renderErrorSummary: function (errors) {
        const errorSumaryContainerElement = document.querySelector('.js-error-summary-container');
        errorSumaryContainerElement.innerHTML = errorSummaryTemplate.render({ errors: errors });
        window.DS.tracking.init(errorSumaryContainerElement);

        if (errors.length > 0) {
            temporaryFocus(errorSumaryContainerElement.querySelector('.ds_error-summary'));
        }
    },

    toggleFormErrors: function (field, valid, fieldName = '', message = '') {
        this.errors = this.errors || [];
        this.errors = this.errors.filter(item => !(item.fragmentId === field.id && item.message === message));
        fieldName = fieldName || this.getLabelText(field);
        if (!valid) {
            this.errors.push({ fragmentId: field.id, fieldName: fieldName, message: message });
        }
    },

    validateStep: function (container) {
        this.errors = [];

        const itemsThatNeedToBeValidated = [].slice.call(container.querySelectorAll('[data-validation]')).filter(item => item.offsetParent);
        itemsThatNeedToBeValidated.forEach(item => {
            const validations = item.getAttribute('data-validation').split(' ');
            const validationChecks = [];
            for (let i = 0, il = validations.length; i < il; i++) {
                if (commonForms[validations[i]]) {
                    validationChecks.push(commonForms[validations[i]]);
                }
            }

            commonForms.validateInput(item, validationChecks);
        });

        const invalidFields = [].slice.call(container.querySelectorAll('[aria-invalid="true"],[data-invalid="true"]')).filter(item => item.offsetParent && item.dataset.validation);

        // elear errors on no longer validated fields
        const noLongerValidatedFields = [].slice.call(container.querySelectorAll('[aria-invalid="true"],[data-invalid="true"]')).filter(item => typeof item.dataset.validation === 'undefined');
        noLongerValidatedFields.forEach(item => {
            item.errors = {};
            commonForms.validateInput(item, []);
            commonForms.toggleCurrentErrors(item, true, '', '')
        });

        this.renderErrorSummary(this.errors);

        return invalidFields.length === 0;
    }
}

const feedbackForm = {
    init: function () {
        this.feedbackContainer = document.getElementById('feedback');

        if (!this.feedbackContainer) {
            // no feedback form on this page, do nothing
            return;
        }

        this.form = this.feedbackContainer.querySelector('form');

        this.addTracking();
        this.attachEventHandlers();
        this.setInitialData();
        this.populateContainerFromTemplate(this.form, feedbackTemplate, { step: 'type' });
        this.feedbackContainer.classList.add('js-initialised');
    },

    addTracking: function () {
        this.feedbackContainer.addEventListener('change', event => {
            if (event.target.classList.contains('ds_radio__input')) {
                const type = this.form.querySelector('[name="feedback-type"]:checked').value;
                this.updateDataLayer(type, 'feedbackRadio');
            }

            if (event.target.classList.contains('ds_select')) {
                this.updateDataLayer(this.data.type, 'feedbackSelect');
            }
        });
    },

    attachEventHandlers: function () {
        this.feedbackContainer.addEventListener('click', event => {
            if (event.target.classList.contains('js-continue')) {
                if (commonForms.validateStep(this.form)) {
                    this.data.type = this.form.querySelector('[name="feedback-type"]:checked').value;

                    this.populateContainerFromTemplate(this.form, feedbackTemplate, {
                        step: 'details',
                        data: this.data
                    });

                    temporaryFocus(this.form);
                }
            }

            if (event.target.classList.contains('js-cancel')) {
                this.resetForm();
                temporaryFocus(this.form);
            }

            if (event.target.classList.contains('js-submit')) {
                event.preventDefault();
                if (commonForms.validateStep(this.form)) {
                    this.data.reason = this.getFeedbackReason();
                    this.data.freetext = this.getFeedbackFreeText();
                    this.submitFeedback();
                }
            }
        });
    },

    getFeedbackReason: function () {
        let reason = '';

        if (this.data.type !== 'yes') {
            reason = this.form.querySelector('#reason').value;
        }

        return reason;
    },

    getFeedbackFreeText: function () {
        const freeText = this.form.querySelector('#feedback-comments').value;
        return freeText.substring(0, 250);
    },

    populateContainerFromTemplate(container, template, templateData) {
        container.innerHTML = template.render(templateData);
        window.DS.tracking.init(container);
    },

    resetForm: function () {
        delete this.data.reason;
        delete this.data.type;
        this.populateContainerFromTemplate(this.form, feedbackTemplate, { step: 'type' });

        commonForms.renderErrorSummary([]);
    },

    setInitialData: function () {
        this.data = {
            category: document.querySelector('#page-category').value,
            contentItem: document.getElementById('documentUuid').value,
            errors: [],
            hippoContentItem: window.location.pathname,
            slug: document.location.pathname
        }

        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.has('q')) {
            this.data.searchTerm = urlParams.get('q');
        }
        if (urlParams.has('cat')) {
            this.data.searchCat = urlParams.get('cat');
        }
        if (urlParams.has('page')) {
            this.data.searchPage = urlParams.get('page');
        }
    },

    showSuccessMessage: function () {
        const errorSummaryElement = this.feedbackContainer.querySelector('.js-error-summary-container');
        const thanksElement = this.feedbackContainer.querySelector('.js-confirmation-message');

        // remove form and error summary
        this.form.parentNode.removeChild(this.form);
        errorSummaryElement.parentNode.removeChild(errorSummaryElement);

        // show success message
        thanksElement.classList.remove('fully-hidden');
    },

    submitFeedback: function () {
        // update datalayer
        this.updateDataLayer(this.data.type, 'feedbackSubmit');

        // submit feedback
        PromiseRequest('/service/feedback', 'POST', JSON.stringify(this.data), { 'Content-Type': 'application/json; charset=utf-8' })
            .then(() => {
                this.showSuccessMessage();
            })
            .catch(err => {
                let message = 'Sorry, we have a problem at our side. Please try again later.';

                if (err.status === 429) {
                    message = 'Sorry, too many requests have been submitted. Please try again later.';
                }

                commonForms.renderErrorSummary([{
                    message: message
                }]);
            });
    },

    updateDataLayer: function (type, event) {
        // update datalayer
        window.dataLayer = window.dataLayer || [];
        window.dataLayer.push({
            'type': type,
            'reason': this.data.reason,
            'event': event
        });
    }
};

export default feedbackForm;

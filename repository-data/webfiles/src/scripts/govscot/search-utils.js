// SEARCH UTILITIES

'use strict';

const searchUtils = {
    /**
     * prepends zeroes to a number, up to a set length
     * @param {number} value - number to prepend zeroes to
     * @param {number} length - desired length of number
     * @returns {string}
     */
     leadingZeroes: function (value, length) {
        let ret = value.toString();

        while (ret.length < length) {
            ret = '0' + ret.toString();
        }

        return ret;
    },

    /**
     * Takes a date string in the format we use in forms (dd/MM/yyyy) and returns a date object
     * @param {string}
     * @returns {date}
     */
    stringToDate: function (string) {
        let fragments = string.split('/');
        return new Date(`${fragments[1]}/${fragments[0]}/${fragments[2]}`);
    },

    /**
     * Takes the parameter name and optional url and returns the value of the parameter
     * @param {string} name - name of parameter
     * @param {string} url - optional url to check for parameters
     * @returns {string}
     */
    getParameterByName: function(name, url) {
        if (!url) url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        let regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    },

    /**
     * Validates $field (newer version)
     *
     * @param {object} $field - the form field to validate
     * @param {boolean} highlightField - should a visual indicator be applied to the UI
     * @param {validationChecks} - array of validation functions to test $field against
     * @returns {boolean} whether the field value is valid
     */
     validateInput: function (field, validationChecks, highlightField = true) {
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

    /**
     *  Shows or hides individual field's current errors box (box below each field)
     */
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

                if (field.querySelector('legend')) {
                    field.closest('.ds_question').querySelector('legend').insertAdjacentElement('afterend', errorContainer);
                } else {
                    field.closest('.ds_question').querySelector('label').insertAdjacentElement('afterend', errorContainer);
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

    isValidDate: function (day, month, year) {
        // solution by Matti Virkkunen: http://stackoverflow.com/a/4881951
        const isLeapYear = year % 4 === 0 && year % 100 !== 0 || year % 400 === 0;
        const daysInMonth = [31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31][month - 1];

        return day <= daysInMonth;
    },

    dateRegex: function (field) {
        const trimmedValue = field.value.trim();
        const fieldName = document.querySelector(`label[for="${field.id}"]`).innerText;
        const message = 'Enter the date as DD/MM/YYYY';

        // A regular expression only allowing dd/mm/yyyy format
        const regex = new RegExp('^\\d\\d\\/\\d\\d\\/\\d\\d\\d\\d$');

        // check date is a valid date
        const day = parseInt(trimmedValue.slice(0, 2));
        const month = parseInt(trimmedValue.slice(3, 5));
        const year = parseInt(trimmedValue.slice(6, 10));

        const valid = trimmedValue === '' || (searchUtils.isValidDate(day, month, year) && trimmedValue.match(regex) !== null);

        searchUtils.toggleCurrentErrors(field, valid, 'invalid-date-format', message);

        return valid;
    },

    afterDate: function (field, minDate) {
        const trimmedValue = field.value.trim();
        const fieldName = document.querySelector(`label[for="${field.id}"]`).innerText;
        const message = `This date must be after ${searchUtils.leadingZeroes(minDate.getDate(), 2)}/${searchUtils.leadingZeroes((minDate.getMonth() + 1), 2)}/${minDate.getFullYear()}`;
        let valid = false;

        if (searchUtils.dateRegex(field) && trimmedValue.length) {
            const day = parseInt(trimmedValue.slice(0, 2));
            const month = parseInt(trimmedValue.slice(3, 5));
            const year = parseInt(trimmedValue.slice(6, 10));

            const date = new Date(`${month}/${day}/${year}`);

            date.setHours(23);
            date.setMinutes(59);
            date.setSeconds(59);

            valid = date >= minDate;

            searchUtils.toggleCurrentErrors(field, valid, 'invalid-after-date', message);
        }

        return valid;
    },

    beforeDate: function (field, maxDate) {
        const trimmedValue = field.value.trim();
        const fieldName = document.querySelector(`label[for="${field.id}"]`).innerText;
        const message = `This date must be before ${searchUtils.leadingZeroes(maxDate.getDate(), 2)}/${searchUtils.leadingZeroes((maxDate.getMonth() + 1), 2)}/${maxDate.getFullYear()}`;
        let valid = false;

        if (searchUtils.dateRegex(field) && trimmedValue.length) {
            const day = parseInt(trimmedValue.slice(0, 2));
            const month = parseInt(trimmedValue.slice(3, 5));
            const year = parseInt(trimmedValue.slice(6, 10));

            const date = new Date(`${month}/${day}/${year}`);

            date.setHours(23);
            date.setMinutes(59);
            date.setSeconds(59);

            valid = date < maxDate;

            searchUtils.toggleCurrentErrors(field, valid, 'invalid-before-date', message);
        }

        return valid;
    },

    addError: function (message, inputGroup, errorId) {
        let errorContainer = inputGroup.find('.ds_question--error-message');

        if (errorContainer.length === 0) {
            errorContainer = document.createElement('div');
            errorContainer.id = errorId;
            errorContainer.classList.add('ds_question--error-message');
            inputGroup.insertBefore(errorContainer, inputGroup.firstChild);
        }

        errorContainer.text(message);
        inputGroup.addClass('input-group--has-error');
    },

    removeError: function (question) {
        if (question.classList.contains('ds_question--error')) {
            question.querySelector('.ds_question__error-message').remove();
            question.querySelector('[aria-describedby]').removeAttribute('aria-describedby');
            question.classList.remove('ds_question--error');

            const inputs = [].slice.call(question.querySelectorAll('.ds_input'));
            inputs.forEach(input => {
                input.classList.remove('ds_input--error');
                input.removeAttribute('aria-invalid');
            });
        }
    },

    /**
     * Updates the search parameters stored on the query string
     *
     * @param params
     */
    getNewQueryString: function (params) {
        let newQueryStringParams = [],
            newQueryString;

        if (params.cat) {
            newQueryStringParams.push('cat=' + params.cat);
        }

        if (params.q) {
            newQueryStringParams.push('q=' + params.q);
        }
        if (params.topics) {
            newQueryStringParams.push('topics=' + params.topics.join(';'));
        }
        if (params.publicationTypes) {
            newQueryStringParams.push('publicationTypes=' + params.publicationTypes.join(';'));
        }

        if (params.topic) {
            params.topic.forEach(topic => {
                newQueryStringParams.push('topic=' + topic);
            });
        }
        if (params.type) {
            params.type.forEach(type => {
                newQueryStringParams.push('type=' + type);
            });
        }

        if (params.date) {
            if (params.date.begin && params.date.begin.length) {
                newQueryStringParams.push('begin=' + params.date.begin);
            }

            if (params.date.end && params.date.end.length) {
                newQueryStringParams.push('end=' + params.date.end);
            }
        }

        if (params.page) {
            newQueryStringParams.push('page=' + params.page);
        }

        if (params.sort) {
            newQueryStringParams.push('sort=' + params.sort);
        }

        if (newQueryStringParams.length > 0) {
            newQueryString = '?' + newQueryStringParams.join('&');
        } else {
            newQueryString = '?';
        }

        return newQueryString;
    }
};

export default searchUtils;

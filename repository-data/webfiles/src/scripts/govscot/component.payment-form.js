/**
 * Payment component
 */

/* global document, window */

'use strict';

const paymentForm = {
    settings: {
        paymentUrl: '/service/payment'
    },

    init: function () {
        this.paymentForm = document.getElementById('payment-form');
        if (this.paymentForm) {
            this.attachEventHandlers();
        }
    },

    attachEventHandlers: function () {
        const that = this;
        // validate on form submission
        this.paymentForm.addEventListener('submit', function (event) {
            event.preventDefault();

            // submit the payment request
            const payment = {
                orderCode: document.getElementById('orderCode').value.trim(),
                amount: document.getElementById('amount').value.trim(),
                description: document.getElementById('description').value.trim(),
                emailAddress: document.getElementById('email-address').value.trim()
            };

            that.removeErrorMessages();

            if (that.validateInputs()) {
                that.sendPayment(payment);
            }
        });
    },

    validateInputs: function () {
        const errors = [];
        const orderCodeInput = document.getElementById('orderCode');
        const orderCodeInputQuestion = orderCodeInput.parentNode;
        const amountInput = document.getElementById('amount');
        const amountInputQuestion = amountInput.parentNode.parentNode;
        const emailInput = document.getElementById('email-address');
        const emailInputQuestion = emailInput.parentNode;
        const descriptionInput = document.getElementById('description');
        const descriptionInputQuestion = descriptionInput.parentNode;

        // reference number restrictions: 64 characters, no spaces
        if (orderCodeInput.value.length > 64) {
            errors.push({message: 'Payment Reference is too long (maximum is 64 characters)', element: orderCodeInput});

            orderCodeInputQuestion.classList.add('ds_question--error');
            orderCodeInput.classList.add('ds_input--error');
        }

        if (orderCodeInput.value.indexOf(' ') > -1) {
            errors.push({message: 'Payment Reference cannot contain spaces', element: orderCodeInput});

            orderCodeInputQuestion.classList.add('ds_question--error');
            orderCodeInput.classList.add('ds_input--error');

            const spacesMessage = orderCodeInputQuestion.querySelector('#payment-ref-spaces');
            spacesMessage.classList.remove('fully-hidden');
        }

        if (orderCodeInput.value.indexOf('&') > -1) {
            errors.push({message: 'Payment Reference cannot contain ampersand', element: orderCodeInput});

            orderCodeInputQuestion.classList.add('ds_question--error');
            orderCodeInput.classList.add('ds_input--error');

            const spacesMessage = orderCodeInputQuestion.querySelector('#payment-ref-spaces');
            spacesMessage.classList.remove('fully-hidden');
        }

        // ref number is a required field
        // if (orderCodeInput.value.length === 0) {
        //     errors.push({message: 'Payment Reference is required', element: orderCodeInput});

        //     orderCodeInputQuestion.classList.add('ds_question--error');
        //     orderCodeInput.classList.add('ds_input--error');
        // }

        // value: min £0.01
        if (parseFloat(amountInput.value) < 0.01) {
            errors.push({message: 'Amount cannot be less than £0.01', element: amountInput});
            amountInputQuestion.classList.add('ds_question--error');
            amountInput.classList.add('ds_input--error');

            const amountMinMessage = amountInputQuestion.querySelector('#amount-min');
            amountMinMessage.classList.remove('fully-hidden');
        }

        // value: max £5000
        if (parseFloat(amountInput.value) > 5000.00) {
            errors.push({message: 'Amount cannot be more than £5000.00', element: amountInput});
            amountInputQuestion.classList.add('ds_question--error');
            amountInput.classList.add('ds_input--error');

            const amountMaxMessage = amountInputQuestion.querySelector('#amount-max');
            amountMaxMessage.classList.remove('fully-hidden');
        }

        // amount is a required field
        // if (amountInput.value.length === 0) {
        //     errors.push({message: 'Amount is required', element: amountInput});

        //     amountInputQuestion.classList.add('ds_question--error');
        //     amountInput.classList.add('ds_input--error');
        // }

        // description is a required field
        // if (descriptionInput.value.length === 0) {
        //     errors.push({message: 'Description is required', element: descriptionInput});

        //     descriptionInputQuestion.classList.add('ds_question--error');
        //     descriptionInput.classList.add('ds_input--error');
        // }

        // email must be valid format
        if (emailInput.value.length > 0) {
            const trimmedValue = emailInput.value.trim();
            const regex = /^[^@ ]+@[^@ ]+\.[^@ ]+$/;
            const valid = trimmedValue === '' || trimmedValue.match(regex) !== null;

            if (!valid) {
                errors.push({ message: 'Email address is not in a valid format', element: emailInput });
                emailInputQuestion.classList.add('ds_question--error');
                emailInput.classList.add('ds_input--error');

                const invalidEmailMessage = emailInputQuestion.querySelector('#invalid-email');
                invalidEmailMessage.classList.remove('fully-hidden');
            }
        }

        // email is a required field
        if (emailInput.value.length === 0) {
            errors.push({message: 'Email address is required', element: emailInput});

            emailInputQuestion.classList.add('ds_question--error');
            emailInput.classList.add('ds_input--error');
        }

        this.showErrors(errors);

        return errors.length === 0;
    },

    showErrors: function (errors) {
        if (errors.length) {
            const errorSummary = document.getElementById('error-summary');
            const errorList = document.createElement('ul');
            errorList.classList.add('ds_error-summary__list');

            errors.forEach(function (error) {
                const errorItem = document.createElement('li');
                const errorLink = document.createElement('a');
                errorLink.innerText = error.message;
                errorLink.href = `#${error.element.id}`;
                errorItem.appendChild(errorLink);
                errorList.appendChild(errorItem);

                errorLink.addEventListener('click', (event) => {
                    event.preventDefault();

                    const errorInput = document.querySelector(event.target.getAttribute('href'));

                    let testNode = errorInput;

                    while (!testNode.classList.contains('ds_question')) {
                        testNode = testNode.parentNode;
                    }

                    testNode.scrollIntoView();
                    errorInput.focus();
                });
            });

            errorSummary.appendChild(errorList);

            this.showErrorSummary();
        }
    },

    sendPayment: function (payment) {
        const that = this;
        const submitButton = document.querySelector('#submit-payment');
        submitButton.setAttribute('disabled', 'disabled');

        const xhr = new XMLHttpRequest();
        xhr.open('POST', this.settings.paymentUrl, true);

        xhr.setRequestHeader("Content-Type", "application/json; charset=utf-8'");

        xhr.onreadystatechange = function () {
            if (this.readyState === XMLHttpRequest.DONE) {
                const responseJSON = JSON.parse(this.responseText);

                if (this.status === 200) {
                    // success
                    window.location.href = responseJSON.paymentUrl;
                } else if (responseJSON.violations && responseJSON.violations.length) {
                    // add errors
                    const errors = [];

                    for (let i = 0, il = responseJSON.violations.length; i < il; i++) {
                        const violation = responseJSON.violations[i];

                        const field = document.getElementById(violation.field);
                        const question = field.parentNode.classList.contains('ds_question') ? field.parentNode : field.parentNode.parentNode;

                        errors.push({ message: violation.message, element: field });

                        question.classList.add('ds_question--error');
                        field.classList.add('ds_input--error');
                    }

                    that.showErrors(errors);
                } else if (responseJSON.success === false && responseJSON.error) {
                    that.removeErrorMessages();

                    // just show error summary with no highlighted fields
                    const errorSummary = document.getElementById('error-summary');
                    const errorList = document.createElement('ul');
                    const errorItem = document.createElement('li');

                    errorList.classList.add('ds_error-summary__list');
                    errorItem.innerText = responseJSON.error;
                    errorList.appendChild(errorItem);
                    errorSummary.appendChild(errorList);

                    that.showErrorSummary();
                } else {
                    // fail
                    const errorSummary = document.getElementById('error-summary');
                    errorSummary.querySelector('.error-summary-message').innerText = 'Sorry, we are currently unable to submit your request. Please try again later.';

                    that.showErrorSummary();
                }

                submitButton.removeAttribute('disabled');
            }
        };

        xhr.send(JSON.stringify(payment));
    },

    removeErrorMessages: function () {
        const errorSummary = document.getElementById('error-summary');
        errorSummary.querySelector('.error-summary-message').innerHTML = '';

        [].slice.call(errorSummary.querySelectorAll('ul')).forEach(ul => ul.parentNode.removeChild(ul));

        errorSummary.classList.add('fully-hidden');

        [].slice.call(document.querySelectorAll('.ds_question--error')).forEach(question => question.classList.remove('ds_question--error'));
        [].slice.call(document.querySelectorAll('.ds_input--error')).forEach(input => input.classList.remove('ds_input--error'));
        [].slice.call(document.querySelectorAll('.ds_question__error-message')).forEach(message => message.classList.add('fully-hidden'));
    },

    showErrorSummary: function () {
        const errorSummary = document.getElementById('error-summary');

        errorSummary.classList.remove('fully-hidden');
        errorSummary.scrollIntoView();
        errorSummary.classList.add('flashable--flash');
        window.setTimeout(function () {
            errorSummary.classList.remove('flashable--flash');
        }, 200);
    }
};

export default paymentForm;

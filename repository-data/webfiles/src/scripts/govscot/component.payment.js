/**
 * Payment component
 */

/* global document, window */

'use strict';

import $ from 'jquery';
import CharacterCount from '../../scss/design-system-preview/forms/character-count/character-count';

const characterCountModules = [].slice.call(document.querySelectorAll('[data-module="ds-character-count"]'));
characterCountModules.forEach(characterCount => new CharacterCount(characterCount).init());

const Payment = {
    settings: {
        paymentUrl: '/service/payment'
    },

    init: function () {
        this.attachEventHandlers();
    },

    attachEventHandlers: function () {
        const that = this;
        // validate on form submission
        $('#payment-form').on('submit', function (event) {
            event.preventDefault();

            // submit the payment request
            const payment = {
                orderCode: $('#orderCode').val(),
                amount: $('#amount').val(),
                description: $('#description').val(),
                emailAddress: $('#email').val()
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
        const emailInput = document.getElementById('email');
        const emailInputQuestion = emailInput.parentNode;
        const errorSummary = document.getElementById('error-summary');

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
            spacesMessage.classList.remove('hidden');
        }

        // value: min £0.01
        if (parseFloat(amountInput.value) < 0.01) {
            errors.push({message: 'Amount cannot be less than £0.01', element: amountInput});
            amountInputQuestion.classList.add('ds_question--error');
            amountInput.classList.add('ds_input--error');

            const amountMinMessage = amountInputQuestion.querySelector('#amount-min');
            amountMinMessage.classList.remove('hidden');
        }

        // value: max £5000
        if (parseFloat(amountInput.value) > 5000.00) {
            errors.push({message: 'Amount cannot be more than £5000.00', element: amountInput});
            amountInputQuestion.classList.add('ds_question--error');
            amountInput.classList.add('ds_input--error');

            const amountMaxMessage = amountInputQuestion.querySelector('#amount-max');
            amountMaxMessage.classList.remove('hidden');
        }

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
                invalidEmailMessage.classList.remove('hidden');
            }
        }

        if (errors.length) {
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

        return errors.length === 0;
    },

    sendPayment: function (payment) {
        const that = this;

        $.ajax({
            type: 'POST',
            url: this.settings.paymentUrl,
            data: JSON.stringify(payment),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json'
        }).done(function(data) {
            window.location.href = data.paymentUrl;
        }).fail(function () {
            const errorSummary = document.getElementById('error-summary');
            errorSummary.querySelector('.error-summary-message').innerText = 'Sorry, we are currently unable to submit your request. Please try again later.';

            that.showErrorSummary();
        });
    },

    removeErrorMessages: function () {
        const errorSummary = document.getElementById('error-summary');
        errorSummary.querySelector('.error-summary-message').innerHTML = '';

        [].slice.call(errorSummary.querySelectorAll('ul')).forEach(ul => ul.parentNode.removeChild(ul));

        errorSummary.classList.add('hidden');

        [].slice.call(document.querySelectorAll('.ds_question--error')).forEach(question => question.classList.remove('ds_question--error'));
        [].slice.call(document.querySelectorAll('.ds_input--error')).forEach(input => input.classList.remove('ds_input--error'));
        [].slice.call(document.querySelectorAll('.ds_question__message')).forEach(message => message.classList.add('hidden'));
    },

    showErrorSummary: function () {
        const errorSummary = document.getElementById('error-summary');

        errorSummary.classList.remove('hidden');
        errorSummary.scrollIntoView();
        errorSummary.classList.add('flashable--flash');
        window.setTimeout(function () {
            errorSummary.classList.remove('flashable--flash');
        }, 200);
    }
};

// auto-initialize
Payment.init();

export default Payment;

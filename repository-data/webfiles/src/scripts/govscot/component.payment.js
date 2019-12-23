/**
 * Payment component
 */

/* global document, window */

'use strict';

import $ from 'jquery';
import CharacterCount from '../design-system-forms/character-count';

const characterCountModules = [].slice.call(document.querySelectorAll('[data-module="ds-character-count"]'));
const characterCountElements = [].slice.call(document.querySelectorAll('input[maxlength], textarea[maxlength]'));
characterCountElements.forEach(element => characterCountModules.push(element.parentNode));
characterCountModules.forEach(characterCount => new CharacterCount(characterCount).init());

const Payment = {
    settings: {
        paymentUrl: '/service/payment/'
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
                description: $('#description').val()
            };

            that.removeErrorMessages();

            if (that.validateInputs()) {
                console.log('ok to send')
                that.sendPayment(payment);
            }
        });
    },

    validateInputs: function () {
        const errors = [];
        const orderCodeInput = document.getElementById('orderCode');
        const errorSummary = document.getElementById('error-summary');

        // reference number restrictions: 64 characters, no spaces
        if (orderCodeInput.value.length > 64) {
            errors.push('Payment Reference is too long (maximum is 64 characters)');

            orderCodeInput.parentNode.classList.add('ds_question--error');
            orderCodeInput.classList.add('ds_input--error');
        }

        if (orderCodeInput.value.indexOf(' ') > -1) {
            errors.push('Payment Reference cannot contain spaces');

            orderCodeInput.parentNode.classList.add('ds_question--error');
            orderCodeInput.classList.add('ds_input--error');

            const spacesMessage = orderCodeInput.parentNode.querySelector('#payment-ref-spaces');
            spacesMessage.classList.remove('hidden');
        }

        if (errors.length) {
            const errorList = document.createElement('ul');

            errors.forEach(function (error) {
                const errorItem = document.createElement('li');
                errorItem.innerText = error;

                errorList.appendChild(errorItem);
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
        errorSummary.classList.add('hidden');
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

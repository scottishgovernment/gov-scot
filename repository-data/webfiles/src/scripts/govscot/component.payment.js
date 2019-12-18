/**
 * Payment component
 */

/* global document, window */

'use strict';

import $ from 'jquery';

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

            that.sendPayment(payment);
        });
    },

    sendPayment: function (payment) {
        $.ajax({
            type: 'POST',
            url: this.settings.paymentUrl,
            data: JSON.stringify(payment),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json'
        }).done(function(data) {
            window.location.href = data.paymentUrl;
        }).fail(function () {
            $('#error-summary-fail .error-summary-message').text('Sorry, we are currently unable to submit your request. Please try again later.');
            $('#error-summary-fail').removeClass('hidden');
            $('#error-summary-fail').get(0).scrollIntoView();
            $('#error-summary-fail').addClass('flashable--flash');
            window.setTimeout(function () {
                $('#error-summary-fail').removeClass('flashable--flash');
            }, 200);

        });
    },

    removeErrorMessages: function () {
        $('.input-group--has-error')
            .removeClass('input-group--has-error')
            .find('.message').remove();
    }
};

// auto-initialize
Payment.init();

export default Payment;

/**
 * Payment component
 */

/* global document, window */

'use strict';

import $ from 'jquery';

const Payment = {
    settings: {
        paymentUrl: 'http://localhost:9095/payment/'
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
        const that = this;

        $.ajax({
            type: 'POST',
            url: that.settings.paymentUrl,
            data: JSON.stringify(payment),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json'
        }).then(function(data) {
            // go to the response url
            window.location.href = data.paymentUrl;

        }, function(err, data) {
            $('#error').text(err.responseJSON.error);
            $('#error').removeClass('hidden');
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

/**
 * Feedback component
 */

/* global document, window */

'use strict';

import $ from 'jquery';

const Feedback = {
    settings: {
        feedbackUrl: '/service/feedback/',
        badServer: 'I\'m sorry, we have a problem at our side, please try again later'
    },

    init: function () {
        this.attachEventHandlers();
    },

    attachEventHandlers: function () {
        const that = this;

        // show relevant fields
        $('.js-feedback-type').on('change', function (event) {
            // remove errors
            that.removeErrorMessages();

            // set feedback type
            that.feedbackType = event.target.value;

            // hide all fields
            $('.feedback__field').addClass('fully-hidden');
            $('.feedback__field').attr('disabled', true);

            // show relevant fields
            $('.feedback__field--' + that.feedbackType).removeClass('fully-hidden');
            $('.feedback__field--' + that.feedbackType).removeAttr('disabled');

            // clear dropdowns
            $('.feedback__select').each(function () {
                $(this).find('option:eq(0)').prop('selected', true);
            });

            // hacky fix for IE8 not creating space on the page for the fields we're showing
            $('#feedback').css('visibility', 'hidden');
            $('#feedback').css('visibility', 'visible');
        });

        // validate on form submission
        $('#feedback-form').on('submit', function (event) {
            event.preventDefault();

            // remove errors
            that.removeErrorMessages();

            // gather feedback
            const feedback = {
                slug: document.location.pathname,
                type: that.feedbackType,
                freetext: $('#feedback-comment').val(),
                category: $('#page-category').val(),
                errors: []
            };

            // there will not always be a reason dropdown
            const reasonDropdown = $('.feedback__select:not(.hidden)');

            if(reasonDropdown.length > 0) {
                feedback.reason = reasonDropdown.val() || '';
            }

            feedback.hippoContentItem = window.location.pathname;
            feedback.contentItem = document.getElementById('documentUuid').value;

            // validate
            if (that.validateFeedback()) {
                // send
                that.sendFeedback(feedback);
            }
        });
    },

    validateFeedback: function () {
        let isValid = true;

        const requiredFields = $('.required--' + this.feedbackType);

        for (let i = 0, il = requiredFields.length; i < il; i++) {
            const thisField = $(requiredFields[i]);
            const idString = 'error-' + parseInt(Math.random() * 1000000);
            thisField.attr('aria-describedby', idString);

            if (thisField.val() === '' || thisField.val() === null) {
                this.addError(thisField.data('message'), thisField.closest('.input-group'), idString);
                isValid = false;
            }
        }

        return isValid;
    },

    sendFeedback: function (feedback) {
        const that = this;

        $.ajax({
            type: 'POST',
            url: that.settings.feedbackUrl,
            data: JSON.stringify(feedback),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json'
        }).then(function() {
            // hide form
            $('#feedback-form').addClass('hidden');

            // show thanks
            $('.feedback__thanks').removeClass('hidden');

            // Log the event to analytics.
            window.dataLayer = window.dataLayer || [];
            window.dataLayer.push({
                'type' : feedback.type,
                'reason' : feedback.reason,
                'event' : 'feedbackSubmit'
            });
        }, function(err) {
            const submit = $('#feedback-form').find('[type=submit]');

            that.addError(
                that.settings.badServer,
                submit.parent()
            );
        });
    },

    removeErrorMessages: function () {
        $('.input-group--has-error').find('.message').remove();
        $('.input-group--has-error').find('[aria-describedby]').removeAttr('aria-describedby');
        $('.input-group--has-error').removeClass('input-group--has-error');
    },

    addError: function (message, inputGroup, errorId) {
        let errorContainer = inputGroup.find('.message');
        if (errorContainer.length === 0) {
            errorContainer = $(`<div id="${errorId}" class="message"></div>`);
            errorContainer.prependTo(inputGroup);
        }

        errorContainer.text(message);
        inputGroup.addClass('input-group--has-error');
    }
};

// auto-initialize
Feedback.init();

export default Feedback;

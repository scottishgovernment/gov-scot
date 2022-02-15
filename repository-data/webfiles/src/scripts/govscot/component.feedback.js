/**
 * Feedback component
 */

//feedback.js
/*
 Contains functionality for on page feedback
 */

 'use strict';

 const feedbackForm = {
     init: function () {
         this.formElement = document.getElementById('feedbackForm');
         this.errorSummary = document.getElementById('feedbackErrorSummary');

         this.fields = {
             yes: {
                 comments: document.querySelector('#comments-yes')
             },
             no: {
                 reason: document.querySelector('#reason-no'),
                 comments: document.querySelector('#comments-no')
             },
             yesbut: {
                 reason: document.querySelector('#reason-yesbut'),
                 comments: document.querySelector('#comments-yesbut')
             }
         };

         if (this.formElement) {
             this.attachEventHandlers();
             this.addTracking();
         }
     },

     addTracking: function () {
         // additional tracking for radio buttons
         const radios = [].slice.call(this.formElement.querySelectorAll('.ds_radio__input'));
         radios.forEach(radio => {
             radio.addEventListener('change', () => {
                 // update datalayer
                 const type = document.querySelector('[name=feedbacktype]:checked').value;
                 this.updateDataLayer(type, 'feedbackRadio');
             });
         });

         const selects = [].slice.call(this.formElement.querySelectorAll('.ds_select'));
         selects.forEach(select => {
             select.addEventListener('change', () => {
                 const type = document.querySelector('[name=feedbacktype]:checked').value;
                 this.updateDataLayer(type, 'feedbackSelect');
             });
         });
     },

     attachEventHandlers: function () {
         this.formElement.addEventListener('submit', (event) => {
             event.preventDefault();

             if (document.querySelector('[name=feedbacktype]:checked')) {
                 this.submitFeedback();
             }
         });
     },

     submitFeedback: function () {
         if (!document.querySelector('[name=feedbacktype]:checked')) {
             return;
         }

         // remove any error messages
         this.removeErrorMessages();

         const type = document.querySelector('[name=feedbacktype]:checked').value;

         const feedback = {
             slug: document.location.pathname,
             type: type,
             reason: this.getFeedbackReason(type),
             freetext: this.getFeedbackFreeText(type),
             category: document.querySelector('#page-category').value,
             errors: [],
             hippoContentItem: window.location.pathname,
             contentItem: document.getElementById('documentUuid').value
         };

         const errorSummaryEl = document.getElementById('feedbackErrorSummary');
         const errorSummaryContentEl = errorSummaryEl.querySelector('.ds_error-summary__content');
         errorSummaryContentEl.innerHTML = '';

         if (!this.validateFeedback(feedback)) {
             //error

             const errorSummaryContentList = document.createElement('ul');
             errorSummaryContentList.classList.add('ds_error-summary__list');
             errorSummaryContentEl.appendChild(errorSummaryContentList);

             feedback.errors.forEach(function (error) {
                 const fieldElement = error.field.nodeName === 'SELECT' ? error.field.parentNode : error.field;
                 fieldElement.classList.add('ds_input--error');

                 const questionElement = error.field.closest('.ds_question');
                 questionElement.classList.add('ds_question--error');

                 let messageElement = questionElement.querySelector('.ds_question__error-message');
                 if (!messageElement) {
                     messageElement = document.createElement('p');
                     messageElement.classList.add('ds_question__error-message');
                 }

                 messageElement.innerHTML = error.message;

                 fieldElement.insertAdjacentElement('beforebegin', messageElement);

                 // summary item
                 const summaryItem = document.createElement('li');
                 const summaryLink = document.createElement('a');
                 summaryItem.appendChild(summaryLink);
                 errorSummaryContentList.appendChild(summaryItem);

                 summaryLink.innerText = `${error.message}`;
                 summaryLink.href = `#${error.field.id}`;
             });

             // error summary
             errorSummaryEl.classList.remove('fully-hidden');

             // scroll to error summary error
             errorSummaryEl.focus();
             errorSummaryEl.scrollIntoView();
         } else {
             errorSummaryEl.classList.add('fully-hidden');

             // update datalayer
             this.updateDataLayer(feedback.type, 'feedbackSubmit');

             // submit feedback
             var xhr = new XMLHttpRequest();
             xhr.open('POST', '/service/feedback/', true);

             //Send the proper header information along with the request
             xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');

             const that = this;

             xhr.onreadystatechange = function () {
                 // Call a function when the state changes.
                 if (this.readyState === XMLHttpRequest.DONE && this.status === 201) {
                     // show success message
                     document.getElementById('feedbackThanks').classList.remove('fully-hidden');

                     // hide form
                     that.formElement.classList.add('fully-hidden');

                 } else if (this.status >= 400) {
                     that.errorSummary.querySelector('.ds_error-summary__content').innerHTML = '<p>Sorry, we have a problem at our side. Please try again later.</p>';
                     that.errorSummary.classList.remove('fully-hidden');
                     that.errorSummary.scrollIntoView();
                 }
             };

             xhr.send(JSON.stringify(feedback));
         }
     },

     updateDataLayer: function (type, event) {
         // update datalayer
         window.dataLayer = window.dataLayer || [];
         window.dataLayer.push({
             'type': type,
             'reason': this.getFeedbackReason(type),
             'event': event
         });
     },

     getFeedbackReason: function (type) {
         let reason = '';

         if (type === 'no') {
             reason = this.fields.no.reason.value;
         } else if (type === 'yesbut') {
             reason = this.fields.yesbut.reason.value;
         } else {
             reason = '';
         }

         return reason;
     },

     getFeedbackFreeText: function (type) {
         let freeText = '';

         if (type === 'no') {
             freeText = this.fields.no.comments.value;
         } else if (type === 'yesbut') {
             freeText = this.fields.yesbut.comments.value;
         } else {
             freeText = this.fields.yes.comments.value || '';
         }

         return freeText;
     },

     removeErrorMessages: function () {
         this.errorSummary.classList.add('fully-hidden');

         [].slice.call(this.formElement.querySelectorAll('.ds_input--error')).forEach(function (inputElement) {
             inputElement.classList.remove('ds_input--error');
         });

         [].slice.call(this.formElement.querySelectorAll('.ds_question--error')).forEach(function (inputElement) {
             inputElement.classList.remove('ds_question--error');
         });

         [].slice.call(this.formElement.querySelectorAll('.ds_question__error-message')).forEach(function (message) {
             message.parentNode.removeChild(message);
         });
     },

     validateFeedback: function (feedback) {
         switch (feedback.type) {
         case 'no':
             if (feedback.freetext === '') {
                 feedback.errors.push({
                     field: this.fields.no.comments,
                     message: 'Please enter a comment'
                 });
             }

             if (feedback.reason === '') {
                 feedback.errors.push({
                     field: this.fields.no.reason,
                     message: 'Please select a reason'
                 });
             }
             break;

         case 'yesbut':
            if (feedback.reason === '') {
                feedback.errors.push({
                    field: this.fields.yesbut.reason,
                    message: 'Please select a reason'
                });
            }

             if (!feedback.freetext) {
                 feedback.errors.push({
                     field: this.fields.yesbut.comments,
                     message: 'Please enter a comment'
                 });
             }
             break;

         default:
             break;
         }

         return feedback.errors.length === 0;
     }
 };

 export default feedbackForm;

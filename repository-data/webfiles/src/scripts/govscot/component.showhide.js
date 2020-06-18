/**
 * Display toggle component
 *
 * Toggles the display of a target element
 */

'use strict';

export default {
    init: function () {
        document.querySelector('.js-content-wrapper').addEventListener('click', function (event) {
            event.preventDefault();

            if (event.target.classList.contains('js-trigger') && event.target.closest('.js-show-hide')) {

                const trigger = event.target;
                const target = document.querySelector(trigger.getAttribute('href'));

                if (trigger.getAttribute('aria-expanded') === 'false') {
                    target.classList.remove('hidden');
                    trigger.setAttribute('aria-expanded', true);
                } else {
                    target.classList.add('hidden');
                    trigger.setAttribute('aria-expanded', false);
                }

                const tempText = trigger.innerText;
                trigger.innerText = trigger.dataset.toggledText;
                trigger.dataset.toggledText = tempText;
            }
        });
    }
};

/**
 * Display toggle component
 *
 * Toggles the display of a target element
 */

'use strict';

export default {
    init: function () {
        const showHides = [].slice.call(document.querySelectorAll('.js-show-hide'));

        showHides.forEach(function (showHide) {
            const trigger = showHide.querySelector('.js-trigger');

            const target = document.querySelector(trigger.getAttribute('href'));

            trigger.addEventListener('click', function (event) {
                event.preventDefault();

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
            });
        });
    }
};

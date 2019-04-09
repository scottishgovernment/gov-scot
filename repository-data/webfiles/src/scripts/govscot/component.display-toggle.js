/**
 * Display toggle component
 *
 * Toggles the display of a target element
 */

'use strict';

export default {
    init: function () {
        document.addEventListener('click', function (event) {
            if (event.target.classList.contains('js-display-toggle')) {
                console.log(event);
                event.preventDefault();

                const elementToShow = document.getElementById(event.target.href.substring(event.target.href.indexOf('/#') + 2));

                elementToShow.classList.add('display-toggle--shown');
                event.target.classList.add('hidden');
            }
        });
    }
};

// COOKIE COMPONENT

/* global window */

'use strict';

import cookie from './cookie';

function triggerEvent(element, eventData) {
    let event;

    if (window.CustomEvent) {
        event = new CustomEvent('my-event', {detail: eventData});
    } else {
        event = document.createEvent('CustomEvent');
        event.initCustomEvent('my-event', true, true, eventData);
    }

    element.dispatchEvent(event);
}

export default {
    init: function () {
        const notice = document.getElementById('cookie-notice');

        if (!notice) {
            return;
        }

        // check whether we need to display the cookie notice
        if (cookie('cookie-notification-acknowledged')) {
            notice.classList.add('hidden');
        } else {
            notice.classList.remove('hidden');
        }

        notice.addEventListener('click', function (event) {
            event.preventDefault();

            const clickedElement = event.target;

            if(clickedElement.classList.contains('notification__close')) {
                event.preventDefault();

                notice.classList.add('hidden');
                cookie('cookie-notification-acknowledged', 'yes', 365);

                triggerEvent(window, 'headerautofixing');
                triggerEvent(window, 'positionnav');
            }
        });
    }
};

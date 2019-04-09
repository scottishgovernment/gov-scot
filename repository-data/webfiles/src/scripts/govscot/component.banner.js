// BANNER COMPONENT

'use strict';

const banner = {
    init: function (id) {
        const notice = document.getElementById(id);

        if (!notice) {
            return;
        }

        let bannerClosed = JSON.parse(sessionStorage.getItem(id + '-closed'));
        if (!bannerClosed) {
            notice.querySelectorAll('.notification__close').classList.remove('notification__close--minimised');
            notice.querySelectorAll('.notification__extra-content').classList.remove('hidden-xsmall');
        }

        notice.addEventListener('click', function (event) {
            event.preventDefault();

            const clickedElement = event.target;

            if(clickedElement.classList.contains('notification__close')) {
                notice.querySelectorAll('.notification__close').classList.toggle('notification__close--minimised');
                notice.querySelectorAll('.notification__extra-content').classList.toggle('hidden-xsmall');

                bannerClosed = !bannerClosed;
                sessionStorage.setItem(id + '-closed', JSON.stringify(bannerClosed));

                triggerEvent(window, 'headerautofixing');
                triggerEvent(window, 'positionnav');
            }
        });
    }
};

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

export default banner;

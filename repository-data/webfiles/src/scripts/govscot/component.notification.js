'use strict';

import storage from '../../../node_modules/@scottish-government/pattern-library/src/base/tools/storage/storage';

class Notification {
    constructor (notification, closeCookie = false, closeCookieDuration = 30) {
        this.notification = notification;
        this.notificationClose = notification.querySelector('.js-close-notification');
        this.closeCookie = closeCookie;
        this.closeCookieDuration = closeCookieDuration;
    }

    init() {
        if (!storage.getCookie(this.notification.id)) {
            this.notification.classList.remove('visually-hidden');
        }

        if (this.notificationClose) {
            this.notificationClose.addEventListener('click', () => {
                this.notification.parentNode.removeChild(this.notification);

                if (this.closeCookie) {
                    storage.setCookie('preferences', this.notification.id, 'true', this.closeCookieDuration);
                }
            });
        }
    }
}

export default Notification;

'use strict';

import cookie from './cookie';

class Notification {
    constructor (notification) {
        this.notification = notification;
        this.notificationClose = notification.querySelector('.js-close-notification');
    }

    init() {
        if (!cookie('importantNotice')) {
            this.notification.classList.remove('hidden');
        }

        if (this.notificationClose) {
            this.notificationClose.style.display = 'block';

            this.notificationClose.addEventListener('click', () => {
                this.notification.parentNode.removeChild(this.notification);
                cookie('importantNotice', 'true', 1);
            });
        }
    }
}

export default Notification;

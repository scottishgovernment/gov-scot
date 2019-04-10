// BANNER COMPONENT

'use strict';

import $ from 'jquery';

const banner = {
    init: function (id) {
        const notice = $('#' + id);

        let bannerClosed = JSON.parse(sessionStorage.getItem(id + '-closed'));
        if (!bannerClosed) {
            notice.find('.notification__close').removeClass('notification__close--minimised');
            notice.find('.notification__extra-content').removeClass('hidden-xsmall');
        }

        notice.on('click', '.notification__close', function(event) {
            event.preventDefault();

            notice.find('.notification__close').toggleClass('notification__close--minimised');
            notice.find('.notification__extra-content').toggleClass('hidden-xsmall');

            bannerClosed = !bannerClosed;
            sessionStorage.setItem(id + '-closed', JSON.stringify(bannerClosed));

            $(window).trigger('headerautofixing');
            $(window).trigger('positionnav');
        });
    }
};

export default banner;

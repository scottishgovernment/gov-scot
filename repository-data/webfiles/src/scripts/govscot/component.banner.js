define([], function () {
    'use strict';

    return {
        init: function (id) {
            var that = this;
            var notice = $('#' + id);

            var bannerClosed = JSON.parse(sessionStorage.getItem(id + '-closed'));
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

                that.headerAutofixing();

                $(window).trigger('positionnav');
            });
        }
    };
});

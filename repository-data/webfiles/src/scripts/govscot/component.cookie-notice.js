define([
    './cookie'
], function (cookie) {
    'use strict';

    return {
        init: function (ppp) {
            let notice = $('#cookie-notice');

            // check whether we need to display the cookie notice
            if (cookie('cookie-notification-acknowledged')) {
                notice.addClass('hidden');
            } else {
                notice.removeClass('hidden');
            }
    
            // When clicked, hide notice and set cookie for a year
            notice.on('click', '.notification__close', function(event) {
                event.preventDefault();
    
                notice.addClass('hidden');
                cookie('cookie-notification-acknowledged', 'yes', 365);
    
                $(window).trigger('positionnav');
                $(window).trigger('headerautofixing');
            });
        }
    };
});

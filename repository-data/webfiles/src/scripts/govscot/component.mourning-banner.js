define([], function () {
    'use strict';

    return {
        init: function () {
            $.ajax({
                type: 'GET',
                url: '/banners.json'
            })
            .done(function(data) {
                if (data.mourning.display === true) {
                    var bannerHtml = '' +
                        '<div id="mourning-banner" class="notification notification--mourning">' +
                        '    <div class="wrapper">' +
                        '        <div class="notification__main-content">' +
                        '        </div>' +
                        '    </div>' +
                        '</div>';

                    $('.notification-wrapper').append(bannerHtml);
                    if ($('#beta-banner').length > 0) {
                        $('#beta-banner').switchClass('notification--gold', 'notification--light');
                    }
                }
            });
        },
    };
});

define([
    'jquery.dotdotdot'
], function () {
    'use strict';

    var TextTruncate = function() {
        // set heights of all truncateable elements
        $('.js-truncate').each(function (key, value) {
            var lines = 2,
                itemToTruncate = $(value),
                lineHeight = parseInt(itemToTruncate.css('line-height'), 10);

            if (itemToTruncate.data('lines')) {
                lines = parseInt(itemToTruncate.data('lines'), 10);
            }

            itemToTruncate.css({
                maxHeight: lineHeight * lines
            });

            itemToTruncate.dotdotdot({
                watch: 'window'
            });
        });
    };

    return TextTruncate;
});

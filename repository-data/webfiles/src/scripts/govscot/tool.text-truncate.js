// TEXT TRUNCATE

'use strict';

import 'jquery.dotdotdot';
import $ from 'jquery';

const TextTruncate = function() {
    // set heights of all truncateable elements
    $('.js-truncate').each(function (key, value) {
        const itemToTruncate = $(value),
            lineHeight = parseInt(itemToTruncate.css('line-height'), 10);
        let lines = 2;

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

export default TextTruncate;

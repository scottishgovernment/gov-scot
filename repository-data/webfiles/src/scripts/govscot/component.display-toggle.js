/**
 * Display toggle component
 *
 * Toggles the display of a target element
 */

'use strict';

import $ from 'jquery';

export default {
    init: function () {
        $('.js-display-toggle').on('click', function (event) {
            event.preventDefault();
            const target = $($(this).attr('href'));
            target.addClass('display-toggle--shown');
            $(this).addClass('hidden');
        });
    }
};

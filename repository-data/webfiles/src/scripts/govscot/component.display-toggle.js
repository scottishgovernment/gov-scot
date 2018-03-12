/**
 * Display toggle component
 *
 * Toggles the display of a target element
 */

define([
    'jquery'
], function ($) {
    'use strict';

    $('.js-display-toggle').on('click', function (event) {
        event.preventDefault();
        var target = $($(this).attr('href'));
        target.addClass('display-toggle--shown');
        $(this).addClass('hidden');
    });
});

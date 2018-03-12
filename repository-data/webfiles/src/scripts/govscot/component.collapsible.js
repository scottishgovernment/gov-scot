define([
    'jquery'
], function ($) {
    'use strict';

    /**
     * Collapsible panels, e.g. accordion
     * Manipulates classes only, does not perform any transitions on its own
     */
    var Collapsible = {
        init: function () {
            $('.collapsible').on('click', '.js-collapsible-trigger', function (event) {
                event.preventDefault();

                $(this).closest('.collapsible').toggleClass('collapsible--open');
            });
        }
    };

    // auto-initialize
    Collapsible.init();

    return Collapsible;
});

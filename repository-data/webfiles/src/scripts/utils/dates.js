/**
 * Utility functions for formatting dates.
 */
define([],  function () {
    'use strict';

    var dates = {
        /**
         * Translates a date from dd/mm/yyyy to mm/dd/yyyy
         * @param input
         * @returns {*}
         */
        translateDate: function(input) {
            if (input !== '') {
                var tempDate = input.split('/');

                return tempDate[1] + '/' + tempDate[0] + '/' + tempDate[2];
            } else {
                return false;
            }
        }
    };

    return dates;
});

/**
 * Utility functions for formatting dates.
 */
define([],  function () {
    'use strict';

    var dates = {

        /**
         * prepends zeroes to a number, up to a set length
         * @param {number} value - number to prepend zeroes to
         * @param {number} length - desired length of number
         * @returns {string}
         */
        leadingZeroes: function (value, length) {
            var ret = value.toString();

            while (ret.length < length) {
                ret = '0' + ret.toString();
            }

            return ret;
        },

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

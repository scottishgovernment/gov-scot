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

        dateFormatMachine: function(rawDate) {
            // expected output format: yyyy-mm-dd
            if (typeof rawDate === 'object') {
                var date = new Date(rawDate);

                return date.getFullYear() + '-' +
                    this.leadingZeroes(date.getMonth() + 1, 2) + '-' +
                    this.leadingZeroes(date.getDate(), 2);
            } else if (typeof rawDate === 'string') {
                var dateFragments = rawDate.split('/');

                return (dateFragments[2] > 90 ? '19' : '20') + dateFragments[2] + '-' + dateFragments[1] + '-' + dateFragments[0];
            }
        },

        dateFormatHuman: function(rawDate) {
            // expected output format: dd/mm/yy
            if (typeof rawDate === 'object') {
                var date = new Date(rawDate);

                return this.leadingZeroes(date.getDate(), 2) + '/' +
                    this.leadingZeroes(date.getMonth() + 1, 2) + '/' +
                    date.getFullYear().toString().substring(2);
            } else if (typeof rawDate === 'string') {
                var dateFragments = rawDate.split('-');

                return dateFragments[2] + '/' + dateFragments[1] + '/' + dateFragments[0].substring(2);
            }
        },

        dateFormatPretty: function (machineDate) {
            var re = /^(\d{4})-(\d{2})-(\d{2})T?/,
                monthNameShort = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul',
                    'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
                matches = machineDate.match(re),
                year, month, day;
            if (matches) {
                year = matches[1];
                // Remove extraneous 0 at beginning
                month = parseInt(matches[2], 10) - 1 ;
                day = parseInt(matches[3],10);
                return day + ' ' + monthNameShort[month] + ' ' + year;
            }
            else {
                return machineDate;
            }
        },

        /**
         * Translates a date from dd/mm/yy to mm/dd/yyyy
         * @param input
         * @returns {*}
         */
        translateDate: function(input) {
            if (input !== '') {
                var tempDate = input.split('/');

                // we need to make these have a four-digit year for IE8 to parse them correctly
                var yearPrefix = tempDate[2] > 90 ? '19' : '20';

                return tempDate[1] + '/' + tempDate[0] + '/' + yearPrefix + tempDate[2];
            } else {
                return false;
            }
        },

        formatDateTime: function (inputDateTime, includeTime) {
            var date = new Date(inputDateTime);

            var output = this.dateFormatPretty(this.dateFormatMachine(date))

            if (includeTime) {
                output += ' ' + this.leadingZeroes(date.getHours(), 2) +
                    ':' + this.leadingZeroes(date.getMinutes(), 2);
            }

            return output;
        }
    };

    return dates;
});

/**
 * Utility to get or set a cookie
 *
 * Usage examples:
 * 1) Setting a cookie's value
 * utils.cookie('foo', 'bar', 7)
 *
 * 2) Getting a cookie's value
 * var cookieValue = utils.cookie('foo')
 *
 * @param {string} name - name of the cookie
 * @param {string} value - value of the cookie to set (undefined if getting)
 * @param {int} days - number of days til cookie expires (undefined if getting)
 * @returns {string|object} value of the cookie if getting, object of cookie data if setting
 */

'use strict';
define('cookie', [], function() {

  var cookie = function(name, value, days) {
      if (typeof value === 'undefined') {
          // with no value param, we are in read mode

          var nameEQ = name + '=',
              cookiesArray = document.cookie.split(';');

          // find a matching cookie
          for (var i = 0, il = cookiesArray.length; i < il; i++) {
              var cookie = cookiesArray[i];

              while (cookie.charAt(0) === ' ') {
                  cookie = cookie.substring(1, cookie.length);
              }

              if (cookie.indexOf(nameEQ) === 0) {
                  return cookie.substring(nameEQ.length, cookie.length);
              }
          }

          // return null if no matching cookie found
          return null;
      } else {
          // with a value param, we are in write mode

          var cookieData = {
              name: name,
              value: value
          };

          if (days) {
              var date = new Date();
              date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));

              cookieData.expires = date.toUTCString();
          }

          // build the string, as IE wants expiry parameter omitted if no expiry set
          var cookieString = name + '=' + value + '; ';
          if (cookieData.expires) {
              cookieString += 'expires=' + cookieData.expires + '; ';
          }
          cookieString += 'path=/';

          document.cookie = cookieString;

          // this variable is used in tests to verify that things are being set correctly
          return cookieData;
      }
  };

  return cookie;

});

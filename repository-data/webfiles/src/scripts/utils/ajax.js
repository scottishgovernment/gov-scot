//ajax.js
/*
 Contains functionality for the search page
 Fetches search results
 Puts results on the search results page
 */
'use strict';
define(['jquery'], function (jquery) {
    var ajax = {
        getJSON: function (url, callback) {
            jquery.getJSON(url)
                .success(function (res) {
                    callback(res);
                })
                .error(function () {
                    callback(false);
                });
        }
    };
    return ajax;
});

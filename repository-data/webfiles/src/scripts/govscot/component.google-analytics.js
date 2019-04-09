// GOOGLE ANALYTICS COMPONENT

/* global window */

'use strict';

import $ from 'jquery';

// sets the user type in the dataLayer
$.get('/service/usertype', function (data) {
    window.dataLayer = window.dataLayer || [];
    window.dataLayer[0].userType = data.userType;
});

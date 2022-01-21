// COOKIE PREFERENCES

'use strict';

import cookieForm from './cookie-form';

const cookiePreferences = {};

cookiePreferences.init = function() {
    cookieForm.init();
};

window.format = cookiePreferences;
window.format.init();

export default cookiePreferences;

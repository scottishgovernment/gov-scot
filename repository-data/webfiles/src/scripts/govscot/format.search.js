// SEARCH

/* global window */

'use strict';

import SearchWithFilters from './component.landing-filters';

window.dataLayer = window.dataLayer || [];

const filteredListPage = new SearchWithFilters ({
    filters: {
        date: false,
        topics: false
    }
});

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split('&');
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split('=');
        if (decodeURIComponent(pair[0]) == variable) {
            return decodeURIComponent(pair[1]);
        }
    }
    console.log('Query variable %s not found', variable);
}

window.format = filteredListPage;

window.format.redactPostcodesInDataLayer = function () {
    const query = getQueryVariable('q');
    if (query) {
        const nospacequery = query.replace(/\s/g, '');

        const postcoderegex = /^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$/i;

        window.dataLayer = window.dataLayer || {};
        if (postcoderegex.test(nospacequery) === true) {
            window.dataLayer.push({ 'query': '[postcode]' });
        } else {
            window.dataLayer.push({ 'site search': query });
        }
    }
};

window.format.init();
window.format.redactPostcodesInDataLayer();

export default filteredListPage;

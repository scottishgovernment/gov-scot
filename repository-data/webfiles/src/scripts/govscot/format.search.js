// SEARCH

/* global window */

'use strict';

import SearchWithFilters from './component.search-with-filters';

window.dataLayer = window.dataLayer || [];

const filteredListPage = new SearchWithFilters ({
    filters: {
        date: false,
        topics: false
    }
});

window.format = filteredListPage;

window.format.redactPostcodesInDataLayer = function () {
    const params = new URLSearchParams(window.location.search);
    const query = params.get('q');
    const nospacequery = query.replace(/\s/g, '');

    const postcoderegex = /^[A-Z]{1,2}[0-9][A-Z0-9]? ?[0-9][A-Z]{2}$/i;

    window.dataLayer = window.dataLayer || {};
    if (postcoderegex.test(nospacequery) === true){
        window.dataLayer.push({'query': '[postcode]'});
    } else {
        window.dataLayer.push({'site search': query});
    }
};

window.format.init();
window.format.redactPostcodesInDataLayer();

export default filteredListPage;

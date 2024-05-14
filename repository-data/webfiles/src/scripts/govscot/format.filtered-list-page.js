// FILTERED LIST PAGE FORMAT
// Contains functionality for the news landing page

/* global window */

'use strict';

import SearchWithFilters from './component.landing-filters';

window.dataLayer = window.dataLayer || [];

const filteredListPage = new SearchWithFilters ({
    filters: {
        date: true,
        topics: true
    }
});

window.format = filteredListPage;
window.format.init();

export default filteredListPage;

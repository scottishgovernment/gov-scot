// format.filtered-list-page.js
/*
 Contains functionality for the news landing page
 */

define([
    './component.search-with-filters'
], function (SearchWithFilters) {
    'use strict';

    window.dataLayer = window.dataLayer || [];

    const filteredListPage = new SearchWithFilters ({
        filters: {
            date: true,
            topics: true
        }
    }); 

    window.format = filteredListPage;
    window.format.init();

    return filteredListPage;
});

//news-landing-page.js
/*
 Contains functionality for the news landing page
 */

define([
    './search-with-filters'
], function (SearchWithFilters) {
    'use strict';

    window.dataLayer = window.dataLayer || [];

    const newsListPage = new SearchWithFilters ({
        filters: {
            date: true,
            topics: true
        }
    }); 

    window.format = newsListPage;
    window.format.init();

    return newsListPage;
});

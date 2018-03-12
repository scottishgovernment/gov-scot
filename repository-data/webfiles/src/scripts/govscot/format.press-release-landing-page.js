//news-landing-page.js
/*
 Contains functionality for the news landing page
 */

// define([
//     './search-with-filters',
//     'hbs!../templates/press-release-list-items'
// ], function (Search, newsListItemsTemplate) {

//     'use strict';

//     var newsListPage = new Search({
//         name: 'news',
//         searchUrl: '/service/search/news-and-policy',
//         resultsContainer: '#news-results',
//         resultTemplate: newsListItemsTemplate,
//         displayText: {
//             singular: 'news item',
//             plural: 'news items'
//         },
//         defaultSortField: 'pressReleaseDateTime',
//         sortWithTermField: '_score',
//         sortOrder: 'desc',
//         minDate: new Date($('#min-date').val()),
//         filters: {
//             date: true,
//             topics: true
//         },
//         dateTimeProperty: 'pressReleaseDateTime'
//     });

//     // queryModifierFunction is for any format-specific query logic
//     newsListPage.queryModifierFunction = function(params) {
//         params.type = 'press_release';

//         return params;
//     };

//     window.format = newsListPage;
//     window.format.init();

//     return newsListPage;
// });

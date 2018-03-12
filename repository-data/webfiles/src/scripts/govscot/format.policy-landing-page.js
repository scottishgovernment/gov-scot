//policy-landing-page.js
/*
 Contains functionality for the policy landing page
 Fetches policies
 Filters policies
 */

// define([
//     './search-with-filters',
//     'hbs!../templates/policy-list-items'
// ], function (Search, policyListItemsTemplate) {

//     'use strict';

//     var policyListPage = new Search({
//         name: 'policy',
//         searchUrl: '/service/search/news-and-policy',
//         resultTemplate: policyListItemsTemplate,
//         defaultSortField: 'title.sortable',
//         sortWithTermField: 'title.sortable',
//         sortOrder: 'asc',
//         displayText: {
//             singular: 'policy',
//             plural: 'policies'
//         },
//         filters: {
//             topics: true
//         }
//     });

//     // queryModifierFunction is for any format-specific query logic
//     policyListPage.queryModifierFunction = function(params) {
//         params.type = 'policy';

//         return params;
//     };

//     window.format = policyListPage;
//     window.format.init();

//     return policyListPage;
// });

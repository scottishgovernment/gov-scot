/*
 Contains functionality for the publications landing page
 Fetches publications
 Filters publications
 */

// define([
//     './search-with-filters',
//     'hbs!../templates/publications-list-items',
//     './component.publication-types'
// ], function (Search, publicationsListItemsTemplate, publicationTypes) {

//     'use strict';

//     function getMinDate() {

//         var minDateInMarkup = $('#min-date').val(),
//             minDate;

//         if (minDateInMarkup !== '') {
//             minDate = new Date(minDateInMarkup);
//         } else {
//             minDate = new Date(1999, 5, 1);
//         }

//         return minDate;
//     }

//     var publicationsListPage = new Search({
//         name: 'publications',
//         searchUrl: '/service/search/publication',
//         resultTemplate: publicationsListItemsTemplate,
//         displayText: {
//             singular: 'publication',
//             plural: 'publications'
//         },
//         defaultSortField: 'filterDate',
//         sortWithTermField: '_score',
//         sortOrder: 'desc',
//         minDate: getMinDate(),
//         maxDate: new Date(),
//         filters: {
//             date: true,
//             topics: true,
//             publicationTypes: true,
//             modified: true
//         }
//     });

//     // queryModifierFunction is for any format-specific query logic
//     publicationsListPage.queryModifierFunction = function(params) {

//         // map the type names to make looking them up easier
//         var typesParamLookup = {};
//         if (params.publicationTypes) {
//           params.publicationTypes.forEach(function (param) {
//             typesParamLookup[param] = true;
//           });
//         }

//         // filter to use to find selected aps publications
//         params.apsPublicationTypes =
//             publicationTypes.apsPublicationTypes(params.publicationTypes, typesParamLookup);

//         // filter used to find non-aps publications
//         params.types =
//             publicationTypes.formatsToInclude(params.publicationTypes, typesParamLookup);

//         return params;
//     };

//     window.format = publicationsListPage;
//     window.format.init();

//     return publicationsListPage;
// });

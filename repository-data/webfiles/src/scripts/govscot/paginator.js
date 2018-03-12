/**
 * Paginator
 *
 * Displays markup for pagination. Events for changing pages are not handled here.
 */

// define('paginatorTemplates', [], function() {
//     var paginatorTemplates = {
//         full: '<div class="search-results__pagination search-results__pagination--full pagination">' +
//             '<ul class="pagination__list">' +
//             '{{#pages}}' +
//             '<li class="pagination__item {{#if isLink}}{{else}}ellipsis{{/if}}">' +
//             '{{#if isLink}}' +
//             '<button type="button" tabindex="0" class="pagination__page {{#if isCurrentPage}}pagination__page--active{{/if}}" data-gtm="{{../../callerName}}-p-{{dataName}}" data-analytics="{{analyticsValue}}" data-start="{{start}}">{{{displayText}}}</button>' +
//             '{{else}}' +
//             '<span class="pagination__page {{#if isCurrentPage}}pagination__page--active{{/if}} pagination__page--no-link">{{{displayText}}}</span>' +
//             '{{/if}}' +
//             '</li>' +
//             '{{/pages}}' +
//             '</ul>' +
//             '</div>' +
//             '{{#if canLoadMore}}' +
//             '<div class="search-results__pagination search-results__pagination--small pagination">' +
//             '<button data-start="{{nextStart}}" id="load-more" class=" button button--primary">Load more</button>' +
//             '</div>' +
//             '{{/if}}'
//     };

//     return paginatorTemplates;
// });

// define([
//     'paginatorTemplates',
//     'hbs/handlebars',
//     './pubsub'
// ], function (paginatorTemplates, Handlebars, pubsub) {
//     'use strict';

//     /**
//      * Constructor, builds a pagination object and inserts it into the current page
//      * @param {object} $container - jQuery object for the pagination container
//      * @param {number} pagePadding - number of pages to display either side of the current page
//      * @param {object} caller - object calling the paginator
//      *
//      * @returns {{getPages: getPages, renderPages: renderPages, init: init, setParams: setParams, getData: getData}}
//      * @constructor
//      */
//     var Paginator = function ($container, pagePadding, caller) {

//         var paginatorData = {};

//         var paginatorFunctions = {

//             /**
//              * Creates an array of pages to be passed to the template
//              * Array contains only pages within "pagePadding" tolerance
//              * Array includes ellipses and prev/next as appropriate
//              *
//              * @param currentPage
//              * @returns {Array} pages to display
//              */
//             getPages: function (currentPage) {
//                 var maxPadding = pagePadding * 2 + 1;

//                 var firstPage = Math.max(0, Math.min(currentPage - pagePadding, paginatorData.numberOfPages - maxPadding)),
//                     lastPage = Math.min(paginatorData.numberOfPages, Math.max(currentPage + pagePadding, maxPadding -1)),
//                     pages = [];

//                 // a little clunky, but walk through the pagination

//                 // check for 'prev' need
//                 if (currentPage > 0) {
//                     pages.push({
//                         isPrevious: true,
//                         displayText: 'Prev',
//                         start: (currentPage - 1) * paginatorData.itemsPerPage,
//                         isLink: true,
//                         analyticsValue: 'prev'
//                     });
//                 }

//                 // check for start ellipsis need
//                 if (firstPage > 0) {
//                     if (firstPage === 1) {
//                         pages.push(
//                             {
//                                 displayText: 1,
//                                 start: 0,
//                                 isLink: true,
//                                 analyticsValue: 1
//                             });
//                     } else {
//                         pages.push({
//                             displayText: '&hellip;',
//                             isLink: false
//                         });
//                     }
//                 }

//                 // loop through pages to pick out the relevant ones.
//                 for(var i = 0, il = paginatorData.numberOfPages; i < il; i++) {
//                     if (i >= firstPage && i <= lastPage) {
//                         var page = {
//                             displayText: i + 1,
//                             start: i * paginatorData.itemsPerPage,
//                             isLink: true,
//                             analyticsValue: i + 1
//                         };

//                         if (i === paginatorData.currentPage) {
//                             page.isLink = false;
//                             page.isCurrentPage = true;
//                         }

//                         pages.push(page);
//                     }
//                 }

//                 // check for end ellipsis need
//                 if (lastPage < paginatorData.numberOfPages-1) {
//                     if (lastPage === paginatorData.numberOfPages - 2) {
//                         pages.push(
//                             {
//                                 displayText: paginatorData.numberOfPages,
//                                 start: (paginatorData.numberOfPages - 1) * paginatorData.itemsPerPage,
//                                 isLink: true,
//                                 analyticsValue: i + 1
//                             });
//                     } else {
//                         pages.push({
//                             displayText: '&hellip;',
//                             isLink: false
//                         });
//                     }

//                 }

//                 // check for 'next' need
//                 if (currentPage + 1 < paginatorData.numberOfPages) {
//                     pages.push({
//                         displayText: 'Next',
//                         isNext: true,
//                         start: (currentPage + 1) * paginatorData.itemsPerPage,
//                         isLink: true,
//                         analyticsValue: 'next'
//                     });
//                 }

//                 // add a lowercase version of the displayText for use by data attributes
//                 for (var p = 0, pl = pages.length; p < pl; p++) {
//                     pages[p].dataName = ('' + pages[p].displayText).toLowerCase();
//                 }

//                 return pages;
//             },

//             /**
//              * Renders the pagination into a supplied container
//              */
//             renderPages: function() {
//                 var paginationTemplateSource = paginatorTemplates.full,
//                     paginationTemplate = Handlebars.compile(paginationTemplateSource);

//                 if (paginatorData.numberOfPages > 1) {
//                     var paginationData = {
//                         callerName: caller.settings.name,
//                         firstStart: 0,
//                         lastStart: paginatorData.numberOfPages * paginatorData.itemsPerPage,
//                         prevStart: (paginatorData.currentPage - 1) * paginatorData.itemsPerPage,
//                         nextStart: (paginatorData.currentPage + 1) * paginatorData.itemsPerPage,
//                         pages: this.getPages(paginatorData.currentPage)
//                     };

//                     if (paginationData.nextStart < paginatorData.hits) {
//                         paginationData.canLoadMore = true;
//                     }

//                     $container.html(paginationTemplate(paginationData));
//                 } else {
//                     $container.html('');
//                 }
//             },


//             /**
//              * Initialises the paginator, binding events to the links/buttons
//              */
//             init: function() {
//                 var that = this;

//                 $container.on('click', 'button, #load-more', function () {
//                     caller.searchParams.from = parseInt($(this).data('start'), 10);

//                     if (window.history.pushState && !$(this).is('#load-more')) {
//                         window.history.pushState(
//                             '',
//                             '',
//                             that.getNewUrlWithPage(caller.searchParams)
//                         );
//                     }

//                     var append = false;

//                     if ($(this).is('#load-more')) {
//                         append = true;
//                     }

//                     caller.doSearch(caller.searchParams, append);

//                     // analytics
//                     if ($(this).is('#load-more')) {
//                         pubsub.publish('analyticsEvent', [caller.settings.name + '-pagination', 'click', 'load more']);
//                     } else {
//                         pubsub.publish('analyticsEvent', [caller.settings.name + '-pagination', 'click', $(this).data('analytics')]);
//                     }

//                     // scroll to top
//                     if (!$(this).is('#load-more')) {
//                         window.scrollTo(0, 0);
//                     }
//                 });

//                 window.onpopstate = function () {
//                     if (typeof caller.updatePage !== 'undefined') {
//                         caller.updatePage();
//                     }
//                 };
//             },

//             getNewUrlWithPage: function (searchParams) {
//                 var pageNumber = parseInt(parseInt(searchParams.from / searchParams.size, 10) + 1, 10);
//                 var newUrlWithPage =  window.location.href.replace(/\&page=\d{1,3}/g, '').replace(/\?page=\d{1,3}/g, '');

//                 if (newUrlWithPage.indexOf('?') > 0) {
//                     newUrlWithPage += '&page=' + pageNumber;
//                 } else {
//                     newUrlWithPage += '?page=' + pageNumber;
//                 }

//                 return newUrlWithPage;
//             },

//             /**
//              * Sets/updates search parameters from the page which influence the pagination display
//              *
//              * @param {int} from
//              * @param {int} itemsPerPage
//              * @param {int} hits
//              */
//             setParams: function (from, itemsPerPage, hits) {
//                 paginatorData.itemsPerPage = itemsPerPage;
//                 paginatorData.hits = hits;

//                 // calculate current page
//                 paginatorData.currentPage = parseInt(from, 10) / parseInt(paginatorData.itemsPerPage, 10);

//                 // calculate number of pages
//                 paginatorData.numberOfPages = Math.ceil(paginatorData.hits / paginatorData.itemsPerPage);
//             },

//             /**
//              * useful for debugging/test
//              */
//             getData: function () {
//                 return paginatorData;
//             }
//         };

//         // initialise the paginator
//         paginatorFunctions.init();

//         return paginatorFunctions;
//     };

//     return Paginator;
// });

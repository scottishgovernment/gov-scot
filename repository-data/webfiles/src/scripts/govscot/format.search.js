//search.js
/*
 Contains functionality for the search page
 Fetches search results
 Puts results on the results page
 */

// define([
//     '../utils/gup',
//     'hbs!../templates/search-results-items',
//     'hbs!../templates/did-you-mean',
//     './search-utils',
//     '../utils/dates',
//     './paginator',
//     './tool.text-truncate',
//     'jquery.dotdotdot'
// ], function (gup, searchResultsItemsTemplate, didYouMeanTemplate, searchUtils, dates, Paginator, TextTruncate) {

//     'use strict';

//     var search = {

//         //Globals
//         settings: {
//             name: 'search',
//             pageSize: 10,
//             pagePadding: 3,
//             searchUrl: '/service/search/site?q=',
//             resultsContainer: '#search-results',
//             resultsList: '#search-results-list',
//             paginationContainer: '#pagination',
//             notAvailable: 'Search is not available right now, please try again later',
//             responsiveWidthThreshold: 767
//         },

//         // Run when module first loads
//         init: function () {
//             this.doInitialSearch();
//             this.initPaginator();
//         },

//         initPaginator: function () {
//             var that = this;

//             var $container = $(that.settings.paginationContainer);

//             // create paginator
//             that.paginator = new Paginator($container, that.settings.pagePadding, that);
//         },

//         /**
//          * Updates the "showing n results" text
//          * @param {number} count
//          *
//          * @returns {boolean} success
//          */
//         updateResultCount: function (count) {
//             var countElement = $('.js-search-results-count');

//             if (countElement.length > 0 && typeof count === 'number') {
//                 countElement.html('Showing <b>' + count + '</b> ' + (count === 1 ? 'result' : 'results'));
//                 return true;
//             } else {
//                 return false;
//             }
//         },

//         doSearch: function (params, append) {

//             // add ajax loader
//             var elementsToHideWhileLoading = $('#result-count, #pagination, #search-results-list');
//             var ajaxLoader = $('<div id="ajax-loader">Loading results</div>');

//             if (!append) {
//                 elementsToHideWhileLoading.addClass('hidden');
//             }

//             ajaxLoader.insertAfter('#search-results-list');

//             var that = this;

//             that.fetchResults(params)
//                 .done(function (res) {
//                     that.paginator.setParams(params.from, params.size, res.hits.total);

//                     that.paginator.renderPages();

//                     // display any "did you mean" suggestions
//                     if (res.hasOwnProperty('suggest')) {
//                         var didYouMeanItems = that.getDidYouMean(res.suggest);

//                         if (didYouMeanItems.length > 0) {
//                             var html = didYouMeanTemplate({items: didYouMeanItems});
//                             $('.did-you-mean').remove();
//                             $('.search-box__form').parent().before(html);
//                         }
//                     }

//                     that.numberHits(res, params);

//                     //Put the results on screen
//                     that.populateResults(res, false, append);

//                     // update the list's start point
//                     $(that.settings.resultsList).attr('start', params.from + 1);

//                     that.updateResultCount(res.hits.total);
//                     //Set the text box text
//                     if ( $('body').hasClass('status')) {
//                         $('#search-box').val(window.location.pathname.replace(/\//g, ' '));
//                     } else {
//                         $('#search-box').val(decodeURIComponent(gup('q').replace(/\+/g, '%20')));
//                     }
//                 })
//                 .fail(function () {
//                     //Search not available
//                     $(that.settings.resultsContainer).html(that.settings.notAvailable);
//                 })
//                 .always(function () {
//                     // remove "loading" spinner
//                     ajaxLoader.remove();
//                     elementsToHideWhileLoading.removeClass('hidden');
//                 });
//         },

//         doInitialSearch: function () {

//             //Get initial search term
//             this.searchParams = {
//                 q: this.getQueryString('q') || '',
//                 size: this.getQueryString('size') || this.settings.pageSize,
//                 from: this.getQueryString('from') || 0
//             };

//             // parse a page querystring param
//             if (this.getQueryString('page') !== '') {
//                 this.searchParams.from = parseInt(this.getQueryString('page') - 1, 10) * this.searchParams.size;
//             }

//             search.doSearch(this.searchParams);
//         },

//         getQueryString: function (param, $window, $gup) {
//             gup = $gup || gup;
//             return gup(param, $window) || '';
//         },

//         /* Returns JSON data  */
//         fetchResults: function (params) {
//             //Build up the query string
//             var url = this.settings.searchUrl + params.q + '&from=' + params.from + '&size=' + params.size;
//             return jQuery.getJSON(url);
//         },

//         /*
//          Takes some data, mixes it with a template and returns the results
//          */
//         templatise: function (data, template) {
//             //Maybe a template wasn't passed in
//             searchResultsItemsTemplate = template || searchResultsItemsTemplate;

//             //Get the template and the data together
//             return searchResultsItemsTemplate(data);
//         },

//         numberHits : function (data, params) {
//             var hits = data.hits.hits;

//             for (var i = 0, il = hits.length; i < il; i++) {
//                 hits[i].globalIndex = params.from + i;
//             }
//         },

//         populateResults: function (data, $searchResultsItemsTemplate, append) {
//             var html = '';

//             for (var i = 0, il = data.hits.hits.length; i < il; i++) {
//                 var thisItem = data.hits.hits[i];

//                 if (thisItem._source) {
//                     if (i === 0) {
//                         thisItem._source.isFirst = true;
//                     }

//                     if (thisItem._source.label === 'role') {
//                         thisItem._source.isRole = true;

//                         // if this is a PERSON
//                         if (thisItem._source._embedded.format.name === 'person') {

//                             // if this is an unlisted tole then set its title to
//                             // the role title
//                             if (thisItem._source.roleType === 'Unlisted') {
//                                 thisItem.title = thisItem._source.roleDescription;
//                             }

//                             thisItem._source.incumbentName = thisItem._source.title;
//                         } else {

//                             // if this is a ROLE
//                             if (thisItem._source.label === 'role') {
//                                 thisItem._source.roleDescription = thisItem._source.title;
//                                 thisItem._source.incumbentName = thisItem._source.incumbentTitle ? thisItem._source.incumbentTitle : '';
//                             }
//                         }
//                     }

//                     if (thisItem._source.pressReleaseDateTime) {
//                         thisItem._source.filterDate = dates.formatDateTime(thisItem._source.pressReleaseDateTime, true);
//                     } else {
//                         thisItem._source.filterDate = dates.dateFormatPretty(thisItem._source.filterDate);
//                     }
//                 }

//                 html += this.templatise(thisItem, $searchResultsItemsTemplate);
//             }

//             if (append) {
//                 $(this.settings.resultsList).append(html);
//             } else {
//                 $(this.settings.resultsList).html(html);
//             }

//             // attach click handler for results...
//             $('#search-results-list > li > a').each(function (index) {
//                 $(this).click(function () {
//                     var pageData = search.paginator.getData();
//                     var absIndex =  (pageData.currentPage * pageData.itemsPerPage) + index;

//                     window.pubsub.publish('analyticsEvent', ['results', 'click', absIndex]);
//                 });
//             });

//             // let the stack clear
//             setTimeout(function () {TextTruncate();}, 0);
//         },

//         /**
//          *
//          * @param {object} suggest
//          */
//         getDidYouMean: function(suggest) {
//             // remove existing suggestions
//             $('#suggestions').remove();

//             // find the suggestion whose HIGHLIGHTED section is fully wrapped in EM tags
//             var suggestedOptions = suggest.didyoumean[0].options;

//             var suggestionsArray = [];

//             for (var i = 0, il = suggestedOptions.length; i < il; i++) {
//                 suggestionsArray.push(suggestedOptions[i]);
//             }

//             return suggestionsArray;
//         },

//         /**
//          * Called in popstate from the paginator
//          */
//         updatePage: function () {
//             var pageNumber = gup('page') || 1;

//             this.searchParams.from = (pageNumber - 1) * this.searchParams.size;

//             this.doSearch(this.searchParams, false);
//       }
//     };

//     window.search = search;

//     window.format = search;
//     window.format.init();

//     return search;

// });

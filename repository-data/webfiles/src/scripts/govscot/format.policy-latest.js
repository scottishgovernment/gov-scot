// define([
//     'jquery',
//     '../shared/component.page-group',
//     './component.display-toggle',
//     '../utils/dates',
//     'hbs!../templates/policy-latest'
// ], function($, pageGroup, displayToggle, dates, template) {

//     var page = {
//         init: function(){
//             pageGroup.init();
//             fetchLatestNews();
//         }
//     };

//     function fetchLatestNews() {
//         var policyTags = [];
//         var latest = [];

//         if ($('#encodedPolicyTags').length) {
//             policyTags = JSON.parse(
//                 decodeURIComponent($('#encodedPolicyTags').val()));
//         }

//         if ($('#encodedLatestItems').length) {
//             latest = JSON.parse(
//                 decodeURIComponent($('#encodedLatestItems').val()));
//         }

//         var query = {
//             'from': 0,
//             'size': 100,
//             'tags': policyTags
//         };

//         $.ajax({
//             type: 'POST',
//             url: '/service/search/news-by-tag?from=0&size=100',
//             dataType: 'json',
//             data: JSON.stringify(query),
//             contentType: 'application/json; charset=UTF-8'
//         })
//         .done(function(data) {
//             // add all hits to the latest items
//             data.hits.hits
//                 .map(function(hit) { return hit._source ;})
//                 .filter(function (source) {
//                     return source.pressReleaseDateTime !== undefined;
//                 })
//                 .forEach(function (source) {
//                     source.filterDate = source.pressReleaseDateTime;
//                     source.formatLabel = 'news';
//                     latest.push(source);
//                 });
//             renderLatest(latest);
//         })
//         .fail(function() {
//             // we failed to fetch the news, but render the publications etc. anyway
//             renderLatest(latest);
//         });
//     }

//     function renderLatest(items) {
//         items.sort(function (a, b) {
//             if (a.filterDate > b.filterDate) {
//                return -1;
//              }
//              if (a.filterDate < b.filterDate) {
//                return 1;
//              }
//              return 0;
//         });
//         items.forEach(function (item) {
//             var includeTime = item.formatLabel === 'news';
//             console.log('' + item.filterDate + ' ' + item.pressReleaseDateTime);
//             item.filterDate = dates.formatDateTime(item.filterDate, includeTime);
//         });
//         var html = template({latest : items});
//         $('#search-results-list').append(html);
//     }

//     window.format = page;
//     window.format.init();

//     return page;
// });

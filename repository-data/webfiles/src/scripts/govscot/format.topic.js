define([
    'jquery',
    './component.expandable',
    './tool.text-truncate',
    './component.publication-types',
    '../utils/dates'
], function($, expandable, TextTruncate, publicationTypes, dates) {

    var topicPage = {};

    topicPage.init = function() {
        TextTruncate();
        this.fetchLatestItems();
    };

    topicPage.fetchLatestItems = function () {
        var topic = $('#topicName').val();
        this.fetchLatestNewsItems(topic);
        this.fetchLatestPublications(topic);
        this.fetchLatestConsultations(topic);
    };

    topicPage.fetchLatestNewsItems = function (topic) {
        var that = this;
        var newsContainer = $('#news-container');

        var query = {
            'term': '',
            'from': 0,
            'size': 3,
            'topics': [topic],
            'type': 'press_release',
            'sortOrder': 'desc',
            'sortField': 'pressReleaseDateTime'
        };
        $.ajax({
            type: 'POST',
            url: '/service/search/news-and-policy?from=0&size=3',
            dataType: 'json',
            data: JSON.stringify(query),
            contentType: 'application/json; charset=UTF-8'
        })
        .done(function(data) {
            if (data.hits.hits.length === 0) {
                newsContainer.html('<p>There are no news items relating to this topic.</p>');
            } else {
                newsContainer.html('');

                for (var j = 0, jl = data.hits.hits.length; j < jl; j++) {
                    var hit = data.hits.hits[j];
                    newsContainer.append(that.elementForSource(hit._source, j));
                }
            }
        })
        .fail(function() {
            newsContainer.html('<p>Failed to get news items relating to this topic.</p>');
        });
    };

    topicPage.fetchLatestPublications = function (topic) {
        var that = this;
        var publicationsContainer = $('#publications-container');
        var apsPublicationTypes
            = publicationTypes.apsPublicationTypes()
                        .filter(function(type) {
                            return type !== 'Consultation Paper';
                        });

        var query = {
            'term': '',
            'from': 0,
            'size': 3,
            'topics': [topic],
            'publicationTypes': publicationTypes.formatsToInclude(),
            'apsPublicationTypes': apsPublicationTypes,
            'types': [],
            'sortOrder': 'desc',
            'sortField': 'filterDate'
        };
        $.ajax({
            type: 'POST',
            url: '/service/search/publication?from=0&size=3',
            dataType: 'json',
            data: JSON.stringify(query),
            contentType: 'application/json; charset=UTF-8'
        })
        .done(function(data) {
            if (data.hits.hits.length === 0) {
                publicationsContainer.html('<p>There are no publications relating to this topic.</p>');
            } else {
                publicationsContainer.html('');

                for (var j = 0, jl = data.hits.hits.length; j < jl; j++) {
                    var hit = data.hits.hits[j];
                    publicationsContainer.append(that.elementForSource(hit._source, j));
                }
            }
        })
        .fail(function() {
            publicationsContainer.html('<p>Failed to get publications relating to this topic.</p>');
        });
    };

    topicPage.fetchLatestConsultations = function (topic) {
        var that = this;
        var consultationsContainer = $('#consultations-container');

        var query = {
            'term': '',
            'from': 0,
            'size': 3,
            'topics': [topic],
            'publicationTypes': ['consultation_paper'],
            'apsPublicationTypes': ['Consultation Paper'],
            'types': [],
            'sortOrder': 'desc',
            'sortField': 'filterDate'
        };
        $.ajax({
            type: 'POST',
            url: '/service/search/publication?from=0&size=3',
            dataType: 'json',
            data: JSON.stringify(query),
            contentType: 'application/json; charset=UTF-8'
        })
        .done(function(data) {
            if (data.hits.hits.length === 0) {
                consultationsContainer.html('<p>There are no consultations relating to this topic.</p>');
            } else {
                consultationsContainer.html('');

                for (var j = 0, jl = data.hits.hits.length; j < jl; j++) {
                    var hit = data.hits.hits[j];
                    consultationsContainer.append(that.elementForSource(hit._source, j));
                }
            }
        })
        .fail(function() {
            publicationsContainer.html('<p>Failed to get consultations relating to this topic.</p>');
        });
    };

    topicPage.elementForSource = function (source, index) {
        var date;
        var includeTime = false;

        if (source.pressReleaseDateTime) {
            date = source.pressReleaseDateTime;
            includeTime = true;
        } else {
            date = source.filterDate;
        }

        date = dates.formatDateTime(date, includeTime);

        return '' +
            '<article class="homepage-publication">' +
            '  <h3 class="js-truncate  homepage-publication__title"><a href="' +
            source.url + '" ' +
            '    data-gtm="news-' + index + '" title="' +
            source.title + '">'+ source.title + '</a></h3>' +
            '  <p class="homepage-publication__date">' + date + '</p>' +
            '</article>';
    };

    window.format = topicPage;
    window.format.init();

    return topicPage;
});

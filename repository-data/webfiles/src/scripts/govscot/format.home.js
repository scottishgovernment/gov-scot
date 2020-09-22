// HOME FORMAT

/* global window */

'use strict';

import TextTruncate from './tool.text-truncate';
import $ from 'jquery';

window.dataLayer = window.dataLayer || [];

const homePage = {

    settings: {
        youTubePublicKey: 'AIzaSyDGmgKDuodqUe8Sb59iNu39OQfEUc7h0PM'
    },

    init: function () {
        this.attachEventHandlers();

        if (typeof IntersectionObserver === 'function') {
            this.observeYouTube();
            this.observeFlickr();
        } else {
            this.populateYouTube();
            this.populateFlickr();
        }

        TextTruncate();
    },

    attachAnalyticsEvents: function () {
        $('.js-topics').on('change', 'input[type=checkbox]', function () {
            window.dataLayer.push({
                'filter': 'topics',
                'interaction': this.checked ? 'check': 'uncheck',
                'value': this.value,
                'event': 'filters'
            });
        });

        $('.js-policy-form-submit').on('click', function () {
            window.dataLayer.push({
                'event': 'policies-go'
            });
        });
    },

    attachEventHandlers: function () {
        const that = this;

        const policySubmitLink = $('.js-policy-form-submit');

        // submit policy form on press of enter on keyword input
        $('#filters-search-term').on('keypress', function (event) {
            if (event.keyCode === 13) {
                event.preventDefault();

                that.submitPolicyForm(policySubmitLink.attr('href'));
            }
        });

        policySubmitLink.on('click', function (event) {
            event.preventDefault();

            that.submitPolicyForm($(this).attr('href'));
        });

        this.attachAnalyticsEvents();
    },

    submitPolicyForm: function (destinationUrl) {
        const queryStringParams = [],
            term = $('#filters-search-term').val(),
            topics = [];
        let queryString;

        $.each($('input[name="topics[]"]:checked'), function (index, checkbox) {
            topics.push(checkbox.value);
        });

        // build querystring
        if (term.length > 0) {
            queryStringParams.push('term=' + term);
        }
        queryStringParams.push('cat=filter');

        if (topics.length > 0) {
            queryStringParams.push('topics=' + topics.join(';'));
        }

        if (queryStringParams.length > 0) {
            queryString = '?' + queryStringParams.join('&');
        } else {
            queryString = window.location.pathname.substring(1);
        }

        // navigate to policy page
        this.navigateToUrl(destinationUrl + queryString);
    },

    navigateToUrl: function (url) {
        window.location.href = url;
    },

    observeFlickr: function () {
        const observer = new IntersectionObserver((entries, self) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.populateFlickr();
                    self.unobserve(entry.target);
                }
            });
        });

        observer.observe(document.getElementById('flickr'));
    },

    populateFlickr: function () {
        const data = {
                method: 'flickr.people.getPublicPhotos',
                api_key: '78be691bd8c8f9a67a6e8d448e974fd5',
                user_id: '26320652@N02',
                per_page: 4,
                format: 'json',
                nojsoncallback: 1
            },
            url = 'https://api.flickr.com/services/rest/';

        $.get(url, data, function (response) {
            $.each(response.photos.photo, function (key, value) {
                // url format is: https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
                const img = $('<img alt="' + value.title + '" class="image-grid__image" src="https://farm' + value.farm + '.staticflickr.com/' + value.server + '/' + value.id + '_' + value.secret + '_q.jpg" />');

                const link = $('<a href="https://www.flickr.com/photos/scottishgovernment/' + value.id + '"></a>');

                const listItem = $('<li class="grid__item image-grid__item six-twelfths"></li>');

                img.appendTo(link);
                link.appendTo(listItem);
                listItem.appendTo('#flickr-photos');
            });
        });
    },

    observeYouTube: function () {
        const observer = new IntersectionObserver((entries, self) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.populateYouTube();
                    self.unobserve(entry.target);
                }
            });
        });

        observer.observe(document.getElementById('youtube'));
    },

    populateYouTube: function () {
        const that = this;

        const data = {
                part: 'contentDetails',
                forUsername: 'scottishgovernment',
                key: this.settings.youTubePublicKey
            },
            url = 'https://www.googleapis.com/youtube/v3/channels';

        $.get(url, data)
            .done(function (result) {
                const uploadPlaylistId = result.items[0].contentDetails.relatedPlaylists.uploads;
                that.fetchLatestYouTubeVideos(uploadPlaylistId);
            });
    },

    fetchLatestYouTubeVideos: function (playlistId) {
        const data = {
                part: 'snippet',
                playlistId: playlistId,
                key: this.settings.youTubePublicKey,
                maxResults: 1
            },
            url = 'https://www.googleapis.com/youtube/v3/playlistItems';

        $.get(url, data, function (response) {
            $.each(response.items, function (key, value) {
                // using the medium thumbnail because it is not letterboxed
                const thumbnailUrl = value.snippet.thumbnails.medium.url;

                const img = $('<img alt="' + value.snippet.title + '" class="image-grid__image" src="' + thumbnailUrl + '" />');

                const link = $('<a class="image-grid__link youtube-link" href="https://www.youtube.com/watch?v=' + value.snippet.resourceId.videoId + '"></a>');

                const listItem = $('<li class="image-grid__item grid__item"></li>');

                img.appendTo(link);
                link.appendTo(listItem);
                listItem.appendTo('#youtube-videos');
            });
        });
    }
};

window.format = homePage;
window.format.init();

export default homePage;

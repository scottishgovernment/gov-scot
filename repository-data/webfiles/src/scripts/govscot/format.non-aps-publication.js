'use strict';

define([
    './component.display-toggle',
    './component.sticky-back-to-top',
    '../shared/component.sticky-document-info'
], function() {

    var publicationPage = {};

    publicationPage.init = function() {
        this.initExpandables();
        this.initSidebarHeight();
    };

    publicationPage.initExpandables = function () {
        $('.js-expand-downloads').on('click', function (event) {
            event.preventDefault();

            var target = $($(this).attr('href')),
                linkText = $(this).find('span.publication-info__preamble-expand');

            target.slideToggle(200);

            linkText.text() === 'More' ? linkText.text('Less') : linkText.text('More');
        });
    };

    publicationPage.initSidebarHeight = function () {
        var that = this;

        that.setSidebarHeight();
        $(window).on('resize', function() {
            that.setSidebarHeight();
        });
    };

    publicationPage.setSidebarHeight = function () {
        var publicationBody = $('.publication-info__body'),
            targetHeight = 0;

        if (window.innerWidth >= 768) {
            targetHeight = Math.min($('.js-content-wrapper').height(), window.innerHeight);
        }

        publicationBody.css({minHeight: targetHeight});
    };

    window.format = publicationPage;
    window.format.init();

    return publicationPage;
});

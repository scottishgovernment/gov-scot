/**
 * Expandable component
 */

define([
    'jquery'
], function ($) {
    'use strict';

    var Expandable = {
        init: function () {
            var that = this;

            that.expandable = $('.expandable');

            // init all data-gtm to being closed
            that.expandable.find('a.expandable-item__header').each(function () {
                $(this).attr('data-gtm', 'panel-closed');
            });

            //Handle changing colours on panel headers when open
            that.expandable.on('click', '.js-toggle-expand', function(event) {

                event.preventDefault();

                var expandableItem = $(this).closest('.expandable-item');
                var containerType = expandableItem.find('.expandable-item__title').text().toLowerCase();

                if (expandableItem.hasClass('expandable-item--open')) {
                    that.closeExpandableItem(expandableItem);
                    // dataLayer.push({
                    //     'event': containerType + '-collapse'
                    // });
                } else {
                    if (that.expandable.hasClass('expandable--single')) {
                        that.closeExpandableItem(expandableItem.siblings('.expandable-item'));
                    }
                    // dataLayer.push({
                    //     'event': containerType + '-expand'
                    // });
                    that.openExpandableItem(expandableItem);
                }
            });
        },

        openExpandableItem: function (expandableItem) {
            var expandableItemBody = expandableItem.find('.expandable-item__body');

            expandableItemBody.attr('aria-expanded', 'true');
            expandableItem.addClass('expandable-item--open');
            expandableItemBody.slideDown(200, function () {
                // hacky IE8 fix to force redraw of changed inline-block element
                expandableItem.closest('.grid__item').toggleClass('foo');
                expandableItem.closest('.grid__item').toggleClass('foo');
            });
            $(this).attr('data-gtm', 'panel-opened');
        },

        closeExpandableItem: function (expandableItem) {
            var expandableItemBody = expandableItem.find('.expandable-item__body');

            expandableItemBody.attr('aria-expanded', 'false');
            expandableItem.removeClass('expandable-item--open');
            expandableItemBody.slideUp(200, function () {
                // hacky IE8 fix to force redraw of changed inline-block element
                expandableItem.closest('.grid__item').toggleClass('foo');
                expandableItem.closest('.grid__item').toggleClass('foo');
            });
            $(this).attr('data-gtm', 'panel-closed');
        },

        showAllExpandableItems: function () {
            var expandableItems = this.expandable.find('.expandable-item');

            for (var i = 0, il = expandableItems.length; i < il; i++) {
                this.openExpandableItem($(expandableItems[i]));
            }
        }
    };

    // auto-initialize
    Expandable.init();

    return Expandable;

});

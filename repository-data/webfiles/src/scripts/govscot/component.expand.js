/**
 * Expand component
 *
 * Provides an interactive button that will toggle expanding/collapsing a target.
 */

/* global window */

'use strict';

import $ from 'jquery';

let expand = {
    init: function () {
        $('.js-expand').each(function () {
            let button = $(this),
                targetId = button.attr('data-target-selector'),
                target = $(targetId),
                collapsedHeight,
                expandedHeight;

            if (target.length === 0) {
                return;
            }

            /**
             * Initialise collapsed state and vars.
             */
            collapsedHeight = target.css('line-height');
            expandedHeight = target.height();

            if (button.css('display') !== 'none') {
                expand.collapseTarget(target, collapsedHeight, true);
            }
            else {
                button.addClass('expand--open');
            }

            /**
             * Button click handler
             */
            button.click(function(){
                /* Close */
                if (button.hasClass('expand--open')) {
                    expand.collapseTarget(target, collapsedHeight);
                    button.removeClass('expand--open');
                }
                /* Open */
                else {
                    expand.expandTarget(target, expandedHeight);
                    button.addClass('expand--open');
                }
            });

            /**
             * Add a hit target extension.
             */
            button.wrapInner('<span class="hit-target"></span>');

            /**
             * Update heights after resizing
             */
            $( window ).resize(function() {
                if (button.hasClass('expand--open')) {
                    expandedHeight = target.height();
                }
                else {
                    collapsedHeight = target.css('line-height');
                    expand.collapseTarget(target, collapsedHeight);
                }
            });
        });
    },

    /**
     * Collapses given element, by setting given height, setting
     * CSS – white-space, overlow, and text-overflow - appropriately.
     * Additionally sets children elements to dispaly:inline so ellipsis
     * will appear.
     */
    collapseTarget: function(el, height, noAnimate) {
        const animateSpeed = noAnimate ? 0 : 200;

        el.animate({'height': height}, animateSpeed, function(){
            el.css({
                'overflow': 'hidden',
                'text-overflow': 'ellipsis',
                'white-space': 'nowrap'
            });
            el.children().css('display', 'inline');
        });
    },

    /**
     * Expands given element, by setting given height and reversing most of the
     * settings from collapseTarget()
     */
    expandTarget: function(el) {
        const elSourceHeight = el.css('height');

        // figure out the height we want
        el.css({
            whiteSpace: 'normal',
            height: ''
        }).children().css('display', '');
        const elTargetHeight = el.outerHeight();

        // reset the height
        el.css({height: elSourceHeight});

        el.animate({'height': elTargetHeight}, function(){
            el.css('height','');
        });
    }
};

export default expand;

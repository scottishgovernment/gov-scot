/**
 * Handle common functionality for stickable elements, with configuration
 * and extension points.
 *
 * Usage:
 *    new Stickable( el, options);
 *
 *  - el: an HTML element (possibly from jquery)
 *  - options: an object with the following properties (all optional)
 *    - stickyClass: the class to add when the item is made sticky, defaults
 *      to 'is-sticky'
 *    - threshold: CSS selector for item to mark the threshold to make item
 *      sticky. if not set, then the threshold is 0.
 *    - thresholdOffset: CSS selector for an item with a height to offset the
 *      threshold by. defaults to 0. Also, if you want the current element then
 *      you can use the string 'this'.
 *    - stick: a callback to call once the item has been stuck
 *    - unstick: a callback that will be called with the item is unstuck
 */

'use strict';
define([
], function () {

    function Stickable( el, options ) {
        var obj = this;

        if (typeof options === 'undefined') {
            options = {};
        }

        this.item = $( el );
        this.options = options;
        this.classToAdd = options.stickyClass ? options.stickyClass : 'is-sticky';

        this.stick();

        // Re- 'stick' item on resize and scroll
        $(window).on('resize scroll', function(){
            obj.stick();
        });
    }

    /**
     * Determine point at which item should be come sticky.
     * This point is calculated everytime we check to see if we should stick it
     * since the threshold and the offset might change position or height
     * depending on height or other factors.
     */
    Stickable.prototype.pointToStickAt = function() {
        var elThreshold = $( this.options.threshold ),
            elOffset = $( this.options.thresholdOffset ),
            offset,
            pointToStickAt;

        if ( this.options.thresholdOffset === 'this' ) {
            offset = this.item.outerHeight();
        } else if (elOffset.length > 0) {
            offset = elOffset.outerHeight();
        } else {
            offset = 0;
        }

        if (elThreshold.length > 0) {
            pointToStickAt = elThreshold.offset().top - offset;
        } else {
            pointToStickAt = 0;
        }

        return pointToStickAt;
    };

    /**
     * Make item sticky if threshold is exceeded
     */
    Stickable.prototype.stick = function() {
        if ($(window).scrollTop() > this.pointToStickAt()) {
            this.item.addClass( this.classToAdd );
            if (this.options.stick instanceof Function) {
                this.options.stick();
            }
        } else {
            this.item.removeClass( this.classToAdd );
            if (this.options.unstick instanceof Function) {
                this.options.unstick();
            }
        }
    };

    return Stickable;
});

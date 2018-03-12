/**
 * Page group component
 *
 *  - Provides toggle button interactivity by default
 *  - Exposing functions for opening/closing the list
 *  - Publishes events when list is opened or closed
 */
'use strict';
define([
    '../govscot/pubsub',
    './component.stickable'
], function (pubsub, Stickable) {

    var classButtonOpen = 'page-group__toggle--open',
        classListExpanded = 'page-group__list--expanded',
        isMobile = $('.page-group__toggle').is(':visible');

    function PageGroup() {

        var pgroup = this;

        $('body').on('click', '.js-show-page-group-list', function (event) {
            if ( $(this).hasClass(classButtonOpen) ) {
                pgroup.close();
            } else {
                pgroup.open();
            }
        });

        // Look for stickable class and make sticky
        new Stickable( $('.page-group--stickable'), {  //NOSONAR
            'stickyClass': 'page-group--stickable--is-sticky',
            'threshold': '.js-sticky-header-position',
            'thresholdOffset': '.sticky-document-info',
            'stick': function(){
                if (!isMobile) {
                    var el = $('.page-group--stickable');
                    el.css({
                        top: $('.sticky-document-info').outerHeight() || 0
                      })
                      .width( el.parent().width() );
                }
            },
            'unstick': function(){
                $('.page-group--stickable').css('top','').width('');
            }
        });
    }

    PageGroup.prototype.open = function() {
        var button = $('.js-show-page-group-list');
        var list = button.next('.page-group__list');
        button.addClass(classButtonOpen);
        list.slideDown(200).addClass(classListExpanded);
        pubsub.publish('page-group-change', ['open']);
    };

    PageGroup.prototype.close = function() {
        var button = $('.js-show-page-group-list');
        var list = button.next('.page-group__list');
        button.removeClass(classButtonOpen);
        list.slideUp(200).removeClass(classListExpanded);
        pubsub.publish('page-group-change', ['close']);
    };

    return {
        init: function(){
            return new PageGroup();
        }
    };
});

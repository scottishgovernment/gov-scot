/**
 * Sticks a title/header element to the top of the viewport
 */
'use strict';

import Stickable from './component.stickable';
import $ from 'jquery';

const displayIfSupportFiles = function () {
    const $trigger = $('.sticky-document-info__trigger');

    if($('.sticky-document-info__panel .supporting-files__item').length === 0) {
        $trigger.hide();
        $trigger.removeClass('sticky-document-info__trigger--expanded');
        $('.sticky-document-info__panel').removeClass('sticky-document-info__panel--open');
    } else {
        $trigger.show();
    }
};

const docInfo = new Stickable( $('.sticky-document-info'), {
    'stickyClass': 'sticky-document-info--is-sticky',
    'threshold': '.js-sticky-header-position',
    'thresholdOffset': 'this',
    'stick': function(){
        $('.sticky-document-info__trigger').show();

        const topOffset = $('.site-header--scaled').length ? 52 : 0;
        const el = $('.sticky-document-info');

        if (topOffset) {
            el.css('top', topOffset);
        } else {
            el.next().css('margin-top', el.height() );
        }

        // if panel is not open update button text
        if ($('.sticky-document-info__panel--open').length === 0) {
            $('.sticky-document-info__trigger').text('All files');
        }

    },
    'unstick': function() {
        displayIfSupportFiles();

        const el = $('.sticky-document-info');

        $('.sticky-document-info').css('top','');
        el.next().css('margin-top', '');

        // if panel is not open update button text
        if ($('.sticky-document-info__panel--open').length === 0) {
            $('.sticky-document-info__trigger').text('Supporting files');
        }
    }
});

export default docInfo;

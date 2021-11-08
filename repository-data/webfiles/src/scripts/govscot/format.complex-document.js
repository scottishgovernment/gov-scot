// COMPLEX DOCUMENT FORMAT

/* global window */

'use strict';

import $ from 'jquery';

const complexDocumentPage = {};

complexDocumentPage.init = function () {
    tweakComplexDocumentMarkup();
};

window.format = complexDocumentPage;
window.format.init();

function tweakComplexDocumentMarkup () {
    // 1. add overflow class to image containers
    $('.bs_figure').addClass('overflow--large--three-twelfths');

    // 2. style standard definition block
    $('blockquote.bs_blockquote').addClass('info-note blockquote');

    // 2.1 define elements
    let limitationTitle, standardTitle, newLimitationTitle, newStandardTitle;

    $('blockquote.bs_blockquote p').each(function () {
        if ($(this).text().toLowerCase() === 'limitation:') {
            limitationTitle = $(this);
        }
    });

    // 2.2 replace elements
    if (limitationTitle) {
        newLimitationTitle = $('<h5 class=delta/>');
        newLimitationTitle.html(limitationTitle.text());
        newLimitationTitle.insertAfter(limitationTitle);
        limitationTitle.remove();
    }

    standardTitle = $('blockquote.bs_blockquote .bs_blockquote-title');
    newStandardTitle = $('<h4 class=\'beta no-top-margin\'/>');
    newStandardTitle.html(standardTitle.html());
    newStandardTitle.insertAfter(standardTitle);
    standardTitle.remove();

    // 3. fix notes
    let bsnote, bsnotecontent;
    bsnote = $('.bs_note');
    bsnotecontent = bsnote.find('td:last');
    bsnote
        .addClass('note info-note')
        .removeAttr('style')
        .html(bsnotecontent.html());
}

export default complexDocumentPage;

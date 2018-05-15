define([
    '../shared/component.page-group',
    './component.display-toggle'
], function(pageGroup, displayToggle) {
    'use strict';

    var policyPage = {
        init: function(){
            pageGroup.init();
            displayToggle.init();
        }
    };

    window.format = policyPage;
    window.format.init();

    return policyPage;
});

define([
    '../shared/component.page-group',
    './component.display-toggle'
], function(pageGroup, displayToggle) {

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

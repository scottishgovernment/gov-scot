define([
    '../shared/component.page-group',
    './component.display-toggle'
], function(pageGroup) {

    var policyPage = {
        init: function(){
            pageGroup.init();
        }
    };

    window.format = policyPage;
    window.format.init();

    return policyPage;
});

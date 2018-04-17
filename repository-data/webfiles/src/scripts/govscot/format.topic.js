define([
    './component.expandable',
    './tool.text-truncate'
], function(expandable, TextTruncate) {

    var topicPage = {};

    topicPage.init = function() {
        TextTruncate();
    };

    window.format = topicPage;
    window.format.init();

    return topicPage;
});

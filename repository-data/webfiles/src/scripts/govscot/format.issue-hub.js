define([], function() {

    var issueHubPage = {};

    issueHubPage.init = function() {
        this.initIssueCalloutDisplacement();
    };

    // nudge the sidebar down if necessary
    // recheck on resize
    issueHubPage.initIssueCalloutDisplacement = function () {
        displaceSidebar();

        $(window).on('resize', function () {
            displaceSidebar();
        });
    };

    function displaceSidebar () {
        var calloutBox = $('.issue-callout'),
            elementToDisplace = $('.displace-by-issue-callout');

        var amountToDisplace = calloutBox.height() + 56 - 50;

        elementToDisplace.css({paddingTop: amountToDisplace});
    };

    window.format = issueHubPage;
    window.format.init();

    return issueHubPage;
});
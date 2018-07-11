let FeedbackPerspective;

(function () {
    if (typeof FeedbackPerspective == 'undefined') {
        FeedbackPerspective = {};
        FeedbackPerspective.resizeEventRegisteredMap = [];
        FeedbackPerspective.iFrameRendered = false;
    }

    FeedbackPerspective.showIFrame = function (id) {
        if (!FeedbackPerspective.iFrameRendered) {
            let iFrame = $('#feedback-perspective').find('iframe');
            iFrame.attr('src', 'https://lgv.publishing.gov.scot/#/feedback');

            FeedbackPerspective.iFrameRendered = true;
        }
    };

    window.addEventListener('message', function (event) {
        let feedbackContentItemLink = $('.feedback-content-item-link');
        let eventData = JSON.parse(event.data);
        feedbackContentItemLink.attr('data-uuid', eventData.uuid);
        feedbackContentItemLink.attr('href', eventData.href);
        feedbackContentItemLink.click();
    });
})();

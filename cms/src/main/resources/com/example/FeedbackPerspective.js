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

            // iFrame.attr('src', 'https://lgv.publishing.gov.scot/#/feedback');

            FeedbackPerspective.iFrameRendered = true;
        }
    };

    window.addEventListener('message', function (event) {
        let feedbackContentItemLink = $('.feedback-content-item-link');
        feedbackContentItemLink.attr('data-querystring', JSON.parse(event.data).querystring);
        feedbackContentItemLink.attr('href', JSON.parse(event.data).href);
        feedbackContentItemLink.click();
    });
})();

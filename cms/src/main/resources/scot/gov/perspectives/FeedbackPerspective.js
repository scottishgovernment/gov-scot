let FeedbackPerspective;

(function () {
    let environments = {
        lcl: 'https://lclrubric.publishing.gov.scot',
        dev: 'https://devrubric.publishing.gov.scot',
        int: 'https://intrubric.publishing.gov.scot',
        exp: 'https://exprubric.publishing.gov.scot',
        per: 'https://perrubric.publishing.gov.scot',
        tst: 'https://tstrubric.publishing.gov.scot',
        uat: 'https://uatrubric.publishing.gov.scot',
        blu: 'https://blurubric.publishing.gov.scot',
        grn: 'https://grnrubric.publishing.gov.scot',
        beta: 'https://rubric.publishing.gov.scot',
        publishing: 'https://rubric.publishing.gov.scot'
    };

    if (typeof FeedbackPerspective == 'undefined') {
        FeedbackPerspective = {};
        FeedbackPerspective.resizeEventRegisteredMap = [];
        FeedbackPerspective.iFrameRendered = false;
    }

    FeedbackPerspective.showIFrame = function (id) {
        if (!FeedbackPerspective.iFrameRendered) {
            let iframe = $('#feedback-perspective').find('iframe');

            let currentEnvironmentKey = window.location.host.substring(0, window.location.host.indexOf('.'));

            if (currentEnvironmentKey.length === 0) {
                currentEnvironmentKey = 'lcl';
            }

            iframe.attr('src', environments[currentEnvironmentKey] + '/#/feedback');

            FeedbackPerspective.iFrameRendered = true;
        }
    };

    window.addEventListener('message', function (event) {
        let feedbackContentItemLink = $('.feedback-content-item-link');
        let eventData = JSON.parse(event.data);
        feedbackContentItemLink.attr('data-uuid', eventData.uuid);
        feedbackContentItemLink.attr('data-path', eventData.path);
        feedbackContentItemLink.attr('href', eventData.href);
        feedbackContentItemLink.click();
    });


    
})();

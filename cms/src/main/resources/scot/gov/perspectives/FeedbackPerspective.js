let FeedbackPerspective;

(function () {
    let environments = {
        lcl: 'https://lgv.publishing.gov.scot',
        dev: 'https://dgv.publishing.gov.scot',
        int: 'https://igv.publishing.gov.scot',
        exp: 'https://egv.publishing.gov.scot',
        per: 'https://pgv.publishing.gov.scot',
        tst: 'https://tgv.publishing.gov.scot',
        uat: 'https://ugv.publishing.gov.scot',
        blu: 'https://bgv.publishing.gov.scot',
        grn: 'https://ggv.publishing.gov.scot',
        www: 'https://beta.publishing.gov.scot'
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
        feedbackContentItemLink.attr('href', eventData.href);
        feedbackContentItemLink.click();
    });
})();

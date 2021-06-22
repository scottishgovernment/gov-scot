// ISSUE HUB FORMAT

'use strict';

/* global window */
const issueHubPage = {};

issueHubPage.init = () => {
    issueHubPage.displaceSidebar();

    window.addEventListener('resize', () => {
        issueHubPage.displaceSidebar();
    });
};

issueHubPage.displaceSidebar = () => {
    const calloutBox = document.querySelector('.issue-callout');
    document.documentElement.style.setProperty('--issue-callout-offset', (calloutBox.scrollHeight - 80) + 'px');
};

window.format = issueHubPage;
window.format.init();

export default issueHubPage;

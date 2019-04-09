// TOPIC FORMAT

/* global window */

'use strict';

import './component.expandable';
import TextTruncate from './tool.text-truncate';

const topicPage = {};

topicPage.init = function() {
    TextTruncate();
};

window.format = topicPage;
window.format.init();

export default topicPage;

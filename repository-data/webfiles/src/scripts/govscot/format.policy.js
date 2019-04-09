// POLICY FORMAT

/* global window */

'use strict';

import pageGroup from '../shared/component.page-group';
import displayToggle from './component.display-toggle';

const policyPage = {
    init: function(){
        pageGroup.init();
        displayToggle.init();
    }
};

window.format = policyPage;
window.format.init();

export default policyPage;

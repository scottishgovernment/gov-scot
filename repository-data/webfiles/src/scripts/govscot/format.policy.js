// POLICY FORMAT

/* global window */

'use strict';

import displayToggle from './component.display-toggle';

const policyPage = {
    init: function(){
        displayToggle.init();
    }
};

window.format = policyPage;
window.format.init();

export default policyPage;

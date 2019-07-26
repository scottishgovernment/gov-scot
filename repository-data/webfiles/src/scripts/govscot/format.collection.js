// COLLECTION FORMAT

/* global window */

'use strict';

import displayToggle from './component.display-toggle';

const collectionPage = {
    init: function(){
        displayToggle.init();
    }
};

window.format = collectionPage;
window.format.init();

export default collectionPage;

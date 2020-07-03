// ABOUT FORMAT

'use strict';

import SideNavigation from '../../scss/design-system-preview/components/side-navigation/side-navigation';

const aboutPage = {
    init: function(){
        this.initSideNavigation();
    },

    initSideNavigation: function () {
        const sideNavigationModules = [].slice.call(document.querySelectorAll('[data-module="ds-side-navigation"]'));
        sideNavigationModules.forEach(sideNavigation => new SideNavigation(sideNavigation).init());
    }
};

window.format = aboutPage;
window.format.init();

export default aboutPage;

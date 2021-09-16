// ORG ROLES FORMAT

/* global window */

'use strict';

const policyPage = {
    init: function(){
        // replace erroring images with placeholders
        this.replaceErrorImages();
    },

    replaceErrorImages: function () {
        const personImages = [].slice.call(document.querySelectorAll('.gov_person__image'));
        personImages.forEach(img => {
            img.addEventListener('error', (event) => {
                if (event.target.dataset.placeholder) {
                    event.target.removeAttribute('srcset');
                    event.target.src = '/assets/images/people/placeholder.png';
                    event.target.dataset.placeholder = true;
                }
            });
        });
    }
};

window.format = policyPage;
window.format.init();

export default policyPage;

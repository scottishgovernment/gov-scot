'use strict';

class UpdateHistory {
    constructor(updateHistory) {
        this.updateHistory = updateHistory;
        this.metadataLink = document.querySelector(`[href="#${this.updateHistory.id}"]`);
        this.endLink = document.querySelector(`[aria-controls="${this.updateHistory.id}"]`);
    }

    init() {
        if (this.metadataLink) {
            this.metadataLink.addEventListener('click', (event) => {
                event.preventDefault();

                if (this.endLink && this.endLink.getAttribute('aria-expanded') === 'false') {
                    this.endLink.click();
                }



                this.updateHistory.scrollIntoView();

            });
        }
    }
}

function isInViewport(el) {
    const rect = el.getBoundingClientRect();
    return (
        rect.top >= 0 &&
        rect.left >= 0 &&
        rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
        rect.right <= (window.innerWidth || document.documentElement.clientWidth)

    );
}


export default UpdateHistory;

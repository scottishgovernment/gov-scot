'use strict';

import ToggleLink from './component.toggle-link';

class UpdateHistory {
    constructor(updateHistory) {
        this.updateHistory = updateHistory;
        this.metadataLink = document.querySelector(`[href="#${this.updateHistory.id}"]`);
        this.endLink = document.querySelector(`[aria-controls="${this.updateHistory.id}"]`);
    }

    init() {
        let endLink;

        if (this.endLink) {
            endLink = new ToggleLink(this.endLink);
            endLink.init();
        }

        if (this.metadataLink) {
            this.metadataLink.addEventListener('click', (event) => {
                event.preventDefault();

                if (endLink && !endLink.expanded) {
                    endLink.setOpen();
                }

                this.updateHistory.scrollIntoViewIfNeeded();
            });
        }
    }
}

export default UpdateHistory;

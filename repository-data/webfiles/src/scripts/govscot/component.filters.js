/* global document, window */

'use strict';

class GovFilters {
    constructor (govFilters) {
        this.govFilters = govFilters;
    }

    init() {
        if (this.govFilters) {
            this.setupGovFilters();
        }
    }

    setupGovFilters() {
        // transform markup to button-driven version
        this.filtersControl = this.govFilters.querySelector('.js-toggle-filters');
        const filtersLabel = this.govFilters.querySelector('.gov_filters__expand');
        const idString = parseInt(Math.random() * 1000000, 10);
        this.filtersForm = this.govFilters.querySelector('.gov_filters__form');
        this.filtersForm.id = this.filtersForm.id || `filters-${idString}`;

        this.filtersControl.checked = false;

        const filtersExpandButton = document.createElement('button');
        filtersExpandButton.classList.add('gov_filters__expand');
        filtersExpandButton.classList.add('ds_link');
        filtersExpandButton.classList.add('js-filters-button');
        filtersExpandButton.setAttribute('aria-expanded', false);
        filtersExpandButton.innerHTML = filtersLabel.innerHTML;
        filtersExpandButton.setAttribute('aria-expanded', false);
        filtersExpandButton.setAttribute('aria-controls', this.filtersForm.id);

        filtersLabel.parentNode.removeChild(filtersLabel);
        this.govFilters.insertBefore(filtersExpandButton, this.filtersForm);

        filtersExpandButton.setAttribute('aria-controls', this.filtersForm.id);

        // events
        filtersExpandButton.addEventListener('click', () => {
            const isOpen = this.filtersControl.checked;

            if (!isOpen) {
                this.openFilters();

            } else {
                this.closeFilters();

            }

            filtersExpandButton.setAttribute('aria-expanded', !isOpen);
            this.filtersControl.checked = !isOpen;
        });

        this.filtersExpandButton = filtersExpandButton;

        this.govFilters.classList.add('js-initialised');
    }

    openFilters() {
        this.filtersForm.style.display = 'block';
        this.filtersForm.style.maxHeight = this.filtersForm.scrollHeight + 14 + 'px';
        window.setTimeout(() => {
            this.filtersForm.style.maxHeight = 'unset';
            this.filtersControl.checked = true;
        }, 200);
        this.filtersExpandButton.querySelector('.js-show-filters-text').innerText = 'Hide search filters';
    }

    closeFilters() {
        this.filtersForm.style.maxHeight = 0;
        window.setTimeout(() => {
            this.filtersForm.style.display = 'none';
            this.filtersControl.checked = false;
        }, 200);
        this.filtersExpandButton.querySelector('.js-show-filters-text').innerText = 'Show search filters';
    }
}

export default GovFilters;

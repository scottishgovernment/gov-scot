class ToggleLink {
    constructor(toggleLink) {
        this.toggleLink = toggleLink;
        this.toggleTarget = document.getElementById(this.toggleLink.getAttribute('aria-controls'));
        this.openText = this.toggleLink.innerText;
        this.closeText = this.toggleLink.dataset.toggledText;
        this.expanded = this.toggleLink.hasAttribute('aria-expanded') ? (this.toggleLink.getAttribute('aria-expanded') === 'true') : false;
    }

    init() {
        // force the aria-expanded attribute
        this.toggleLink.setAttribute('aria-expanded', this.expanded);

        this.toggleLink.addEventListener('click', (event) => {
            event.preventDefault();

            if (this.expanded) {
                this.setHidden();
            } else {
                this.setVisible();

                const rect = this.toggleTarget.getBoundingClientRect();
                if (rect.top + 32 > window.innerHeight) {
                    window.scrollTo(window.scrollX, window.scrollY + rect.top / 2);
                }
            }
        });
    }

    setVisible() {
        this.toggleTarget.classList.add('gov_toggle-link__target--visible');
        this.toggleLink.innerText = this.closeText;

        this.expanded = !this.expanded;
        this.toggleLink.setAttribute('aria-expanded', this.expanded);

        if (this.closeText === '') {
            this.toggleLink.parentNode.removeChild(this.toggleLink);
        }
    }

    setHidden() {
        this.toggleTarget.classList.remove('gov_toggle-link__target--visible');
        this.toggleLink.innerText = this.openText;

        this.expanded = !this.expanded;
        this.toggleLink.setAttribute('aria-expanded', this.expanded);
    }
}

export default ToggleLink;

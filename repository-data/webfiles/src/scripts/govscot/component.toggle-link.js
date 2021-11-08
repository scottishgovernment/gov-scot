class ToggleLink {
    constructor(toggleLink) {
        this.toggleLink = toggleLink;
        this.toggleTarget = document.getElementById(this.toggleLink.getAttribute('aria-controls'));
        this.openText = this.toggleLink.innerText;
        this.closeText = this.toggleLink.dataset.toggledText;
        this.expanded = this.toggleLink.hasAttribute('aria-expanded') ? (this.toggleLink.getAttribute('aria-expanded') === 'true') : false;
    }

    init() {
        this.toggleLink.addEventListener('click', (event) => {
            event.preventDefault();

            if (this.expanded) {
                this.setClosed();
            } else {
                this.setOpen();
                this.toggleTarget.scrollIntoViewIfNeeded();
            }
        });
    }

    setOpen() {
        this.toggleTarget.classList.remove('fully-hidden');
        this.toggleLink.innerText = this.closeText;

        this.expanded = !this.expanded;
        this.toggleLink.setAttribute('aria-expanded', this.expanded);
    }

    setClosed() {
        this.toggleTarget.classList.add('fully-hidden');
        this.toggleLink.innerText = this.openText;

        this.expanded = !this.expanded;
        this.toggleLink.setAttribute('aria-expanded', this.expanded);
    }
}

export default ToggleLink;

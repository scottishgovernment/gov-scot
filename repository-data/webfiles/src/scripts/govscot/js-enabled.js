(function () {
    const htmlClass = document.documentElement.getAttribute('class') || '';
    document.documentElement.setAttribute('class', (htmlClass ? htmlClass + ' ' : '') + 'js-enabled');
})();

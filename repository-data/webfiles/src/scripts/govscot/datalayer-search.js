(function () {
    const datalayerScriptElement = document.getElementById('gtm-datalayer-search');

    if (!datalayerScriptElement) {
        return;
    }

    const searchEnabled = datalayerScriptElement.dataset.enabled;
    const searchType = datalayerScriptElement.dataset.type;

    window.dataLayer = window.dataLayer || [];

    const obj = {};

    function present(value) {
        return value && !!value.length;
    }

    if (present(searchEnabled)) {
        obj.searchEnabled = searchEnabled;
    }

    if (present(searchType)) {
        obj.searchType = searchType;
    }

    window.dataLayer.push(obj);
})();

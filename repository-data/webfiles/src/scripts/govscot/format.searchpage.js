// DEFAULT

'use strict';

import SearchFilters from './component.search-filters';

const searchPage = {
    init: function () {
        this.searchFilters = new SearchFilters();
        this.searchFilters.init();
    }
};

window.format = searchPage;
window.format.init();

export default searchPage;

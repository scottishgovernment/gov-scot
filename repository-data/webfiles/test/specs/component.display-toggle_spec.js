'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import displayToggle from '../../src/scripts/govscot/component.display-toggle';

describe('"Display toggle" component', function () {

    beforeEach(function() {
        // first load your fixtures
        loadFixtures('display-toggle.html');
        displayToggle.init();
    });

    it("should show a targeted element", function() {

        let toggler = $('#toggler');
        let target = $('#target');

        $('.js-display-toggle').trigger('click');

        expect(target.hasClass('display-toggle--shown')).toBe(true);
        expect(toggler.hasClass('hidden')).toBe(true);
    });
});

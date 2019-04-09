'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import expand from '../../src/scripts/govscot/component.expand';

describe('"Expand" component', function () {

    beforeEach(function() {
        // first load your fixtures
        loadFixtures('expand.html');

    });

    it("should show a targeted element", function() {
        expand.init();
        let targetElement = $('#target');
        let expandButton = $('#expand-button');
        expandButton.click();

        //expect (targetElement.hasClass('')).toBe('true');
        expect (expandButton.hasClass('expand--open')).toBe(true);
    });

    it("should hide a targeted element", function() {
        expand.init();
        let targetElement = $('#target');
        let expandButton = $('#expand-button');
        $('.js-expand').click();
        $('.js-expand').click();

        //expect (targetElement.hasClass('')).toBe('true');
        expect (expandButton.hasClass('expand--open')).toBe(false);
    });

    it ("should do nothing if there is no appropriate target element", function () {
        expand.init();
        let expandButton = $('#expand-button-no-target');
        $('.js-expand').click();
        expect (expandButton.hasClass('expand--open')).toBe(false);
    });

    it ('should recalculate heights on window resize', function () {
        expand.init();
        $(window).resize();
    });

    it ('should ensure the triggering button is in the "open" state if a panel is expanded on load', function () {
        let targetElement = $('#target');
        let expandButton = $('#expand-button');

        expandButton.css({display: 'none'});

        expand.init();

        expect (expandButton.hasClass('expand--open')).toBe(true);
    });
});

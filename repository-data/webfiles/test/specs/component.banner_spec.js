'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import banner from '../../src/scripts/govscot/component.banner';

describe('"Banner" component', function () {
    let testBanner;
    let closeButton;
    let extraContent;

    beforeEach(function() {
        // first load your fixtures
        loadFixtures('banner.html');
        testBanner = $('#test-banner');
        closeButton = $('#close-button');
        extraContent = $('#extra-content');
    });

    it("should toggle extra content of a banner on click of its 'close' button", function() {
        banner.init('test-banner');

        let initialState = extraContent.hasClass('hidden-xsmall');

        closeButton.trigger('click');
        expect(extraContent.hasClass('hidden-xsmall')).toBe(!initialState);
    });

    it ('should get the initial state from sessionStorage', function () {
        sessionStorage.setItem('test-banner-closed', true);
        banner.init('test-banner');
        expect(closeButton.hasClass('notification__close--minimised')).toBe(true);
        expect(extraContent.hasClass('hidden-xsmall')).toBe(true);

        sessionStorage.setItem('test-banner-closed', 'false');
        banner.init('test-banner');
        expect(closeButton.hasClass('notification__close--minimised')).toBe(false);
        expect(extraContent.hasClass('hidden-xsmall')).toBe(false);
    });

    it ('should set a sessionStorage value on click of the close button', function () {
        sessionStorage.setItem('test-banner-closed', 'false');
        banner.init('test-banner');

        let initialState = extraContent.hasClass('hidden-xsmall');

        closeButton.trigger('click');
        expect(sessionStorage.getItem('test-banner-closed')).toBe('true');
    });
});

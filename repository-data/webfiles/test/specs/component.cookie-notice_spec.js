'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import cookieNotice from '../../src/scripts/govscot/component.cookie-notice';
import cookie from '../../src/scripts/govscot/cookie';

describe('"Cookie notice" component', function () {
    let closeButton,
        notice,
        cookieNoticeContainer;

    beforeEach(function() {
        // first load your fixtures
        loadFixtures('cookie-notice.html');

        closeButton = $('#close-button');
        cookieNoticeContainer = $('#cookie-notice');
    });

    it("should close the cookie notice on click of its 'close' button", function() {
        document.cookie = "cookie-notification-acknowledged=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        cookieNotice.init();

        closeButton.trigger('click');

        // hide notice
        expect(cookieNoticeContainer.hasClass('hidden')).toBe(true);
        // set cookie
        expect(cookie('cookie-notification-acknowledged')).toEqual('yes');
    });

    it ('should not show the cookie notice if it has been acknowledged', function () {
        cookie('cookie-notification-acknowledged', 'yes');
        cookieNotice.init();

        expect(cookieNoticeContainer.hasClass('hidden')).toBe(true);
    });

    it ('should show the cookie notice if it has not been acknowledged', function () {
        cookie('cookie-notification-acknowledged', false);
        document.cookie = "cookie-notification-acknowledged=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";

        cookieNotice.init();

        expect(cookieNoticeContainer.hasClass('hidden')).toBe(false);
    });
});

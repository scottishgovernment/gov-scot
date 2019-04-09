'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import cookie from '../../src/scripts/govscot/cookie';

describe('"Cookie" component', function () {
    it('should return a cookie\'s value if the cookie exists', function() {
        document.cookie = 'alpha=beta';

        expect( cookie('alpha') ).toEqual('beta');
    });

    it('should return null is a cookie does not exist', function() {
        expect( cookie('gamma') ).toBeNull();
    });

    it('should set a cookie if a value is passed to the function', function() {
        var cookieData = cookie('delta', 'epsilon');
        expect(cookieData.name).toEqual('delta');
        expect(cookieData.value).toEqual('epsilon');
    });

    it('should set a cookie and an expiry time if an expiry duration is passed to the function', function() {
        var cookieData = cookie('zeta', 'eta', 7);
        expect(cookieData.expires).not.toBeNull();
    });
});

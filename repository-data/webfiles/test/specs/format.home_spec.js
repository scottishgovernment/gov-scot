'use strict';

jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

import Home from '../../src/scripts/govscot/format.home';

describe('"Home" format', function () {
    beforeEach(function() {
        // first load your fixtures
        loadFixtures('home.html');
    });

    it ('Should set up its various bits and pieces on init', function () {
        spyOn(Home, 'attachEventHandlers');

        Home.init();

        expect(Home.attachEventHandlers).toHaveBeenCalled();
    });

    describe('Policy block', function () {
        it('should direct users to a prefiltered policy page on submit of the policy form', function () {
            Home.init();

            var policySubmitButton = $('.js-policy-form-submit');
            spyOn(Home, 'navigateToUrl');

            // case 1: nothing selected
            policySubmitButton.trigger('click');
            expect(Home.navigateToUrl).toHaveBeenCalledWith('/policies/' + window.location.pathname.substring(1));

            // case 2: some checkboxes selected
            Home.navigateToUrl.calls.reset();
            $('input[name="topics[]"]').get(6).setAttribute('checked', 'checked');
            $('input[name="topics[]"]').get(7).setAttribute('checked', 'checked');

            policySubmitButton.trigger('click');
            expect(Home.navigateToUrl).toHaveBeenCalledWith('/policies/?topics=Economy;Education');

            // case 3: search term added
            Home.navigateToUrl.calls.reset();
            $('#filters-search-term').val('foo');
            policySubmitButton.trigger('click');
            expect(Home.navigateToUrl).toHaveBeenCalledWith('/policies/?term=foo&topics=Economy;Education');
        });
    });
});

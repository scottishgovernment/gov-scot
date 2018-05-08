jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

define([
    'govscot/component.expand'
], function(expand) {
    describe('"Expand" component', function () {

        beforeEach(function() {
            // first load your fixtures
            loadFixtures('expand.html');
            expand.init();
        });

        it("should show a targeted element", function() {
            let targetElement = $('#target');
            let expandButton = $('#expand-button');
            expandButton.click();

            //expect (targetElement.hasClass('')).toBe('true');
            expect (expandButton.hasClass('expand--open')).toBe(true);
        });

        it("should hide a targeted element", function() {
            let targetElement = $('#target');
            let expandButton = $('#expand-button');
            $('.js-expand').click();
            $('.js-expand').click();

            //expect (targetElement.hasClass('')).toBe('true');
            expect (expandButton.hasClass('expand--open')).toBe(false);
        });

        it ("should do nothing if there is no appropriate target element", function () {
            let expandButton = $('#expand-button-no-target');
            $('.js-expand').click();
            expect (expandButton.hasClass('expand--open')).toBe(false);
        });

        it ('should recalculate heights on window resize', function () {
            $(window).resize();
        });
    });
});

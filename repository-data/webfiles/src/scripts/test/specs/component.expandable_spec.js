jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

define([
    'govscot/component.expandable',
    'jquery'
], function(expandable, $) {
    'use strict';

    describe('"Expandable" component', function() {

        window.jq = $;

        beforeEach(function() {
            // first load your fixtures
            loadFixtures('expandable.html');
        });

        it("init should be defined", function() {
            expect(expandable.init).toBeDefined();
        });

        it("closed panel should open when clicked", function() {
            expandable.init();

            // target the second panel, which is closed in the fixture
            var targetPanel = $($('.expandable-item')[1]);
            var targetBody = targetPanel.find('.expandable-item__body');

            // trigger click
            targetPanel.find('.js-toggle-expand').trigger('click');

            expect(targetPanel.hasClass('expandable-item--open')).toBeTruthy();
        });

        it("open panel should close when clicked", function() {
            expandable.init();

            // target the first panel, which is open in the fixture
            var targetPanel = $($('.expandable-item')[0]);
            var targetBody = targetPanel.find('.expandable-item__body');
            // trigger click
            targetPanel.find('.js-toggle-expand').trigger('click');

            expect(targetPanel.hasClass('expandable-item--open')).toBeFalsy();
        });

        it ("should allow only a single expandable item to be open when set that way", function () {
            var expandableContainer = $('.expandable'),
                expandableItemOne = $(expandableContainer.find('.expandable-item')[0]),
                expandableItemTwo = $(expandableContainer.find('.expandable-item')[1]);

            // add the "single" class before init-ing
            expandableContainer.addClass('expandable--single');

            // open one item to start with
            expandableItemOne.addClass('expandable-item--open expandable-item--init-open');

            // now init
            expandable.init();

            expect(expandableItemOne.hasClass('expandable-item--open')).toBeTruthy();
            expect(expandableItemTwo.hasClass('expandable-item--open')).toBeFalsy();

            expandableItemTwo.find('.js-toggle-expand').trigger('click');

            setTimeout(function () {
                expect(expandableItemOne.hasClass('expandable-item--open')).toBeFalsy();
                expect(expandableItemTwo.hasClass('expandable-item--open')).toBeTruthy();
            }, 3000);
        });

        it ("should be able to open all panels at once", function () {
            expandable.init();

            let expandableCount = $('.expandable-item').length;

            expandable.showAllExpandableItems();

            let openExpandableCount = $('.expandable-item--open').length;

            expect (openExpandableCount).toEqual(expandableCount);
        });

    });
});

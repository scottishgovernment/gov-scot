define([
    'govscot/global'
], function(global) {
    describe('"Global"', function () {
        describe ('The header on mobile', function () {
            beforeEach(function() {
                // load your fixtures
                loadFixtures('home.html');
                global.init();
                jasmine.clock().install();
            });
    
            it ('should open search box when search icon is clicked', function () {
                var search = $('.site-header__button--search'),
                    searchPanel = $('.mobile-search');
    
                expect(searchPanel.hasClass('mobile-layer--open')).toBeFalsy();
    
                search.click();
    
                expect(searchPanel.hasClass('mobile-layer--open')).toBeTruthy();
            });
    
            it('should open main nav when menu icon is clicked', function () {
                var menuButton = $('.site-header__button--nav'),
                    menuPanel = $('.mobile-nav');
    
                expect(menuPanel.hasClass('mobile-layer--open')).toBeFalsy();
    
                menuButton.click();
    
                expect(menuPanel.hasClass('mobile-layer--open')).toBeTruthy();
    
            });
    
            afterEach(function() {
                jasmine.clock().uninstall();
    
            });
        });
    });
});

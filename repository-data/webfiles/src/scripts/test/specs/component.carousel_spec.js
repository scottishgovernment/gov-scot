jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

define([
    'govscot/component.carousel'
], function(Carousel) {

    var config = {
        'selectors': {
          'carousel': '.carousel',
          'items': '.carousel-item',
          'stage': '.carousel-item__stage',
          'video': '.carousel-item__video',
          'textOverlay': '.carousel-item__stage-text',
          'textOverlayToggle': '.carousel-item__stage-text-toggle',
          'support': '.carousel-item__support',
          'title': '.carousel-item__title',
          'desc': '.carousel-item__desc',
          'link': '.carousel-item__link',
          'controls': '.carousel-controls',
          'controlsPrevious': '.carousel-controls__link--previous',
          'controlsNext': '.carousel-controls__link--next',
          'controlsPip': '.carousel-controls__link--pip',
          'controlsPlaceholder': '.carousel-controls-placeholder'
        },
        'html': {
          'pip': '<li class="carousel-controls__link">' +
              '<button class="carousel-controls__link--pip"></button></li>',
          'playbutton': '<div class="carousel-item__play-button"></div>'
        },
        'videoIdPrefix': 'carousel-video',
        'hoverDelta': 24
      };

    var carousel,
        items,
        stage,
        support,
        desc,
        controls,
        controlsPrevious,
        controlsNext,
        textOverlayToggle,
        currentItem,
        itemClickedText,
        itemClickedHref,
        videos,
        touchDevice;

    describe('Carousel without a carousel element', function () {
        it ('should do nothing', function () {
            expect (Carousel.init()).toBe(false);
        });
    });

    describe('"Carousel" component', function () {
        beforeEach(function() {
            // first load your fixtures
            loadFixtures('carousel.html');

            carousel = $(config.selectors.carousel);
            items = $(config.selectors.items);
            stage = $(config.selectors.stage);
            support = $(config.selectors.support);
            desc = $(config.selectors.desc);
            controls = $(config.selectors.controls);
            controlsPrevious = $(config.selectors.controlsPrevious);
            controlsNext = $(config.selectors.controlsNext);
            textOverlayToggle = $(config.selectors.textOverlayToggle);
            currentItem = 0;
            itemClickedText = '';
            itemClickedHref = '';
            videos = [];
            touchDevice = ('ontouchstart' in document.documentElement);
        });

        describe('desktop events', function () {
            beforeEach(function () {
                jasmine.clock().install();
                loadFixtures('carousel.html');
                Carousel.init();
            });

            afterEach(function () {
                jasmine.clock().uninstall();
            });

            it ('should change active panel on click of a panel title', function() {
                let itemToClick = $('#carousel-item-2');
                spyOn(Carousel, 'transition').and.callThrough();
                
                itemToClick.trigger('click');

                expect(itemToClick.hasClass('carousel-item--active')).toBe(true);
                expect(Carousel.transition).toHaveBeenCalled();
                expect(Carousel.transition).toHaveBeenCalledWith(itemToClick[0], 'none');
            });

            it ('should NOT change active panel on click of a link within a panel', function () {
                let linkToClick = $('#carousel-item-1').find('a');
                spyOn(Carousel, 'transition');
                
                linkToClick.trigger('click');

                expect(Carousel.transition).not.toHaveBeenCalled();
            });

            it ('should allow the user to toggle the display of an overlay', function () {
                let overlayToggleToClick = $('#carousel-item-1').find('.carousel-item__stage-text-toggle');

                overlayToggleToClick.trigger('click');
                jasmine.clock().tick(1100);

                expect(overlayToggleToClick.hasClass('carousel-item__stage-text-toggle--closed')).toBe(true);

                /* inverse case: open a closed overlay */
                overlayToggleToClick.trigger('click');
                jasmine.clock().tick(1100);

                expect(overlayToggleToClick.hasClass('carousel-item__stage-text-toggle--closed')).toBe(false);
            });

            it ('should show/hide an article teaser on hover of a carousel item title', function () {
                let itemToHoverOver = $('#carousel-item-2');
                let itemToHoverOverSupport = itemToHoverOver.find('.carousel-item__support');

                itemToHoverOverSupport.trigger('mouseover');

                expect(itemToHoverOver.hasClass('carousel-item--hover')).toBe(true);

                itemToHoverOverSupport.trigger('mouseout');

                expect(itemToHoverOver.hasClass('carousel-item--hover')).toBe(false);
            });

            it ('should NOT change the display of an active carousel item on hover', function () {
                let itemToHoverOver = $('.carousel-item--active');
                let itemToHoverOverSupport = itemToHoverOver.find('.carousel-item__support');

                itemToHoverOverSupport.trigger('mouseover');

                expect(itemToHoverOver.hasClass('carousel-item--hover')).toBe(false);
            });

            // a compromise for not being able to test keypress events directly (security),
            // test the function keypresses fire instead
            it ('KEYBOARD NAV should trigger a click on press of enter or space on a supplied button/element', function () {
                let buttonToPress = $('#carousel-item-2').find('.carousel-item__support');

                function keypressEvent (keyCode) {
                    var event = document.createEvent('Event');
                    event.keyCode = keyCode; // Deprecated, prefer .key instead.
                    event.key = keyCode;
                    event.initEvent('keydown');
                    document.dispatchEvent(event);

                    return event;
                }

                spyOn(buttonToPress, 'click');
                Carousel.keypressToClick(buttonToPress, keypressEvent(13));
                expect(buttonToPress.click.calls.count()).toEqual(1);

                Carousel.keypressToClick(buttonToPress, keypressEvent(32));
                expect(buttonToPress.click.calls.count()).toEqual(2);

                Carousel.keypressToClick(buttonToPress, keypressEvent(1));
                expect(buttonToPress.click.calls.count()).toEqual(2);
            });
        });

        describe('mobile events', function () {
            beforeEach(function () {
                loadFixtures('carousel.html');
                Carousel.init();
            });

            it ('should go to the next carousel item on click of the "next" button', function () {
                let nextButton = $('.carousel-controls__link--next');
                spyOn(Carousel, 'transition');
                let expectedItem = document.getElementById('carousel-item-2');

                nextButton.trigger('click');

                expect(Carousel.transition).toHaveBeenCalledWith(expectedItem, 'right');
            });

            it ('should go to the previous carousel item on click of the "previous" button', function () {
                let prevButton = $('.carousel-controls__link--previous');
                spyOn(Carousel, 'transition');
                let expectedItem = document.getElementById('carousel-item-3');

                prevButton.trigger('click');

                expect(Carousel.transition).toHaveBeenCalledWith(expectedItem, 'left');
            });

            
        });

        describe ('mobile controls', function () {
            it ('should not show any controls on mobile if there is only one carousel item', function () {
                loadFixtures('carousel.html');
                $('#carousel-item-2, #carousel-item-3').remove();

                Carousel.init();

                expect($('.carousel-controls').length).toEqual(0);
            });

            it ('should populate "pip" navigation items', function () {
                loadFixtures('carousel.html');
                Carousel.init();

                expect($('.carousel-controls__link--pip').length).toEqual($('.carousel-item').length);
            });

            it ('should transition to a specific carousel item on press of a "pip" indicator', function () {
                loadFixtures('carousel.html');
                Carousel.init();

                let carouselItemToNavigateTo = $('#carousel-item-2');
                let pipToPress = $('.carousel-controls__link--pip')[1];
                spyOn(Carousel, 'transition');

                $(pipToPress).trigger('click');

                expect(Carousel.transition).toHaveBeenCalledWith(carouselItemToNavigateTo[0]);
            });
        });

        describe('GTM tracking', function () {

        });

        describe('panels with videos', function () {
            beforeEach(function () {
                loadFixtures('carousel.html');
                Carousel.init();
            });

            it ('should expand a YouTube video on click of "play"', function () {
                let stageToClick = $('#carousel-item-2').find('.carousel-item__stage');
                spyOn(Carousel, 'playVideo');

                stageToClick.trigger('click');

                expect(Carousel.playVideo).toHaveBeenCalled();
                expect(Carousel.playVideo.calls.count()).toEqual(1);

                /* inverse case - panel without video should not play a video */
                stageToClick = $('#carousel-item-1').find('.carousel-item__stage');

                stageToClick.trigger('click');
                expect(Carousel.playVideo.calls.count()).toEqual(1);
            });

            it ('should pause a playing YouTube video on transition to another pane', function () {
                // get a panel's video to play
                let itemToClick = $('#carousel-item-2');
                itemToClick.trigger('click');
                let stageToClick = $('#carousel-item-2').find('.carousel-item__stage');
                stageToClick.trigger('click');

                itemToClick = $('#carousel-item-1');

                spyOn(Carousel, 'pauseVideo');

                // now click the item
                itemToClick.trigger('click');
                expect(Carousel.pauseVideo).toHaveBeenCalled();
            });

            it ('should pause a playing YouTube video on click of the current panel\'s description', function () {
                // get a panel's video to play
                let itemToClick = $('#carousel-item-2');
                itemToClick.trigger('click');
                let stageToClick = $('#carousel-item-2').find('.carousel-item__stage');
                stageToClick.trigger('click');

                spyOn(Carousel, 'pauseVideo');

                // now click the item
                itemToClick.trigger('click');

                expect(itemToClick.hasClass('carousel-item--play')).toBe(false);
                expect(itemToClick.hasClass('carousel-item--pause')).toBe(true);
                expect(Carousel.pauseVideo).toHaveBeenCalled();
            });
        });
    });
});

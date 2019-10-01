// CAROUSEL COMPONENT

/* globals YT, window, document */

'use strict';

import Hammer from 'hammer';
import $ from 'jquery';
import 'jquery.dotdotdot';

const config = {
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

let carousel,
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

/**
 * Carousel object that gets returned.
 */
const carouselObject = {

    itemTitleClicked: false,

    init: function () {
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

        if (carousel.length === 0) {
            return false;
        }
        /**
         * Add class to indicate number of items.
         */
        carousel.addClass( 'carousel--' + items.length + 'items' );

        /**
         * Init the automatic ellipses
         */
        desc.dotdotdot({'watch':'window'});

        /**
         * Init mobile events
         */
        this.initMobileEvents();

        /**
         * Init desktop events
         */
        this.initDesktopEvents();

        /**
         * Init the controls on mobile.
         */
        this.initControls();

        /**
         * Init the videos API objects.
         */
        this.initVideos();

        this.addAnalyticsAttributesToCarouselContent();
    },


    forward: function() {
        this.itemTitleClicked = true;
        itemClickedText = 'Right Arrow';

        const numItems = items.length,
            next = (currentItem + 1) % numItems,
            nextItem = items[next];
        this.transition(nextItem, 'right');
    },


    backward: function() {
        this.itemTitleClicked = true;
        itemClickedText = 'Left Arrow';

        const numItems = items.length,
            next = (currentItem - 1 + numItems) % numItems,
            nextItem = items[next];
        this.transition(nextItem, 'left');
    },


    transition: function(toItem) {
        const currentItemElement = $(items[currentItem]),
            newIndex = items.index(toItem),
            feature_h_indexStr = 'feature-h-' +newIndex,
            feature_cta_indexStr = 'feature-cta-' +newIndex;

        //Add GTM functionality for carousel item title or item link
        if (this.itemTitleClicked) {
            window.dataLayer.push(
                {
                    'linktext': itemClickedText,
                    'event': feature_h_indexStr
                }
            );
        } else {
            window.dataLayer.push(
                {
                    'linktext': itemClickedText,
                    'href': itemClickedHref,
                    'event': feature_cta_indexStr
                }
            );
        }



        /**
         * Pause the current video playing (if it is)
         */
        this.pauseVideo(currentItem);

        /**
         * If transitioning to current item, then pause video if it's
         * currently playing.
         */
        if (newIndex === currentItem) {

            if (currentItemElement.hasClass('carousel-item--play')) {
                carousel.removeClass('carousel--play');
                currentItemElement.removeClass('carousel-item--play')
                    .addClass('carousel-item--pause');
            }
        }
        else {
            /**
             * Deactive current item
             */
            $(items[currentItem]).removeClass('carousel-item--active')
                .removeClass('carousel-item--play')
                .removeClass('carousel-item--pause');
            carousel.removeClass('carousel--play');

            /**
             * Activate new item
             */
            $(toItem).addClass('carousel-item--active');

            /**
             * Remove hover classes from the new item.
             */
            $(toItem).removeClass('carousel-item--hover');
            carousel.removeClass('carousel--hover');

            /**
             * Update the automatic ellipses
             */
            $(items[currentItem]).find(config.selectors.desc).dotdotdot({'watch':'window'});
            $(toItem).find(config.selectors.desc).trigger('destroy.dot');

            currentItem = newIndex;

            /**
             * Clear JS set height (e.g. from hover state)
             */
            support.css('height', '');

            this.addAnalyticsAttributesToCarouselContent();
        }

        this.transitionControls(newIndex);
    },

    initMobileEvents: function() {
        const that = this,
            carouselSwipe = new Hammer(carousel[0]);

        /**
         * click/tap events
         */
        controlsNext.click(function(){
            that.forward();
            return false;
        });
        controlsPrevious.click(function(){
            that.backward();
            return false;
        });

        /**
         * swipe events
         */
        carouselSwipe.on('swipeleft', function(){
            that.forward();
        });
        carouselSwipe.on('swiperight', function(){
            that.backward();
        });
    },

    initDesktopEvents: function() {
        const that = this;

        items.on('click', function (e) {
            e.stopPropagation();

            that.itemTitleClicked = true;
            itemClickedText = this.innerHTML;

            that.transition(this, 'none');
        });

        support.on('keydown', function(e) {
            window.keypress(e);
        });

        stage.on('keydown', function(e) {
            window.keypress(e);
        });

        items.find('a').on('click', function (e) {
            e.stopPropagation();

            that.itemTitleClicked = false;
            itemClickedText = this.innerHTML;
            itemClickedHref = e.target.href;
        });

        /**
         * Trigger video if available
         */
        stage.click(function(ev){
            const item = $(this).parent(),
                itemIndex = items.index(item),
                video = $(this).find(config.selectors.video);
            if ( video.length ) {
                // Show video
                carousel.addClass('carousel--play');
                item.addClass('carousel-item--play');
                that.playVideo(itemIndex);
            }
            ev.stopPropagation();
        });

        /**
         * Text overlay toggle button
         */
        textOverlayToggle.removeAttr('disabled');
        textOverlayToggle.click(function(){
            const slideDuration = 1100, /* Should sync with CSS transition */
                button = $(this),
                thisTextOverlay = $(this).parent().find(config.selectors.textOverlay),
                padRight = parseInt(thisTextOverlay.css('padding-right'),10),
                padLeft = parseInt(thisTextOverlay.css('padding-left'),10),
                toggleWidth = thisTextOverlay.width() + padRight + padLeft,
                toggleClass = 'carousel-item__stage-text-toggle--closed';

            if (!button.hasClass(toggleClass)) {
                thisTextOverlay.css('right', - toggleWidth);
                button.fadeOut();
                window.setTimeout(function(){
                    button.addClass(toggleClass).fadeIn().css('display', '');
                }, slideDuration);
            } else {
                thisTextOverlay.css('right', '');
                button.fadeOut();
                window.setTimeout(function(){
                    button.removeClass(toggleClass).fadeIn().css('display', '');
                }, slideDuration);
            }
        });

        /**
         * Expanding description on hover over... only if not a touch device.
         */
        if (!touchDevice) {
            support.hover(
                function(){
                    const thisItem = $(this).parent();

                    // Only perform transition state for non active items.
                    if (thisItem.hasClass('carousel-item--active')) {
                        return;
                    }

                    thisItem.addClass('carousel-item--hover');
                    carousel.addClass('carousel--hover');
                    desc.trigger('update.dot');
                },
                function() {
                    const thisItem = $(this).parent();

                    thisItem.removeClass('carousel-item--hover');
                    carousel.removeClass('carousel--hover');
                    desc.trigger('update.dot');
                }
            );
        }

    },

    keypressToClick: function (element, event) {
        let code = event.which || event.key;
        if (code === 13 || code === 32) {
            element.click();
        }
    },

    /**
     * ==================================================================
     * VIDEOS - initialise and interact with Google YouTube api.
     * ==================================================================
     */


    /**
     * Load Google api, and init video objects into a central config
     * for easy manipulation.
     */
    initVideos: function() {
        const videoEls = $(config.selectors.video),
            that = this;

        /**
         * Only progress if there are video elements.
         */
        if (videoEls.length === 0) {
            return;
        }

        videoEls.each(function() {
            let videoIframe = $(this).find('iframe');
            let videoUrl = videoIframe.attr('data-videoUrl');

            const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;

            let match;

            if (videoUrl) {
                match = videoUrl.match(regExp);
            }

            if (match) {
                videoIframe.attr('src', 'https://www.youtube-nocookie.com/embed/' + match[2] + '?enablejsapi=1&playsinline=1');
            }
        });

        /**
         * Load the IFrame Player API code asynchronously.
         */
        const tag = document.createElement('script');
        tag.src = 'https://www.youtube.com/iframe_api';
        const firstScriptTag = document.getElementsByTagName('script')[0];
        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

        /**
         * This function creates a YouTube player object for each video
         * in the videos array for use later. The index in the array is the
         * _item_ index and so the array may contain undefined elements.
         */
        window.onYouTubeIframeAPIReady = function() {

            videoEls.each(function(){
                const el = $(this),
                    iframe = el.find('iframe'),
                    item = el.parent().parent(),
                    itemIndex = items.index(item),
                    itemHasStageText = item.find(config.selectors.textOverlay).length > 0,
                    videoId = config.videoIdPrefix + itemIndex,
                    button = $(config.html.playbutton);

                if (itemHasStageText) {
                    button.addClass('carousel-item__play-button--stage-text-visible');
                }

                iframe.attr('id', videoId);

                videos[itemIndex] = new YT.Player(videoId, {
                    events: {
                        'onStateChange': that.onPlayerStateChange
                    }
                });

                /**
                 * Add video play button for
                 */
                el.after(button);
            });
        };

    },


    /**
     * Set video to start and play video.
     */
    playVideo: function(index) {
        if (videos[index]) {
            videos[index].seekTo(0);
            videos[index].playVideo();
        }
    },


    /**
     * Pause video with index given.
     */
    pauseVideo: function(index) {
        if (videos[index] && videos[index].pauseVideo && videos[index].pauseVideo) {
            videos[index].pauseVideo();
        }
    },

    /**
     * Fires when player changes state
     */
    onPlayerStateChange: function(event) {
        const nonPlayingStates = [-1, 0, 2, 5],
            iframe = $(event.target.f),
            item = iframe.parent().parent().parent().parent();

        /**
         * If video is in a playing state, then bring to foreground.
         */
        if ( nonPlayingStates.indexOf(event.data) === -1 ) {
            item.removeClass('carousel-item--pause')
                .addClass('carousel-item--play');
            carousel.addClass('carousel--play');
        }

        /**
         * If video has ended (0), then remove play state.
         */
        if (event.data === 0) {
            item.removeClass('carousel-item--play');
            carousel.removeClass('carousel--play');
        }


    },


    /**
     * ==================================================================
     * CONTROLS - for use on mobile
     * ==================================================================
     */


    /**
     * Initialise the controls for mobile.
     */
    initControls: function() {
        const that = this;

        if (items.length === 1) {
            controls.remove();
            $(config.selectors.controlsPlaceholder).remove();
            return;
        }

        items.each(function(itemIndex){
            const pip = $(config.html.pip);
            pip.find('button').attr('title', 'Slide ' + (itemIndex + 1));
            pip.find('button').click(function(ev){
                that.transition(items[itemIndex]);
                ev.stopPropagation();
            });
            controlsNext.parent().before(pip);
        });

        /**
         * Display the controls after stage of active item.
         */
        this.transitionControls(currentItem);
    },


    /**
     * Update the controls when transitioning to new item.
     */
    transitionControls: function(toItem) {
        const pips = $(config.selectors.controlsPip);

        /**
         * Remove the placeholder
         */
        $(config.selectors.controlsPlaceholder).remove();

        /**
         * Place the controls right after the stage.
         */
        $(items[toItem]).find(config.selectors.stage)
            .after( controls );

        /**
         * Highlight the correct pip
         */
        pips.removeClass('carousel-controls__link--pip-active');
        $(pips[toItem]).addClass('carousel-controls__link--pip-active');
    },

    addAnalyticsAttributesToCarouselContent: function () {
        // add data-gtm attribute to links in carousel content
        let itemIndex = 0;
        let fooItem = document.querySelector('.carousel-item--active');
        while (fooItem.previousElementSibling) {
            fooItem = fooItem.previousElementSibling;
            itemIndex = itemIndex + 1;
        }

        const links = fooItem.querySelectorAll('.carousel-item__desc a');

        links.forEach(function (link, linkIndex) {
            link.setAttribute('data-gtm', `carousel-item-desc-${itemIndex+1}-${linkIndex+1}`);
        });
    }

};

export default carouselObject;

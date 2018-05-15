jasmine.getFixtures().fixturesPath = 'base/test/fixtures';

define([
    'govscot/component.search-with-filters',
    'utils/dates'
], function(SearchWithFilters, dates) {
    'use strict';
    describe('"Search with filters" component', function() {

        let search = new SearchWithFilters();
        
        beforeEach(function () {
            loadFixtures('search-with-filters.html');
        });

        it ("should have a method to determine whether there is an active search", function () {
            let params;
            
            // no params
            expect(search.hasActiveSearch(params)).toBe(false);

            // search term param
            params = {term: 'foo'};
            expect(search.hasActiveSearch(params)).toBe(true);

            // unrecognised param
            params = {no: 'foo'};
            expect(search.hasActiveSearch(params)).toBe(false);

            // empty param
            params = {term: ''};
            expect(search.hasActiveSearch(params)).toBe(false);

            // topic param
            params = {topics: ['Economy']};
            expect(search.hasActiveSearch(params)).toBe(true);

            // publication type param
            params = {publicationTypes: ['Consultation paper']};
            expect(search.hasActiveSearch(params)).toBe(true);

            // date param
            params = {date: {begin: '10/11/2012'}};
            expect(search.hasActiveSearch(params)).toBe(true);
            params = {date: {end: '10/11/2012'}};
            expect(search.hasActiveSearch(params)).toBe(true);
        });

        describe ('enable JavaScript filters', function () {
            it ('should convert radio button groups to checkbox groups', function () {
                let checkboxGroup = $('#topics-group');
                let radiosCount = checkboxGroup.find('input[type="radio"]').length;
                search.init();
                let checkboxesCount = checkboxGroup.find('input[type="checkbox"]').length;
                expect(checkboxesCount).toEqual(radiosCount);
            });

            it ('should populate multiple checkboxes when appropriate', function () {
                let checkboxGroup = $('#topics-group');
                let checkboxes = checkboxGroup[0].querySelectorAll('input[type="radio"]');
                checkboxes.forEach(function (checkbox) {
                    if (Math.round(Math.random()) === 1) {
                        checkbox.setAttribute('data-checkedonload', 'true');
                    }
                });

                let checkboxesToCheckOnLoad = checkboxGroup[0].querySelectorAll('[data-checkedonload="true"]');

                search.init();
                checkboxesToCheckOnLoad.forEach(function (checkbox) {
                    expect($(checkbox).prop('checked')).toBeTruthy();
                });
            });

            it ('should show date pickers', function () {
                let datePickers = $('.js-show-calendar');
                // check that we start hidden
                expect($(datePickers[0]).hasClass('hidden')).toBe(true);
                search.init();
                expect($(datePickers[0]).hasClass('hidden')).toBe(false);
            });

            it ('should hide manual filter buttons (on desktop)', function () {
                let filterButtons = $('#filter-actions');
                search.init();
                expect(filterButtons.hasClass('visible-xsmall')).toBe(true);
            });              
        });

        describe ('filter events', function () {
            beforeEach(function () {
                search.init();
                jasmine.clock().install();
            });

            afterEach(function () {
                jasmine.clock().uninstall();
            })

            it ('should repopulate the results on submit of the filters', function () {
                // NB: this also covers the search by term functionality

                // update URL

                // update results

                // update "clear"

                // hide filters
            });

            it ('should append a new page of results on click of "load more"', function () {
                spyOn(search, 'submitSearch');
                let paginationLink = $($('#search-results').find('a.pagination__page')[0]);
                paginationLink.click();
                expect(search.submitSearch).toHaveBeenCalled();
                expect(search.searchUtils.getNewQueryString(search.gatherParams())).toEqual('?page=2');
            });

            it ('should repopulate the results with the relevant page on click of a pagination link', function () {
                // i.e. submit the current search with a different `page` parameter

            });

            it ('should have "show filters" and "hide filters" features', function () {
                let filtersContainer = $('.filters-container');
                let mobileLayer = filtersContainer.closest('.mobile-layer');

                // show                
                $('.js-show-filters').trigger('click');
                
                expect(filtersContainer.hasClass('filters-container--open')).toBe(true);
                expect(mobileLayer.hasClass('mobile-layer--open')).toBe(true);

                // hide
                $('.js-cancel-filters').trigger('click');

                expect(filtersContainer.hasClass('filters-container--open')).toBe(false);
                expect(mobileLayer.hasClass('mobile-layer--open')).toBe(false);
            });

            it ('should submit the filters form on change of a filter checkbox', function () {
                spyOn(search, 'submitSearch');

                let checkboxToCheck = $('#test-checkbox');
                checkboxToCheck.trigger('click');

                // update params
                let params = search.gatherParams();
                expect(params.topics).toEqual(['Business, industry and innovation']);

                // submit search
                jasmine.clock().tick(1000);
                expect(search.submitSearch).toHaveBeenCalled();
            });

            it ('should clear the filters form on click of "clear"', function () {
                spyOn(search, 'submitSearch');
                let defaultParams = {term: '', date: {begin: '', end: ''}};
                
                // set some initial filter values
                search.searchParams = {
                    term: 'foo',
                    topics: ['Economy']
                };

                let checkboxToCheck = $('#test-checkbox');
                let termInput = $('#filters-search-term');
                checkboxToCheck.prop('checked', true);
                termInput.val('foo');
                
                $('.js-clear-filters').click();

                expect(search.gatherParams()).toEqual(defaultParams);
                expect(search.submitSearch).toHaveBeenCalled();
                expect(termInput.val()).toEqual('');
                expect($('#topics-group').find(':checked').length).toEqual(0);
            });
        });


        describe('date filter', function () {
            var dateFromInputGroup,
                dateFromInput,
                dateToInputGroup,
                dateToInput;

            beforeEach(function () {
                dateFromInputGroup = $($('.date-entry__input-group')[0]);
                dateFromInput = dateFromInputGroup.find('.date-entry__input');
                dateToInputGroup = $($('.date-entry__input-group')[1]);
                dateToInput = dateToInputGroup.find('.date-entry__input');

                search.init();
            });

            it('manual entry: should allow values formatted as dd/mm/yyyy', function () {
                dateFromInput.focus();
                dateFromInput.val('12/12/2012');
                dateFromInput.trigger('change');

                expect(dateFromInputGroup.hasClass('input-group--has-error')).toBeFalsy();
            });

            it('manual entry: should NOT allow values not formatted as dd/mm/yyyy', function () {
                dateFromInput.focus();
                dateFromInput.val('foo');
                dateFromInput.trigger('change');

                expect(dateFromInputGroup.hasClass('input-group--has-error')).toBeTruthy();
            });

            it('manual entry: should clear error messages after a successful date entry', function () {
                dateFromInput.focus();
                dateFromInput.val('foo');
                dateFromInput.trigger('change');

                expect(dateFromInputGroup.hasClass('input-group--has-error')).toBeTruthy();

                dateFromInput.focus();
                dateFromInput.val('12/12/2012');
                dateFromInput.trigger('change');

                expect(dateFromInputGroup.hasClass('input-group--has-error')).toBeFalsy();
            });

            it('date picker: should set dates in associated text inputs', function () {
                // get datepicker
                var datePicker = $('#date-from').closest('.date-entry').find('.pika-single');

                // click on an option
                var dateToPick = datePicker.find('td:not(.is-disabled) .pika-day:last');

                var expectedDate = dates.leadingZeroes(dateToPick.data('pika-day'), 2) + '/' + dates.leadingZeroes(dateToPick.data('pika-month') + 1, 2)  + '/' + dateToPick.data('pika-year').toString();
                search.dateFromPicker.setDate(expectedDate);

                window.setTimeout(function () {
                    expect($('#date-from').val()).toEqual(expectedDate)
                }, 200);
            });

            it('date picker: should trigger a search on DESKTOP', function () {
                search.settings.responsiveWidthThreshold = 0;

                spyOn(search, 'submitSearch');

                // get datepicker
                var datePicker = $('#date-from').closest('.date-entry').find('.pika-single');

                // click on an option
                var dateToPick = datePicker.find('td:not(.is-disabled) .pika-day:last');
                var expectedDate = dates.leadingZeroes(dateToPick.data('pika-day'), 2) + '/' + dates.leadingZeroes(dateToPick.data('pika-month') + 1, 2)  + '/' + dateToPick.data('pika-year').toString();

                search.dateFromPicker.setDate(expectedDate);

                window.setTimeout(function () {
                    expect(search.submitSearch).toHaveBeenCalled();
                }, 100);
            });

            it ('date picker: there should be a way of opening the date picker calendar', function () {
                $('.date-entry__calendar--open').removeClass('date-entry__calendar--open');

                var showButton = $('#date-from').closest('.date-entry').find('.js-show-calendar');
                showButton.trigger('click');

                expect(showButton.closest('.date-entry').find('.date-entry__calendar').hasClass('date-entry__calendar--open')).toBeTruthy();
            });

            it ('date picker: there should be a way of closing the date picker calendar', function () {
                // first open a calendar
                $('.date-entry__calendar--open').removeClass('date-entry__calendar--open');

                var showButton = $('.js-show-calendar:first');
                showButton.trigger('click');

                var closeButton = showButton.closest('.date-entry').find('.js-close-calendar');

                closeButton.trigger('click');
                expect($('.date-entry__calendar--open').length).toEqual(0);
            });

            it ('date picker: only one date picker calendar should be visible at a time', function () {
                $('.date-entry__calendar--open').removeClass('date-entry__calendar--open');

                var showButton1 = $('#date-from').closest('.date-entry').find('.js-show-calendar');
                showButton1.trigger('click');

                expect($('.date-entry__calendar--open').length).toEqual(1);

                var showButton2 = $('#date-to').closest('.date-entry').find('.js-show-calendar');
                showButton2.trigger('click');

                expect($('.date-entry__calendar--open').length).toEqual(1);
            });

            describe('date validation', function () {
                it ('should fail validation if an input date is an incorrect format', function () {
                    spyOn(search.searchUtils, 'addError');

                    var input = $('#date-from');
                    input.val('bananas');

                    var isValid = search.validateDateInput(input);

                    expect(isValid).toBeFalsy();
                    expect(search.searchUtils.addError).toHaveBeenCalled();
                });

                it ('should fail validation if the "from" date is later than the "to" date', function () {
                    spyOn(search.searchUtils, 'addError');

                    var inputFrom = $('#date-from');
                    inputFrom.val('10/11/2016');

                    var inputTo = $('#date-to');
                    inputTo.val('10/11/2015');

                    var isValid = search.validateDateInput(inputFrom);

                    expect(isValid).toBeFalsy();
                    expect(search.searchUtils.addError).toHaveBeenCalled();
                });
            });
        });
    });
});

define([
    'utils/dates'
], function(dates) {
    describe('"Dates" utils', function () {

        it ('should have a method to reformat a date from dd/mm/yyyy to mm/dd/yyyy', function () {
            expect(dates.translateDate('14/02/1979')).toEqual('02/14/1979');
        });
    });
});

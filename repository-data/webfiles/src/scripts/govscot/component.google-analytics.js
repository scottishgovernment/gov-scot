define([], function () {
    // sets the user type in the dataLayer
    $.get('/service/usertype', function (data) {
        window.dataLayer = window.dataLayer || [];
        window.dataLayer.userType = data.userType;
    });
});

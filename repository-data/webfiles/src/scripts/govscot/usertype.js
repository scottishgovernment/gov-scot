(function () {
    function setUserType(userType) {
        window.dataLayer[0].userType = userType;
        document.body.classList.add(`usertype-${userType}`);
    }

    document.body.classList.add('has-usertype');
    window.dataLayer = window.dataLayer || [{}];

    var xhr = new XMLHttpRequest();
    xhr.open('GET', '/service/usertype', true);

    xhr.timeout = 1000;

    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            var userType;
            if (xhr.status === 200) {
                var response = JSON.parse(xhr.responseText);
                userType = response.userType;
            } else {
                userType = 'error';
            }
            setUserType(userType);
            initGTM();
        }
    };

    xhr.ontimeout = function() {
        setUserType('timeout');
        initGTM();
    };

    xhr.send(null);
})();

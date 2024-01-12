'use strict';

import setInitialCookiePermissions from '../tools/set-initial-cookie-permissions';

const gtmScript = {
    init: function () {
        this.gtmScriptElement = document.getElementById('gtm-script');

        if (!this.gtmScriptElement) {
            return;
        }

        this.initDataLayer();
        this.initGTM();
    },

    initDataLayer: function () {
        const userType = this.gtmScriptElement.dataset.usertype;
        const format = this.gtmScriptElement.dataset.format;
        const siteid = this.gtmScriptElement.dataset.siteid;

        window.dataLayer = window.dataLayer || [];

        const obj = {};

        obj['gtm.whitelist'] = ['google', 'jsm', 'lcl'];

        function present(item) {
            return item && !!item.length;
        }

        if (present(userType)) {
            obj.userType = userType;
        }

        if (present(siteid)) {
            obj.siteid = siteid;
        }

        if (present(format)) {
            obj.format = format;
        }

        window.dataLayer.push(obj);
    },

    initGTM: function () {
        function getCookie(name) {
            const cookie = {};
            document.cookie.split(';').forEach(function (el) {
                const [k, v] = el.split('=');
                cookie[k.trim()] = v;
            });
            return cookie[name];
        }

        setInitialCookiePermissions();
        const cookiePermissions = getCookie('cookiePermissions');

        if (JSON.parse(atob(cookiePermissions)).statistics !== false) {
            const containerId = this.gtmScriptElement.dataset.containerid;
            const auth = this.gtmScriptElement.dataset.auth;
            const env = this.gtmScriptElement.dataset.env;

            let authString = '';
            let envString = '';

            if (auth && !!auth.length) {
                authString = `&gtm_auth=${auth}`;
            }

            if (env && !!env.length) {
                envString = `&gtm_preview=${env}&gtm_cookies_win=x`;
            }

            (function (w, d, s, l, i) {
                w[l] = w[l] || []; w[l].push({
                    'gtm.start':
                        new Date().getTime(), event: 'gtm.js'
                }); let f = d.getElementsByTagName(s)[0],
                    j = d.createElement(s), dl = l != 'dataLayer' ? '&l=' + l : ''; j.async = true; j.src =
                        'https://www.googletagmanager.com/gtm.js?id=' + i + dl + authString + envString; f.parentNode.insertBefore(j, f);
            })(window, document, 'script', 'dataLayer', containerId);
        }
    }
};

gtmScript.init();

export default gtmScript;
